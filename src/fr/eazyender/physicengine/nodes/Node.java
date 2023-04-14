package fr.eazyender.physicengine.nodes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import fr.eazyender.physicengine.events.NodeTriggerEvent;
import fr.eazyender.physicengine.fields.Field;
import fr.eazyender.physicengine.fields.FieldProperties.NodeInteraction;
import fr.eazyender.physicengine.nodes.NodeProperties.ChargeInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.DragForce;
import fr.eazyender.physicengine.nodes.NodeProperties.FieldsInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.InteractMode;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;
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
	
	private QuadTree host;
	
	
	public boolean isDeleted = false;
	
	public static double hitbox_radius = 0.15;
	
	
	/**
	 * A node is the elementary component of the engine, it is on it that the forces will play.
	 * 
	 * @param position : Position in Minecraft
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
		if(host.getNodes().remove(this)) {
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
		
		if(properties.getGrav_force() == GravitationalForce.ENABLE) result.add(new Vector(0,-PhysicalConstants.gravity_constant * mass,0));
		if(properties.getDrag_force() == DragForce.ENABLE) result.multiply(-PhysicalConstants.DragCoef*velocity.clone().dot(velocity.clone()));
		
		if(properties.getGrav_influence() == GravitationalInfluence.ALL || properties.getGrav_influence() == GravitationalInfluence.IS_ATTRACTED) {
			for (Node node : PhysicEngine.nodes.getNodes()) {
				if(node == this) continue;
				if(node.getProperties().getGrav_influence() == GravitationalInfluence.DISABLE || node.getProperties().getGrav_influence() == GravitationalInfluence.IS_ATTRACTED)continue;
				Vector force = node.getPosition().clone().subtract(this.getPosition().clone()).toVector();
				force.normalize().multiply(PhysicalConstants.gravitationnal_constant * node.mass*mass);
				
				double distance_internodes = node.getPosition().distance(this.getPosition());
				if(distance_internodes > 50) continue;
				if(distance_internodes <= 0.1) distance_internodes = 0.1;
				result.add(force.multiply(1/Math.pow(distance_internodes,2)));
			}
		}
		
		if(charge != 0 && (properties.getCharge_influence() == ChargeInfluence.ALL || properties.getCharge_influence() == ChargeInfluence.IS_ATTRACTED)) {
			for (Node node : PhysicEngine.nodes.getNodes()) {
				if(properties.getCharge_influence() == ChargeInfluence.DISABLE || properties.getCharge_influence() == ChargeInfluence.IS_ATTRACTED || node == this || node.charge == 0)continue;
				
				double q2 = node.charge;
				
				Vector force = node.getPosition().clone().subtract(this.getPosition().clone()).toVector();
				force.normalize().multiply((-charge)*q2);
				
				double distance_internodes = node.getPosition().distance(this.getPosition());
				if(distance_internodes > 50) continue;
				if(distance_internodes <= 0.1) distance_internodes = 0.1;
				double coef = 1/Math.pow(distance_internodes,2);
				if(coef > 3) coef = 3;
				
				force.multiply(coef);
				result.add(force);
	
			}
		}
		
		if(properties.getPlayer_collision() == PlayerCollision.ENABLE) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(player.getWorld() != this.getPosition().getWorld()) continue;
				if(player.getGameMode() == GameMode.SPECTATOR) continue;
				
				Location pos_player = player.getLocation().clone().add(0,1,0);
				if(pos_player.distance(this.position) < 0.75){
					Vector delta_hit = this.position.clone().subtract(pos_player).toVector();
					delta_hit.normalize().multiply(5*(1/pos_player.distance(this.position)));
					result.add(delta_hit);
				}
			}
		}
		
		if(getProperties().getField_influence() == FieldsInfluence.ENABLE)
		for (Field field : PhysicEngine.getFields()) {
			if(field.getProperties().getInteractWthNode() != NodeInteraction.FORCE) continue;
			result.add(field.getField().calc(position.toVector()));
		}
		
		return result;
	}
	
	public void render() {
		
		if(material == null)return;
		
		Location center = position.clone();
		center.add(new Vector(-material.size/2,-material.size/2,-material.size/2));
		
		if(render_entity == null) {
			if(material.texture instanceof BlockData) {
			render_entity = (BlockDisplay) center.getWorld().spawn(position, BlockDisplay.class, display -> {
			
				
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
				render_entity = (ItemDisplay) center.getWorld().spawn(position, ItemDisplay.class, display -> {
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
		
		if(material.texture instanceof ItemStack) {render_entity.teleport(center.setDirection(getVelocity().clone().multiply(-1)));}
		else render_entity.teleport(center.setDirection(getVelocity()));
		render_entity.setVelocity(getVelocity());
		
	}
	
	
	
	public QuadTree getHost() {
		return host;
	}

	public void setHost(QuadTree host) {
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
	
	
	
	
	
	

}
