package fr.eazyender.physicengine.quadtree;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.nodes.ChargedNode;
import fr.eazyender.physicengine.nodes.Node;

public class QuadTree {
	
	static final int QT_NODE_CAPACITY = 20;
	
	QRegion region;
	QNode average_node;
	
	List<Node> nodes = new ArrayList<Node>();
	
	QuadTree northWest, northEast, southWest, southEast;
	
	public QuadTree(QRegion region) {
		this.region = region;
	}
	
	public boolean insert(Node node) {
		
		if(!region.containsNode(node)) return false;
		
		if(nodes.size() < QT_NODE_CAPACITY) {
			nodes.add(node);
			return true;
		}
		
		if(northWest == null) subdivide();
		
		if(northWest.insert(node)) return true;
		if(northEast.insert(node)) return true;
		if(southWest.insert(node)) return true;
		if(southEast.insert(node)) return true;
		
		return false;
		
	}
	
	public void subdivide() {
		northWest = new QuadTree(new QRegion(region.center.clone().subtract(new Vector(-region.half_length/2, 0, -region.half_length/2)), region.half_length/2));
		northEast = new QuadTree(new QRegion(region.center.clone().subtract(new Vector(+region.half_length/2, 0, -region.half_length/2)), region.half_length/2));
		southWest = new QuadTree(new QRegion(region.center.clone().subtract(new Vector(-region.half_length/2, 0, region.half_length/2)), region.half_length/2));
		southEast = new QuadTree(new QRegion(region.center.clone().subtract(new Vector(+region.half_length/2, 0, region.half_length/2)), region.half_length/2));
		
		for (Node node : nodes) {
			if(northWest.region.containsNode(node)) {
				northWest.insert(node);
				this.nodes.remove(node);
				continue;
			}
			if(northEast.region.containsNode(node)) {
				northEast.insert(node);
				this.nodes.remove(node);
				continue;
			}
			if(southWest.region.containsNode(node)) {
				southWest.insert(node);
				this.nodes.remove(node);
				continue;
			}
			if(southEast.region.containsNode(node)) {
				southEast.insert(node);
				this.nodes.remove(node);
				continue;
			}
		}
		
		if(nodes.size() > 0)System.out.println("Error : " + nodes.size() +" nodes in the quadtree (" + this.region.center + ") are not in the sub trees.");
		
		
	}
	
	public List<QuadTree> getNeighbors(){
		
		return null;
	}
	
	public QNode createAverageNode() {
		
		if(northWest == null) {
			
			if(nodes.size() <= 0) return new QNode(region.center.toLocation(null), 0, 0);
			
			Location average_location = null;
			double average_mass = 0;
			double average_charge = 0;
			
			for (Node node : nodes) {
				if(average_location == null) average_location = node.getPosition().clone();
				else average_location.add(node.getPosition());
				
				average_mass += node.getMass();
				if(node instanceof ChargedNode) average_charge += ((ChargedNode)node).getCharge();
			}
			
			average_mass /= 4;
			average_charge /= 4;
			
			return new QNode(average_location, average_mass, average_charge);
		
		}
		
		QNode nw_node = northWest.createAverageNode();
		QNode ne_node = northEast.createAverageNode();
		QNode sw_node = southWest.createAverageNode();
		QNode se_node = southEast.createAverageNode();
		
		Location average_location = nw_node.getPosition().clone().add(ne_node.getPosition()).add(sw_node.getPosition()).add(se_node.getPosition()).multiply(1/4);
		double average_mass = (nw_node.getMass() + ne_node.getMass() + sw_node.getMass() + se_node.getMass()) / 4;
		double average_charge = (nw_node.getCharge() + ne_node.getCharge() + sw_node.getCharge() + se_node.getCharge()) / 4;
		
		QNode a_node = new QNode(
				average_location, average_mass, average_charge
				);
		
		return a_node;
	}
	

}
