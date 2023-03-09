package fr.eazyender.physicengine.quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.nodes.ChargedNode;
import fr.eazyender.physicengine.nodes.Node;

/**
 * 
 * The QuadTree is a way to organize the points no longer in a list but in a tree. <br />
 * To do this, simply create a 2*2 grid and each time a cell exceeds a certain number of nodes, it is subdivided into a 2*2 grid.
 *
 */
public class QuadTree {
	
	static final int QT_NODE_CAPACITY = 100;
	
	public QRegion region;
	QNode average_node;
	
	List<Node> nodes = new CopyOnWriteArrayList<Node>();
	
	QuadTree parent;
	QuadTree northWest, northEast, southWest, southEast;
	List<QuadTree> neighbors;
	
	public QuadTree(QRegion region) {
		this.region = region;
	}

	public boolean update(){
		
		//Update average nodes
		if(parent == null) {
			createAverageNode();
		}
		//Find neighbors
		neighbors = getNeighbors();
		
		if(northWest != null) {
			northWest.update();
			northEast.update();
			southWest.update();
			southEast.update();
		}
		
		return false;
	}
	
	public boolean insert(Node node) {
		
		if(!region.containsNode(node)) return false;
		
		if(nodes.size() < QT_NODE_CAPACITY) {
			node.setHost(this);
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
		
		System.out.println("[SIRIUS] Subdivise QuadTree : " + region.center);
		
		northWest = new QuadTree(new QRegion(region.center.clone().add(new Vector(-region.half_length/2, 0, -region.half_length/2)), region.half_length/2, region.world));
		northEast = new QuadTree(new QRegion(region.center.clone().add(new Vector(+region.half_length/2, 0, -region.half_length/2)), region.half_length/2, region.world));
		southWest = new QuadTree(new QRegion(region.center.clone().add(new Vector(-region.half_length/2, 0, region.half_length/2)), region.half_length/2, region.world));
		southEast = new QuadTree(new QRegion(region.center.clone().add(new Vector(+region.half_length/2, 0, region.half_length/2)), region.half_length/2, region.world));
		
		northWest.parent = this;
		northEast.parent = this;
		southWest.parent = this;
		southEast.parent = this;
		
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
		
		if(nodes.size() > 0)System.out.println("[SIRIUS] Error : " + nodes.size() +" nodes in the quadtree (" + this.region.center + ") are not in the sub trees.");
		
		
	}
	
	public void clearAllNodes() {
		if(northWest != null) {
			northWest.clearAllNodes();
			northEast.clearAllNodes();
			southWest.clearAllNodes();
			southEast.clearAllNodes();
		}
		this.nodes.clear();
	}
	
	/*
	 * QuadTree: direction = (1/-1/0,0,1/-1/0)
	 */
	public QuadTree getNeighbor(Vector direction){
		
		
		//Up
		QuadTree par = this.parent;
		Vector center_direction = par.region.center.clone().subtract(this.region.center);
		int iters = 0;
		while(iters < 100 && par != null && Math.abs(center_direction.angle(direction) % 2*Math.PI) >= Math.PI/2) {
			par = par.parent;
			if(par == null) break;
			center_direction = par.region.center.clone().subtract(this.region.center);
			iters++;
		}
		
		if(par == null)return null;
			
		direction.multiply(par.region.half_length*2);
		
		//Down
		iters = 0;
		while(iters < 100 && par.northWest != null && par.region.half_length != this.region.half_length) {
			double nw_dist = Math.abs(par.region.center.clone().add(par.northWest.region.center).distance(this.region.center.clone().add(direction)));
			double ne_dist = Math.abs(par.region.center.clone().add(par.northEast.region.center).distance(this.region.center.clone().add(direction)));
			double sw_dist = Math.abs(par.region.center.clone().add(par.southWest.region.center).distance(this.region.center.clone().add(direction)));
			double se_dist = Math.abs(par.region.center.clone().add(par.southEast.region.center).distance(this.region.center.clone().add(direction)));
			
			
			double min = Math.min(nw_dist, Math.min(ne_dist, Math.min(sw_dist, se_dist)));
			if(nw_dist <= min) {par = par.northWest;}
			else if(ne_dist <= min) {par = par.northEast;}
			else if(sw_dist <= min) {par = par.southWest;}
			else {par = par.southEast;}
			
			iters++;
		}
		
		return par;
	}
	
	
	/**
	 * @return A QuadTree list which contains : the {north_west/north_est/south_west/south_est} neighbor.
	 */
	public List<QuadTree> getNeighbors(){
		List<QuadTree> neighbors = new ArrayList<QuadTree>();
		//nw
		neighbors.add(getNeighbor(new Vector(-1,0,-1)));
		//ne
		neighbors.add(getNeighbor(new Vector(1,0,-1)));
		//sw
		neighbors.add(getNeighbor(new Vector(-1,0,1)));
		//se
		neighbors.add(getNeighbor(new Vector(1,0,1)));
		
		return neighbors;
	}
	
	public List<Node> getNodes() {
		List<Node> no = new ArrayList<Node>();
		
		if(northEast != null) {
			no.addAll(northWest.getNodes());
			no.addAll(northEast.getNodes());
			no.addAll(southWest.getNodes());
			no.addAll(southEast.getNodes());
		}
		no.addAll(nodes);
		
		return no;
	}
	
	public QNode createAverageNode() {
		
		if(northWest == null) {
			
			if(nodes.size() <= 0) return new QNode(region.center.toLocation(region.world), 0, 0);
			
			Location average_location = null;
			double average_mass = 0;
			double average_charge = 0;
			
			for (Node node : nodes) {
				if(average_location == null) average_location = node.getPosition().clone().multiply(node.getMass());
				else average_location.add(node.getPosition().clone().multiply(node.getMass()));
				
				average_mass += node.getMass();
				if(node instanceof ChargedNode) average_charge += ((ChargedNode)node).getCharge();
			}
			
			if(average_mass != 0) average_location.multiply(1/average_mass);
			average_mass /= nodes.size();
			average_charge /= nodes.size();
			
			return new QNode(average_location, average_mass, average_charge);
		
		}
		
		QNode nw_node = northWest.createAverageNode();
		QNode ne_node = northEast.createAverageNode();
		QNode sw_node = southWest.createAverageNode();
		QNode se_node = southEast.createAverageNode();
		
		double average_mass = (nw_node.getMass() + ne_node.getMass() + sw_node.getMass() + se_node.getMass());
		Location average_location = nw_node.getPosition().clone().multiply(nw_node.getMass())
				.add(ne_node.getPosition().clone().multiply(ne_node.getMass()))
				.add(sw_node.getPosition().clone().multiply(sw_node.getMass()))
				.add(se_node.getPosition().clone().multiply(se_node.getMass()));
		if(average_mass != 0)average_location.multiply(1/average_mass);
		average_mass = average_mass / 4;
		double average_charge = (nw_node.getCharge() + ne_node.getCharge() + sw_node.getCharge() + se_node.getCharge()) / 4;
		
		QNode a_node = new QNode(
				average_location, average_mass, average_charge
				);
		
		return a_node;
	}
	

}
