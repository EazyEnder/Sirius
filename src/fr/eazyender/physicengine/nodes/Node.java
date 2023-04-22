package fr.eazyender.physicengine.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.PhysicalConstants;
import fr.eazyender.physicengine.events.NodeRenderEvent;
import fr.eazyender.physicengine.events.NodeTriggerEvent;
import fr.eazyender.physicengine.fields.Field;
import fr.eazyender.physicengine.fields.FieldProperties.NodeInteraction;
import fr.eazyender.physicengine.nodes.NodeProperties.ChargeInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.DragForce;
import fr.eazyender.physicengine.nodes.NodeProperties.FieldsInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.InteractMode;
import fr.eazyender.physicengine.nodes.NodeProperties.NodeRange;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerForce;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerForce.PlayerForce_Type;
import fr.eazyender.physicengine.quadtree.QuadTree;

/**
 * A node is the elementary component of the engine, it is on it that the forces will play.
 */
public class Node {
	
	private Location position;
	private Location old_position;
	private double mass;
	private NodeProperties properties;
	private String data;
	private NodeMaterial material; private Display render_entity;
	private double charge = 0;
	
	private List<UUID> player_list = new ArrayList<UUID>();
	
	//QuadTree or Node
	private Object host;
	
	private	List<Node> childs = new CopyOnWriteArrayList<Node>(); 
	
	private List<Field> custom_fields = new CopyOnWriteArrayList<Field>();
	
	
	public boolean isDeleted = false;
	
	public static double hitbox_radius = 0.15;
	
	
	/**
	 * A node is the elementary component of the engine, it is on it that the forces will play.
	 * 
	 * @param position : Position in the reference frame of its host (if host is QuadTree then it is the absolute position).
	 * @param init_velocity : Initial(t=0) velocity of the node 
	 * @param mass : Mass ~ weight on how forces will influence
	 * @param properties : Global properties, Check the class {@link NodeProperties}
	 */
	public Node(Location position, Vector init_velocity, double mass, NodeProperties properties) {
		this.position = position;
		if(init_velocity == null)init_velocity = new Vector(0,0,0);
		this.old_position = position.clone().add(init_velocity.multiply(-PhysicEngine.dt));
		this.mass = mass;
		this.properties = properties;
	}
	
	public static Node checkNodeUsingRay(Location location, Vector direction, double max_length, double delta) {
		Vector dir = direction.clone().normalize().multiply(delta);
		Vector result = new Vector(0,0,0);
		
		while(result.length() < max_length) {
			for (Node node : PhysicEngine.nodes.getNodes()) {
				if(node.getProperties().getInteract_attribute() == InteractMode.DISABLE) continue;
				if(location.distance(node.getPosition()) > max_length) continue;
				if(location.clone().add(result).distance(node.getPosition()) <= hitbox_radius) return node;
			}
			result.add(dir);
		}
		return null;
	}
	
	public boolean delete() {
		if((host instanceof QuadTree && ((QuadTree)host).getNodes().remove(this)) || (host instanceof Node && ((Node)host).getNodes().remove(this))) {
			for (Node child : childs) {child.delete();}
			if(render_entity != null) render_entity.remove();
			isDeleted = true;
			return true;
		}
		return false;
	}

	public void trigger(Object data) {
		Bukkit.getPluginManager().callEvent(new NodeTriggerEvent(this, getProperties().getTriggerSource(), data));
	}
	
