package fr.eazyender.physicengine.links;

import org.bukkit.util.Vector;

import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class RigidConnector extends Connector {
	
	double distance;
	double precision;
	
	public RigidConnector(Node node1, Node node2, double precision) {
		super(node1, node2);
		this.distance =	Math.abs(node1.getPosition().toVector().clone().distance(node2.getPosition().toVector().clone()));
		this.precision = precision;
	}

	public RigidConnector(Node node1, Node node2, double distance, double precision) {
		super(node1, node2);
		this.distance = distance;
		this.precision = precision;
	}
	
	

	public double getPrecision() {
		return precision;
	}



	public void setPrecision(double precision) {
		this.precision = precision;
	}



	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	//------------------------------------------------------
	
	
	//Apply constraint
	@Override
	public void update() {
		
		
		Vector pos1 = node1.getPosition().toVector();
		Vector pos2 = node2.getPosition().toVector();	
		double d_internodes = Math.abs(pos2.distance(pos1));
		if(d_internodes <= this.distance + this.precision && d_internodes >= this.distance - this.precision) {return;}
		
		Vector pos1_updated = node1.getPosition().toVector().clone();
		Vector pos2_updated = node2.getPosition().toVector().clone();
		double delta = Math.abs(d_internodes-this.distance);
		if(d_internodes < this.distance) delta *= -1;
		
		if(node1.getProperties().getStatic_prop() == Static.DISABLE && node2.getProperties().getStatic_prop() == Static.DISABLE) {
			pos1_updated = pos1.clone().add(pos2.clone().subtract(pos1.clone()).normalize().multiply(delta/2));
			pos2_updated = pos2.clone().add(pos1.clone().subtract(pos2.clone()).normalize().multiply(delta/2));
		}else {
			if(node1.getProperties().getStatic_prop() == Static.ENABLE) {
				pos2_updated = pos2.clone().add(pos1.clone().subtract(pos2.clone()).normalize().multiply(delta));
			}else {
				pos1_updated = pos1.clone().add(pos2.clone().subtract(pos1.clone()).normalize().multiply(delta));
			}
		}
		
		node1.setPosition(pos1_updated.toLocation(node1.getPosition().getWorld()));
		node2.setPosition(pos2_updated.toLocation(node2.getPosition().getWorld()));
	}
	
	
}
