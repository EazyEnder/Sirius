package fr.eazyender.physicengine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.links.RigidConnector;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeMaterial;
import fr.eazyender.physicengine.nodes.NodeProperties;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class ObjectUtils {
	
	public static void createRigidLine(Location loc, int nbr_nodes, double inter_distance, Vector direction) {
		
		Location node_loc = loc.clone();
		
		NodeProperties props = new NodeProperties();
		props.setGrav_force(GravitationalForce.ENABLE);
		props.setStatic_prop(Static.DISABLE);
		props.setPlayer_collision(PlayerCollision.ENABLE);
		
		List<Node> nodes = new ArrayList<Node>();
		for (int i=0; i < nbr_nodes; i++) {
			Node node = new Node(node_loc.clone().add(direction.clone().normalize().multiply(inter_distance).multiply(i)), new Vector(0,0,0), 1, props);
			//node.setMaterial(new NodeMaterial(Bukkit.createBlockData(Material.BAMBOO_PLANKS), 0.25f));
			nodes.add(node);
			PhysicEngine.nodes.insert(node);
			if(i > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(i-1),nodes.get(i), 0.05));
		}
		nodes.clear();
		nodes = null;
	}
	
	public static void createRigidPlan(Location loc, int nbr_nodes, double inter_distance, Vector direction) {
		
		Location node_loc = loc.clone();
		Vector ortho = new Vector(-direction.getZ(),direction.getY(), direction.getX());
		
		NodeProperties props = new NodeProperties();
		props.setGrav_force(GravitationalForce.ENABLE);
		props.setStatic_prop(Static.DISABLE);
		props.setPlayer_collision(PlayerCollision.ENABLE);
		
		List<List<Node>> nodes = new ArrayList<List<Node>>();
		for (int i=0; i < (int)Math.sqrt(nbr_nodes); i++) {
			nodes.add(new ArrayList<Node>());
			for (int j=0; j < (int)Math.sqrt(nbr_nodes); j++) {
				Node node = new Node(node_loc.clone().add(direction.clone().normalize().multiply(inter_distance).multiply(j)).add(ortho.clone().normalize().multiply(inter_distance).multiply(i)), new Vector(0,0,0), 1, props);
				nodes.get(i).add(node);
				PhysicEngine.nodes.insert(node);
				if(j > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(i).get(j-1),nodes.get(i).get(j), 0.05));
				if(i > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(i-1).get(j),nodes.get(i).get(j), 0.05));
			}
		}
		
		nodes.clear();
		nodes = null;
	}
	
	public static void createRigidSolid(Location loc, int nbr_nodes, double inter_distance, Vector direction) {
		
		Location node_loc = loc.clone();
		Vector ortho = new Vector(-direction.getZ(),direction.getY(), direction.getX());
		Vector normal = new Vector(-direction.getY(),direction.getX(), direction.getZ()); 
		
		NodeProperties props = new NodeProperties();
		props.setGrav_force(GravitationalForce.ENABLE);
		props.setStatic_prop(Static.DISABLE);
		props.setPlayer_collision(PlayerCollision.ENABLE);
		
		List<List<List<Node>>> nodes = new ArrayList<List<List<Node>>>();
		for (int k=0; k < (int)Math.cbrt(nbr_nodes); k++) {
			nodes.add(new ArrayList<List<Node>>());
			for (int i=0; i < (int)Math.cbrt(nbr_nodes); i++) {
				nodes.get(k).add(new ArrayList<Node>());
				for (int j=0; j < (int)Math.cbrt(nbr_nodes); j++) {
					Node node = new Node(node_loc.clone().add(direction.clone().normalize().multiply(inter_distance).multiply(j)).add(ortho.clone().normalize().multiply(inter_distance).multiply(i)).add(normal.clone().normalize().multiply(inter_distance).multiply(k)), new Vector(0,0,0), 1, props);
					node.setCharge(1);
					nodes.get(k).get(i).add(node);
					PhysicEngine.nodes.insert(node);
					if(j > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(k).get(i).get(j-1),nodes.get(k).get(i).get(j), 0.05));
					if(i > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(k).get(i-1).get(j),nodes.get(k).get(i).get(j), 0.05));
					if(k > 0)PhysicEngine.createRigidConnector(new RigidConnector(nodes.get(k-1).get(i).get(j),nodes.get(k).get(i).get(j), 0.05));
					
				}
			}
		}
		
		nodes.clear();
		nodes = null;
	}
	

}
