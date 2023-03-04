package fr.eazyender.physicengine.nodes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.PhysicalConstants;
import fr.eazyender.physicengine.events.NodeTriggerEvent;
import fr.eazyender.physicengine.nodes.NodeProperties.DragForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.InteractMode;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;

/**
 * A node is the elementary component of the engine, it is on it that the forces will play.
 */
public class Node {
	
	private Location position;
	private Location old_position;
	private double mass;
	private NodeProperties properties;
	private String data;
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
		this.old_position = position.clone().add(init_velocity.multiply(-PhysicEngine.dt));
		this.mass = mass;
		this.properties = properties;
	}
	
	public static Node checkNodeUsingRay(Location location, Vector direction, double max_length, double delta) {
		Vector dir = direction.clone().normalize().multiply(delta);
		Vector result = new Vector(0,0,0);
		
		while(result.length() < max_length) {
			for (Node node : PhysicEngine.getNodes()) {
				if(node.getProperties().getInteract_attribute() == InteractMode.DISABLE) continue;
				if(location.distance(node.getPosition()) > max_length) continue;
				if(location.clone().add(result).distance(node.getPosition()) <= hitbox_radius) return node;
			}
			result.add(dir);
		}
		return null;
	}

	public void trigger(Object data) {
		Bukkit.getPluginManager().callEvent(new NodeTriggerEvent(this, getProperties().getTriggerSource(), data));
	}
	
	public Vector applyForces(Vector velocity) {
		Vector result = new Vector();
		
		if(properties.getGrav_force() == GravitationalForce.ENABLE) result.add(new Vector(0,-PhysicalConstants.gravity_constant * mass,0));
		if(properties.getDrag_force() == DragForce.ENABLE) result.multiply(-0.1*velocity.clone().dot(velocity.clone()));
		
		if(properties.getGrav_influence() == GravitationalInfluence.ALL || properties.getGrav_influence() == GravitationalInfluence.IS_ATTRACTED) {
			for (Node node : PhysicEngine.getNodes()) {
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
		
		if(properties.getPlayer_collision() == PlayerCollision.ENABLE) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(player.getWorld() != this.getPosition().getWorld()) continue;
				
				Location pos_player = player.getLocation().clone().add(0,1,0);
				if(pos_player.distance(this.position) < 0.75){
					Vector delta_hit = this.position.clone().subtract(pos_player).toVector();
					delta_hit.normalize().multiply((1/pos_player.distance(this.position)));
					result.add(delta_hit);
				}
			}
		}
		
		return result;
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
	
	
	
	

}
