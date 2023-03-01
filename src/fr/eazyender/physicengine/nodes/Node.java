package fr.eazyender.physicengine.nodes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.nodes.NodeProperties.DragForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;

public class Node {
	
	private Location position;
	private Location old_position;
	private double mass;
	private NodeProperties properties;
	
	public Node(Location position, Vector init_velocity, double mass, NodeProperties properties) {
		this.position = position;
		this.old_position = position.clone().add(init_velocity.multiply(-PhysicEngine.dt));
		this.mass = mass;
		this.properties = properties;
	}
	
	public void trigger() {
		
		PhysicEngine.removeNode(this);
	}
	
	public Vector applyForces(Vector velocity) {
		Vector result = new Vector();
		
		if(properties.getGrav_force() == GravitationalForce.ENABLE) result.add(new Vector(0,-1 * mass,0));
		if(properties.getDrag_force() == DragForce.ENABLE) result.multiply(-0.1*velocity.clone().dot(velocity.clone()));
		
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
