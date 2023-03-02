package fr.eazyender.physicengine.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.DebugMod;
import fr.eazyender.physicengine.ObjectUtils;
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
						
					}else if(args[1].contentEquals("rigid_line")) {
						if(args.length > 1+2) {
							int nbr_nodes = 1;
							try {
								nbr_nodes = Integer.parseInt(args[2]);
							} catch (Exception e) {
								p.sendMessage(prefix+"nbr_nodes doit être un entier !");
							}
							if(nbr_nodes < 1 ) {p.sendMessage(prefix+"nbr_nodes doit être supérieur à 1 !"); return false;}
							double interdist = 1.0;
							try {
								interdist = Double.parseDouble(args[3]);
							} catch (Exception e) {
								p.sendMessage(prefix+"interdist doit être un double !");
							}
							if(interdist <= 0 ) {p.sendMessage(prefix+"interdist ne peut pas être inférieur ou égale à 0 !"); return false;}
							
							Vector direction = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize();
							Location node_loc = p.getLocation();
							ObjectUtils.createRigidLine(node_loc, nbr_nodes, interdist, direction);
							
						}else {
							p.sendMessage(prefix+"Usage : "+command_name+" create rigid_line {nbr_nodes:Int} {interdist:Double}");
						}
					}else if(args[1].contentEquals("rigid_plan")) {
						if(args.length > 1+2) {
							int nbr_nodes = 1;
							try {
								nbr_nodes = Integer.parseInt(args[2]);
							} catch (Exception e) {
								p.sendMessage(prefix+"nbr_nodes doit être un entier !");
							}
							if(nbr_nodes < 1 ) {p.sendMessage(prefix+"nbr_nodes doit être supérieur à 1 !"); return false;}
							double interdist = 1.0;
							try {
								interdist = Double.parseDouble(args[3]);
							} catch (Exception e) {
								p.sendMessage(prefix+"interdist doit être un double !");
							}
							if(interdist <= 0 ) {p.sendMessage(prefix+"interdist ne peut pas être inférieur ou égale à 0 !"); return false;}
							
							Vector direction = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize();
							Location node_loc = p.getLocation();
							ObjectUtils.createRigidPlan(node_loc, nbr_nodes, interdist, direction);
							
						}else {
							p.sendMessage(prefix+"Usage : "+command_name+" create rigid_plan {nbr_nodes:Int} {interdist:Double}");
						}
					}else if(args[1].contentEquals("rigid_solid")) {
						if(args.length > 1+2) {
							int nbr_nodes = 1;
							try {
								nbr_nodes = Integer.parseInt(args[2]);
							} catch (Exception e) {
								p.sendMessage(prefix+"nbr_nodes doit être un entier !");
							}
							if(nbr_nodes < 1 ) {p.sendMessage(prefix+"nbr_nodes doit être supérieur à 1 !"); return false;}
							double interdist = 1.0;
							try {
								interdist = Double.parseDouble(args[3]);
							} catch (Exception e) {
								p.sendMessage(prefix+"interdist doit être un double !");
							}
							if(interdist <= 0 ) {p.sendMessage(prefix+"interdist ne peut pas être inférieur ou égale à 0 !"); return false;}
							
							Vector direction = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize();
							Location node_loc = p.getLocation();
							ObjectUtils.createRigidSolid(node_loc, nbr_nodes, interdist, direction);
							
						}else {
							p.sendMessage(prefix+"Usage : "+command_name+" create rigid_solid {nbr_nodes:Int} {interdist:Double}");
						}
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" create {node/rigid_line/rigid_plan/rigid_solid}");
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
