package fr.eazyender.physicengine.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.DebugMod;
import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.links.RigidConnector;
import fr.eazyender.physicengine.nodes.ChargedNode;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class CommandPhysicEngine  implements CommandExecutor {

	
	private static final String prefix = PhysicEngine.text_prefix;
	private static final String command_name = "/sirius";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {

		Player p = (Player) sender;
		
		if(args.length > 0) {
			
			if(args[0].equalsIgnoreCase("debug")) {
				if(args.length > 1) {
					if(args[1].toUpperCase().equalsIgnoreCase("ON")) {
						DebugMod.drawingEnabled = true;
						p.sendMessage(prefix+"Debug mod has been enabled.");
						
					}else if(args[1].toUpperCase().equalsIgnoreCase("OFF")) {
						DebugMod.drawingEnabled = false;
						p.sendMessage(prefix+"Debug mod has been disabled.");
					}else {
						p.sendMessage(prefix+"Usage : "+command_name+" debug {ON/OFF}");
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" debug {ON/OFF}");
				}
				
				
			}else if(args[0].equalsIgnoreCase("create")) {
				if(args.length > 1) {
					if(args[1].contentEquals("node")) {
						Location node_loc = p.getLocation();
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.ENABLE);
						props.setStatic_prop(Static.DISABLE);
						props.setPlayer_collision(PlayerCollision.ENABLE);
						Node node1 = new Node(node_loc, new Vector(0,0,0), 1, props);
						PhysicEngine.createNode(node1);
						
					}else if(args[1].contentEquals("1")) {
						Location node_loc = p.getLocation();
						Vector velocity = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize().multiply(3);
						
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.ENABLE);
						props.setStatic_prop(Static.DISABLE);
						props.setPlayer_collision(PlayerCollision.ENABLE);
						Node node1 = new Node(node_loc, velocity, 1, props);
						
						NodeProperties props2 = new NodeProperties();
						props2.setGrav_force(GravitationalForce.DISABLE);
						props2.setStatic_prop(Static.ENABLE);
						Node node2 = new Node(node_loc.clone().add(1,1,0), new Vector(0,0,0), 1, props2);
						
						Node node3 = new Node(node_loc.clone().add(-1,-1.25,0), velocity, 1, props);
						Node node4 = new ChargedNode(node_loc.clone().add(-2.5,-2,0), velocity, 1, props, 1);
						
						PhysicEngine.createNode(node1);
						PhysicEngine.createNode(node2);
						PhysicEngine.createNode(node3);
						PhysicEngine.createNode(node4);
						
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node2, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node3, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node4,node3, 0.05 ));
					}else if(args[1].contentEquals("2")) {
						Location node_loc = p.getLocation();
						Vector velocity = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize().multiply(3);
						
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.ENABLE);
						props.setStatic_prop(Static.DISABLE);
						props.setPlayer_collision(PlayerCollision.ENABLE);
						Node node1 = new Node(node_loc, velocity, 1, props);
						
						NodeProperties props2 = new NodeProperties();
						props2.setGrav_force(GravitationalForce.DISABLE);
						props2.setStatic_prop(Static.ENABLE);
						Node node2 = new Node(node_loc.clone().add(1,1,0), new Vector(0,0,0), 1, props2);
						
						Node node3 = new Node(node_loc.clone().add(-1,-1.25,0), velocity, 1, props);
						Node node4 = new ChargedNode(node_loc.clone().add(-2.5,-2,0), velocity, 1, props, -1);
						
						PhysicEngine.createNode(node1);
						PhysicEngine.createNode(node2);
						PhysicEngine.createNode(node3);
						PhysicEngine.createNode(node4);
						
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node2, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node3, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node4,node3, 0.05 ));
					}else if(args[1].contentEquals("3")) {
						Vector velocity = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize();
						Location node_loc = p.getLocation();
						
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.ENABLE);
						props.setStatic_prop(Static.DISABLE);
						props.setPlayer_collision(PlayerCollision.ENABLE);
						Node node1 = new Node(node_loc, new Vector(0,0,0) , 1, props);
						Node node2 = new Node(node_loc.clone().add(velocity.clone().normalize().multiply(1)), new Vector(0,0,0), 1, props);
						Node node3 = new Node(node_loc.clone().add(velocity.clone().normalize().multiply(2)), new Vector(0,0,0), 1, props);
						Node node4 = new Node(node_loc.clone().add(velocity.clone().normalize().multiply(3)), new Vector(0,0,0), 1, props);
						
						PhysicEngine.createNode(node1);
						PhysicEngine.createNode(node2);
						PhysicEngine.createNode(node3);
						PhysicEngine.createNode(node4);
						
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node2, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node1,node3, 0.05 ));
						PhysicEngine.createRigidConnector(new RigidConnector(node4,node3, 0.05 ));
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" create {node}");
				}
				
				
			}else if(args[0].equalsIgnoreCase("pause")) {
				if(PhysicEngine.isPaused) {
					p.sendMessage(prefix+"La simulation est déjà en pause. Faites "+command_name+" resume ,pour relancer la simulation.");
				}else {
					PhysicEngine.isPaused = true;
					p.sendMessage(prefix+"La simulation a été mise en pause.");
				}
			}else if(args[0].equalsIgnoreCase("resume")) {
				if(!PhysicEngine.isPaused) {
					p.sendMessage(prefix+"La simulation est déjà en route. Faites "+command_name+" pause ,pour mettre en pause la simulation.");
				}else {
					PhysicEngine.isPaused = false;
					p.sendMessage(prefix+"La simulation a été remise en route.");
				}
			}else if(args[0].equalsIgnoreCase("clear")) {
				PhysicEngine.getConnectors().clear();
				PhysicEngine.getNodes().clear();
				p.sendMessage(prefix+"Tous les objets ont été supprimés.");
			}else if(args[0].equalsIgnoreCase("speed")) {
				if(args.length > 1) {
					if(Double.parseDouble(args[1]) > 0) {
						PhysicEngine.engineSpeed = Double.parseDouble(args[1]);
						p.sendMessage(prefix+"La vitesse de la simu a été changée à : " + PhysicEngine.engineSpeed + ".");
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" speed {double}");
				}
			}
			else {
				p.sendMessage(prefix+"Usage : "+command_name+" {config/debug/create/pause/resume/clear/speed}");
			}
			
		}else {
			p.sendMessage(prefix+"Usage : "+command_name+" {config/debug/create/pause/resume/clear/speed}");
		}
		
		return false;
	}

}
