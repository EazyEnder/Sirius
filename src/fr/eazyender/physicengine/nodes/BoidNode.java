package fr.eazyender.physicengine.nodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;

public class BoidNode extends Node{

	private double visual_range = 30;
	private double avoid_factor = 0.5, min_distance = 1.5;
	private double max_velocity = 10;
	
	public BoidNode(Location position, Vector init_velocity, double mass, NodeProperties properties) {
		super(position, init_velocity, mass, properties);
	}
	
	public BoidNode(Location position, Vector init_velocity, double mass, NodeProperties properties, double visual_range, double avoid_factor, double min_distance, double max_velocity) {
		super(position, init_velocity, mass, properties);
		
		this.visual_range = visual_range;
		this.avoid_factor = avoid_factor;
		this.min_distance = min_distance;
		this.max_velocity = max_velocity;
	}
	
	@Override
	public Vector applyForces(Vector velocity) {
		Vector result = super.applyForces(velocity);
		
		List<BoidNode> closest_boids = new ArrayList<BoidNode>();
		for (Node node : PhysicEngine.nodes.getNodes()) {
			if(!(node instanceof BoidNode)) continue;
			if(node.getPosition().distance(this.getPosition()) > visual_range) continue;
			
			closest_boids.add((BoidNode)node);
		}
		
		Vector interaction = new Vector(0,0,0);
		for (BoidNode boid : closest_boids) {
			if(boid == this) continue;
			Vector delta = boid.getPosition().clone().subtract(this.getPosition()).toVector();
			double distance = delta.length();
			if(distance < 0.05) distance = 0.05;
			if(distance <= min_distance) { interaction.add(delta.clone().normalize().multiply(-1 * avoid_factor * (1/Math.pow(distance,2)))); continue;}
			
			interaction.add(delta.clone().normalize().multiply(distance));
			
		}
		if(interaction.length() > max_velocity) interaction.normalize().multiply(max_velocity);
		result.add(interaction);
		
		return result;
	}

	public double getVisual_range() {
		return visual_range;
	}

	public void setVisual_range(double visual_range) {
		this.visual_range = visual_range;
	}

	public double getAvoid_factor() {
		return avoid_factor;
	}

	public void setAvoid_factor(double avoid_factor) {
		this.avoid_factor = avoid_factor;
	}

	public double getMin_distance() {
		return min_distance;
	}

	public void setMin_distance(double min_distance) {
		this.min_distance = min_distance;
	}

	public double getMax_velocity() {
		return max_velocity;
	}

	public void setMax_velocity(double max_velocity) {
		this.max_velocity = max_velocity;
	}

	
	
	

}
