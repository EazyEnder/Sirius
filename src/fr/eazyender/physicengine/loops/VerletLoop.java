package fr.eazyender.physicengine.loops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.links.Connector;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class VerletLoop {
	
	private static List<Node> nodes = PhysicEngine.getNodes();
	private static List<Connector> connectors = PhysicEngine.getConnectors();
	
	private static int numberOfConstraintsUpdates = 5;
	
	private static Map<Node,Location> old_positions = new HashMap<Node,Location>();
	private static Map<Node,Location> new_positions = new HashMap<Node,Location>();
	
	public static void update() {
		
		double dt = PhysicEngine.dt*PhysicEngine.engineSpeed;
		
		old_positions.clear();
		new_positions.clear();
		
		//Update nodes
		for (Node node : nodes) {
			
			if(node.getProperties().getStatic_prop() == Static.ENABLE)continue;

			
			Vector pos = node.getPosition().toVector();
			Vector old_pos = node.getOldPosition().toVector();
			Vector delta_pos = pos.clone().subtract(old_pos.clone());
			
			Vector acceleration = node.applyForces(delta_pos.clone().multiply(1/dt)).multiply(1/node.getMass());
			
			Vector new_pos = pos.clone().add(delta_pos.multiply(1).add(acceleration.multiply(dt*dt)));
			Location new_pos_location = new Location(node.getPosition().getWorld(), new_pos.getX(), new_pos.getY(), new_pos.getZ());
			
			//Trigger verification part
			switch(node.getProperties().getTriggerSource()) {
				case BLOCK:
					Block b = node.getPosition().getWorld().getBlockAt(new_pos_location);
					if(b != null && !b.getType().equals(Material.AIR)) {
						node.trigger();
					};
					break;
				default:
					break;
			}
			
			
			old_positions.put(node, pos.toLocation(node.getPosition().getWorld()));
			new_positions.put(node, new_pos_location);
			
		}
		
		for (Node node : old_positions.keySet()) {
			node.setOldPosition(old_positions.get(node));
			node.setPosition(new_positions.get(node));
		}
		
		old_positions.clear();
		new_positions.clear();
		
		//Update connectors
		for(int i = 0; i < numberOfConstraintsUpdates; i++) {
			for (Connector connector : connectors) {
				connector.update();
			}
		}
		
	}

}
