package fr.eazyender.physicengine.nodes;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;

public class ChargedNode extends Node{

	private double charge;
	
	public ChargedNode(Location position, Vector init_velocity, double mass, NodeProperties properties, double charge) {
		super(position, init_velocity, mass, properties);
		this.charge = charge;
		
	}
	
	@Override
	public Vector applyForces(Vector velocity) {
		Vector result = super.applyForces(velocity);
		
		for (Node node : PhysicEngine.nodes.getNodes()) {
			if(!(node instanceof ChargedNode) || node == this)continue;
			
			double q2 = ((ChargedNode)node).charge;
			
			Vector force = ((ChargedNode)node).getPosition().clone().subtract(this.getPosition().clone()).toVector();
			force.normalize().multiply((-charge)*q2);
			
			double coef = 1/Math.pow(((ChargedNode)node).getPosition().distance(this.getPosition()),2);
			if(coef > 3) coef = 3;
			
			force.multiply(coef);
			result.add(force);

		}
		
		return result;
	}

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}
	
	

}