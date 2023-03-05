package fr.eazyender.physicengine.quadtree;

import org.bukkit.Location;

public class QNode {
	
	private Location position;
	private double mass = 1;
	private double charge = 0;
	
	
	public QNode(Location position, double mass, double charge) {
		this.position = position;
		this.mass = mass;
		this.charge = charge;
	}


	public Location getPosition() {
		return position;
	}


	public void setPosition(Location position) {
		this.position = position;
	}


	public double getMass() {
		return mass;
	}


	public void setMass(double mass) {
		this.mass = mass;
	}


	public double getCharge() {
		return charge;
	}


	public void setCharge(double charge) {
		this.charge = charge;
	}
	
	
	
	

}
