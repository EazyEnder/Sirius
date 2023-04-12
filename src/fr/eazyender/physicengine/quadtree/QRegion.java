package fr.eazyender.physicengine.quadtree;

import org.bukkit.World;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.nodes.Node;

public class QRegion {
	
	Vector center;
	double half_length;
	World world;
	
	public QRegion(Vector center, double half_length, World world) {
		this.center = center;
		this.half_length = half_length;
		this.world = world;
	}
	
	public boolean containsNode(Node node) {		
		Vector node_pos = node.getPosition().toVector();
		return node_pos.isInAABB(center.clone().add(new Vector(-half_length,-100,-half_length)), center.clone().add(new Vector(+half_length,1500,+half_length)));
	}
	
	public boolean containsVector(Vector vector) {		
		return vector.isInAABB(center.clone().add(new Vector(-half_length,-100,-half_length)), center.clone().add(new Vector(+half_length,1500,+half_length)));
	}

	public Vector getCenter() {
		return center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public double getHalf_length() {
		return half_length;
	}

	public void setHalf_length(double half_length) {
		this.half_length = half_length;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
	
	
	

}
