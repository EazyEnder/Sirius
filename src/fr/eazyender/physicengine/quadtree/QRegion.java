package fr.eazyender.physicengine.quadtree;

import org.bukkit.util.Vector;

import fr.eazyender.physicengine.nodes.Node;

public class QRegion {
	
	Vector center;
	double half_length;
	
	public QRegion(Vector center, double half_length) {
		this.center = center;
		this.half_length = half_length;
	}
	
	public boolean containsNode(Node node) {		
		Vector node_pos = node.getPosition().toVector();
		return node_pos.isInAABB(center.clone().add(new Vector(-half_length,0,-half_length)), center.clone().add(new Vector(+half_length,node.getPosition().getWorld().getLogicalHeight(),+half_length)));
	}

}