	public Vector applyForces(Vector velocity) {
		Vector result = new Vector();
		
		List<Node> available_nodes = PhysicEngine.nodes.getNodes();
		if(properties.getNode_selection() != NodeRange.ALL && host != null) {
			if(host instanceof QuadTree) {
				//true bcs other options will come soon
				if(true || properties.getNode_selection() == NodeRange.QUADTREE_PARENT) {
					available_nodes = ((QuadTree)host).getNodes();
				}
			}else if(host instanceof Node) {
				available_nodes = ((Node)host).childs;
			}
		}
		
		//----------------GENERIC FORCES------------------
		if(properties.getGrav_force() == GravitationalForce.ENABLE) result.add(new Vector(0,-properties.getGrav_force().gravity_constant * mass,0));
		if(properties.getDrag_force() == DragForce.ENABLE) result.multiply(-properties.getDrag_force().drag_coef*velocity.clone().dot(velocity.clone()));
		
		//----------------GRAV FORCE BETWEEN NODES------------------
		if(properties.getGrav_influence() == GravitationalInfluence.ALL || properties.getGrav_influence() == GravitationalInfluence.IS_ATTRACTED) {
			for (Node node : available_nodes) {
				if(node == this) continue;
				if(node.getProperties().getGrav_influence() == GravitationalInfluence.DISABLE || node.getProperties().getGrav_influence() == GravitationalInfluence.IS_ATTRACTED)continue;
				Vector force = node.getPosition().clone().subtract(this.getPosition().clone()).toVector();
				force.normalize().multiply(properties.getGrav_influence().gravitationnal_constant * node.mass*mass);
				
				double distance_internodes = node.getPosition().distance(this.getPosition());
				if(distance_internodes > 50) continue;
				if(distance_internodes <= properties.getGrav_influence().min_distance) distance_internodes = properties.getGrav_influence().min_distance;
				result.add(force.multiply(1/Math.pow(distance_internodes,2)));
			}
		}
		
		//----------------ELEC CHARGE ------------------
		if(charge != 0 && (properties.getCharge_influence() == ChargeInfluence.ALL || properties.getCharge_influence() == ChargeInfluence.IS_ATTRACTED)) {
			for (Node node : available_nodes) {
				if(properties.getCharge_influence() == ChargeInfluence.DISABLE || properties.getCharge_influence() == ChargeInfluence.IS_ATTRACTED || node == this || node.charge == 0)continue;
				
				double q2 = node.charge;
				
				Vector force = node.getPosition().clone().subtract(this.getPosition().clone()).toVector();
				force.normalize().multiply((-charge)*q2);
				
				double distance_internodes = node.getPosition().distance(this.getPosition());
				if(distance_internodes > 50) continue;
				if(distance_internodes <=  properties.getCharge_influence().min_distance) distance_internodes = properties.getCharge_influence().min_distance;
				double coef = 1/Math.pow(distance_internodes,2);
				if(coef > properties.getCharge_influence().max_intensity) coef = properties.getCharge_influence().max_intensity;
				
				force.multiply(coef);
				result.add(force);
	
			}
		}
		
		//----------------PLAYER COLLISION------------------
		if(properties.getPlayer_collision() == PlayerCollision.ENABLE) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(player.getWorld() != this.getPosition().getWorld()) continue;
				if(player.getGameMode() == GameMode.SPECTATOR) continue;
				
				Location pos_player = player.getLocation().clone().add(0,1,0);
				if(pos_player.toVector().distance(calculateAbsolutePosition()) < properties.getPlayer_collision().player_hitbox_distance){
					Vector delta_hit = calculateAbsolutePosition().clone().subtract(pos_player.toVector());
					delta_hit.normalize().multiply(5*(1/pos_player.distance(this.position)));
					result.add(delta_hit);
				}
			}
		}
		
		//----------------PLAYER ATTRACTORS FORCES------------------
		if(!(properties.getPlayer_force() == PlayerForce.DISABLE || properties.getPlayer_force() == PlayerForce.STAY)) {
			List<Player> player_allowed = new ArrayList<Player>();
			
			for (Player player : this.getPosition().getWorld().getPlayers()) {
				if(properties.getPlayer_force() == PlayerForce.ALL) {
					player_allowed.addAll(this.getPosition().getWorld().getPlayers());
					break;
				}
				if(this.getPlayer_list().contains(player.getUniqueId())) {
					if(properties.getPlayer_force() == PlayerForce.WHITELIST)player_allowed.add(player);
				}else {
					if(properties.getPlayer_force() == PlayerForce.BLACKLIST)player_allowed.add(player);
				}
			}
			
			for (Player player : player_allowed) {
				if(properties.getPlayer_force().force_type == null)break;
				if(player.getGameMode() == GameMode.SPECTATOR) continue;
				
				Vector force = player.getLocation().clone().subtract(this.getPosition().clone()).toVector();
				
				double distance_internodes = player.getLocation().distance(this.getPosition());
				if(distance_internodes > properties.getPlayer_force().max_distance) continue;
				
				double coef = properties.getPlayer_force().intensity;
				
					if(distance_internodes <= properties.getPlayer_force().min_distance) distance_internodes = properties.getPlayer_force().min_distance;
					
					if(properties.getPlayer_force().force_type == PlayerForce_Type.INVERT_LINEAR) {
						coef = 1/Math.pow(distance_internodes,1);
					}else if(properties.getPlayer_force().force_type == PlayerForce_Type.INVERT_SQUARE) {
						coef = 1/Math.pow(distance_internodes,2);
					}else if(properties.getPlayer_force().force_type == PlayerForce_Type.LINEAR) {
						coef = Math.pow(distance_internodes,1);
					}else if(properties.getPlayer_force().force_type == PlayerForce_Type.SQUARE) {
						coef = Math.pow(distance_internodes,2);
					}
					
					if(coef > properties.getPlayer_force().max_intensity) coef = properties.getPlayer_force().max_intensity;
					
				force.normalize().multiply(coef);
				System.out.println(coef);
				result.add(force);
			}
		}
		
		//----------------FIELDS------------------
		List<Field> fields_selection = new ArrayList<Field>();
		if(getProperties().getField_influence() == FieldsInfluence.ALL) fields_selection.addAll(PhysicEngine.getFields());
		else if(getProperties().getField_influence() == FieldsInfluence.SELECTION && getCustom_fields() != null) fields_selection.addAll(getCustom_fields());
		for (Field field : fields_selection) {
			if(field.getProperties().getInteractWthNode() != NodeInteraction.FORCE) continue;
			result.add(field.getField().calc(position.toVector()));
		}
		
		return result;
	}
	
	public void render() {
		
		NodeRenderEvent event = new NodeRenderEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			if(render_entity != null) render_entity.remove();
			render_entity = null;
			return;
		}
		
		if(material == null)return;
		
		
		
		Location center = calculateAbsolutePosition().toLocation(this.getPosition().getWorld()).clone();
		
		
		if(material.particle_type != null) {
			if(material.particle_type == Particle.REDSTONE)this.position.getWorld().spawnParticle(material.particle_type, center , 1, 0D, 0D, 0D, 0, material.particle_option, true);
			else this.position.getWorld().spawnParticle(material.particle_type, center, 1, 0, 0, 0, 0, null, true);
		}
		
		if(render_entity == null) {
			if(Double.isNaN(center.getX())) {this.delete(); if(this.render_entity != null)render_entity.remove(); return;}
			if(material.texture instanceof BlockData) {
			render_entity = (BlockDisplay) center.getWorld().spawn(center, BlockDisplay.class, display -> {
			
				
				display.setGravity(false);
				display.setVelocity(getVelocity());
				display.setBlock((BlockData)material.texture);
				display.setBrightness(new Brightness(15, 15));
				display.setInvulnerable(true);
				display.setCustomNameVisible(false);
				
				display.setDisplayHeight(material.size);
				display.setDisplayWidth(material.size);
				if(material.glow != null) {
					display.setGlowColorOverride(material.glow);
					display.setGlowing(true);
				}
				
				Transformation transfo = new Transformation(new Vector3f(0,0,0),new AxisAngle4f (0,0,0,0),new Vector3f(material.size,material.size,material.size),new AxisAngle4f (0,0,0,0));
				
				display.setTransformation(transfo);
				
			});}
			else if(material.texture instanceof ItemStack) {
				render_entity = (ItemDisplay) center.getWorld().spawn(center, ItemDisplay.class, display -> {
					display.setGravity(false);
					display.setVelocity(getVelocity());
					display.setItemStack((ItemStack) material.texture);
					display.setBrightness(new Brightness(15, 15));
					display.setInvulnerable(true);
					display.setCustomNameVisible(false);
					
					display.setDisplayHeight(material.size);
					display.setDisplayWidth(material.size);
					if(material.glow != null) {
						display.setGlowColorOverride(material.glow);
						display.setGlowing(true);
					}
					
					Transformation transfo = new Transformation(new Vector3f(0,0,0),new AxisAngle4f (0,0,0,0),new Vector3f(material.size,material.size,material.size),new AxisAngle4f (0,0,0,0));
					
					display.setTransformation(transfo);
				});
			}
		return;
		}
		
		try {
			Vector direction = getVelocity().clone();
			if(direction.length() == 0) direction = new Vector(0,1,0);
			if(material.texture instanceof ItemStack) {render_entity.teleport(center.setDirection(direction.clone().multiply(-1)));}
			else render_entity.teleport(center.setDirection(direction));
			render_entity.setVelocity(getVelocity());
		}catch (Exception e) {
			this.delete();
		}
		
	}
	
	public Vector calculateOldAbsolutePosition() {
		Vector absolute_pos = this.getOldPosition().clone().toVector();
		Node runner = this;
		while(runner.host instanceof Node) {
			runner = (Node)runner.host;
			absolute_pos.add(runner.getOldPosition().toVector());
		}
		
		return absolute_pos;
	}
	
	public Vector calculateAbsolutePosition() {
		Vector absolute_pos = this.getPosition().clone().toVector();
		Node runner = this;
		while(runner.host instanceof Node) {
			runner = (Node)runner.host;
			absolute_pos.add(runner.getPosition().toVector());
		}
		
		return absolute_pos;
	}
	
	public Vector removeHostPositions(Vector vector) {
		Vector absolute_pos = vector.clone();
		Node runner = this;
		while(runner.host instanceof Node) {
			runner = (Node)runner.host;
			absolute_pos.subtract(runner.getPosition().toVector());
		}
		
		return absolute_pos;
	}
	
	public Vector removeHostOldPositions(Vector vector) {
		Vector absolute_pos = vector.clone();
		Node runner = this;
		while(runner.host instanceof Node) {
			runner = (Node)runner.host;
			absolute_pos.subtract(runner.getOldPosition().toVector());
		}
		
		return absolute_pos;
	}
	
	public boolean insertNode(Node node) {
		if(childs.contains(node))return false;
		boolean flag = childs.add(node);
		if(flag)node.host = this;
		return flag;
	}
	
	public List<Node> getNodes() {
		return childs;
	}
	
	public List<Node> getAllNodes(){
		List<Node> no = new CopyOnWriteArrayList<Node>();
		
		for (Node node : childs) {
			no.addAll(node.getAllNodes());
		}
		
		no.addAll(childs);
		
		return no;
	}
	
	public Object getHost() {
		return host;
	}

	public void setHost(Object host) {
		this.host = host;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Location getPosition() {
		return this.position;
	}

	public void setPosition(Location position) {
		this.position = position;
	}
	
	public Location getOldPosition() {
		return this.old_position;
	}

	public void setOldPosition(Location old_position) {
		this.old_position = old_position;
	}
	
	public Vector getVelocity() {
		return this.position.clone().toVector().subtract(this.old_position.clone().toVector()).multiply(1/PhysicEngine.dt);
	}

	public double getMass() {
		return this.mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public NodeProperties getProperties() {
		return this.properties;
	}

	public void setProperties(NodeProperties properties) {
		this.properties = properties;
	}

	public NodeMaterial getMaterial() {
		return material;
	}

	public void setMaterial(NodeMaterial material) {
		this.material = material;
	}

	public Display getRender_entity() {
		return render_entity;
	}

	public void setRender_entity(BlockDisplay render_entity) {
		this.render_entity = render_entity;
	}
	
	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	public List<UUID> getPlayer_list() {
		return player_list;
	}

	public void setPlayer_list(List<UUID> player_list) {
		this.player_list = player_list;
	}

	public List<Field> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(List<Field> custom_fields) {
		this.custom_fields = custom_fields;
	}
	
	
	
	
	
	
	
	
	

}
