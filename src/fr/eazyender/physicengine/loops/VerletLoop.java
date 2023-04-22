package fr.eazyender.physicengine.loops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.PhysicalConstants;
import fr.eazyender.physicengine.fields.Field;
import fr.eazyender.physicengine.fields.FieldProperties.NodeInteraction;
import fr.eazyender.physicengine.lang.LangManager;
import fr.eazyender.physicengine.links.Connector;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties.FieldsInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.Ghost;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerForce;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class VerletLoop {
	
	private static List<Connector> connectors = PhysicEngine.getConnectors();
	
	private static int numberOfConstraintsUpdates = 5;
	
	private static Map<Node,Location> old_positions = new HashMap<Node,Location>();
	private static Map<Node,Location> new_positions = new HashMap<Node,Location>();
	
	public static void update() {
		
		double dt = PhysicEngine.dt*PhysicEngine.engineSpeed;
		
		old_positions.clear();
		new_positions.clear();
		
		
		
		//Update nodes
		for (Node node : PhysicEngine.nodes.getAllLayeredNodes()) {
			
			if(node.getProperties().getStatic_prop() == Static.ENABLE)continue;

			
			Vector pos = node.getPosition().toVector();
			Vector old_pos = node.getOldPosition().toVector();
			Vector delta_pos = pos.clone().subtract(old_pos.clone());
			
			Vector acceleration = node.applyForces(delta_pos.clone().multiply(1/dt)).multiply(1/node.getMass());
			
			Vector velocity_fields = new Vector(0,0,0);
			List<Field> fields_selection = new ArrayList<Field>();
			if(node.getProperties().getField_influence() == FieldsInfluence.ALL) fields_selection.addAll(PhysicEngine.getFields());
			else if(node.getProperties().getField_influence() == FieldsInfluence.SELECTION && node.getCustom_fields() != null) fields_selection.addAll(node.getCustom_fields());
			for (Field field : fields_selection) {
				if(field.getProperties().getInteractWthNode() != NodeInteraction.VELOCITY) continue;
				velocity_fields.add(field.getField().calc(pos));
				if(pos.distance(old_pos) > 0.05)velocity_fields.subtract(field.getField().calc(old_pos));
			}

			
			delta_pos.add(velocity_fields.multiply(dt));
			
			Vector new_pos = pos.clone().add(delta_pos.multiply(1).add(acceleration.multiply(dt*dt)));
			Location new_pos_location = new Location(node.getPosition().getWorld(), new_pos.getX(), new_pos.getY(), new_pos.getZ());
			
			//Trigger verification part / dont work for layered nodes
			switch(node.getProperties().getTriggerSource()) {
				case BLOCK:
					Block b = node.getPosition().getWorld().getBlockAt(new_pos_location);
					if(b != null && !b.getType().equals(Material.AIR)) {
						node.trigger(b);
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
		
		for (Node node : PhysicEngine.nodes.getAllLayeredNodes()) {
			repairNodePosition(node, 0.1, 2);
		}
		
		//Update connectors
		for(int i = 0; i < numberOfConstraintsUpdates; i++) {
			for (Connector connector : connectors) {
				connector.update();
				repairNodePosition(connector.getNode1(), 0.1, 2);
				repairNodePosition(connector.getNode2(), 0.1, 2);
			}
		}
		
		//Verif
		for (Node node : PhysicEngine.nodes.getAllLayeredNodes()) {
			
			if(node.getProperties().getPlayer_force() == PlayerForce.STAY) {
				if(!node.getPlayer_list().isEmpty()) {
					Player player = Bukkit.getPlayer(node.getPlayer_list().get(0));
					if(player != null) {
						node.setPosition(player.getLocation());
					}
				}
			}
			
			if(node.getVelocity().length() > 1000) {
				System.out.println("[SIRIUS]" + LangManager.getText("QT_ERROR_NODE_SPEED_LIMIT"));
				node.delete();
			}
			
			if(Double.isNaN(node.getPosition().getX()) || Double.isNaN(node.getPosition().getY()) || Double.isNaN(node.getPosition().getZ())) {
				System.out.println("[SIRIUS]" + LangManager.getText("QT_ERROR_NODE_POSITION_NAN"));
				node.delete();
			}
		}
		
		
		//Render
		for (Node node : PhysicEngine.nodes.getAllLayeredNodes()) {
			node.render();
		}
		for (Connector connector : connectors) {
			connector.render();
		}
		
		
	}
	
	private static void repairNodePosition(Node node, double precision, double max) {
		if(node.getProperties().getGhost_attribute() == Ghost.ENABLE) return;
		if(node.calculateAbsolutePosition().toLocation(node.getPosition().getWorld()).getBlock() == null || node.calculateAbsolutePosition().toLocation(node.getPosition().getWorld()).getBlock().getType() == Material.AIR)return;
		if(node.calculateAbsolutePosition().equals(node.calculateOldAbsolutePosition())) return;
		
		Vector exterior = node.calculateOldAbsolutePosition().clone().subtract(node.calculateAbsolutePosition().clone()).normalize();
		
		Location pos = node.calculateAbsolutePosition().toLocation(node.getPosition().getWorld()).clone();
		Vector ext = new Vector(0,0,0);
		int i = 0;
		while(pos.getBlock() != null && pos.getBlock().getType() != Material.AIR && i < 5/precision) {
			i++;
			ext.add(exterior.clone().normalize().multiply(precision));
			pos = node.calculateAbsolutePosition().toLocation(node.getPosition().getWorld()).clone().add(ext.toLocation(node.getPosition().getWorld()));
			if(ext.length() > node.calculateOldAbsolutePosition().clone().subtract(node.calculateAbsolutePosition().clone()).length() * max)break;
		}
		

		if(node.calculateOldAbsolutePosition().distance(pos.toVector()) <= PhysicalConstants.oscillation_cancel) {
			node.setPosition(node.getOldPosition());
			return;}
		
		node.setOldPosition(node.removeHostPositions(pos.clone().toVector()).toLocation(pos.getWorld()));
		node.setPosition(node.removeHostPositions(pos.clone().toVector()).toLocation(pos.getWorld()));
		
	}

}
