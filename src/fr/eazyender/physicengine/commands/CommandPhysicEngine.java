package fr.eazyender.physicengine.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.DebugMod;
import fr.eazyender.physicengine.ObjectUtils;
import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.lang.LangManager;
import fr.eazyender.physicengine.nodes.BoidNode;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeMaterial;
import fr.eazyender.physicengine.nodes.NodeProperties;
import fr.eazyender.physicengine.nodes.NodeProperties.DragForce;
import fr.eazyender.physicengine.nodes.NodeProperties.FieldsInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalInfluence;
import fr.eazyender.physicengine.nodes.NodeProperties.NodeRange;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerCollision;
import fr.eazyender.physicengine.nodes.NodeProperties.PlayerForce;
import fr.eazyender.physicengine.nodes.NodeProperties.Static;

public class CommandPhysicEngine  implements CommandExecutor {

	
	private static final String prefix = PhysicEngine.text_prefix;
	private static final String command_name = "/sirius";
	public static boolean renderNode = true;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {

		Player p = (Player) sender;
		
		if(args.length > 0) {
			
			if(args[0].equalsIgnoreCase("debug")) {
				if(args.length > 1) {
					if(args[1].toUpperCase().equalsIgnoreCase("ON")) {
						DebugMod.drawingEnabled = true;
						p.sendMessage(prefix+LangManager.getText("CMD_DEBUGMOD_ENABLE"));
						
					}else if(args[1].toUpperCase().equalsIgnoreCase("OFF")) {
						DebugMod.drawingEnabled = false;
						p.sendMessage(prefix+LangManager.getText("CMD_DEBUGMOD_DISABLE"));
					}else if(args[1].toUpperCase().equalsIgnoreCase("RENDER")) {
						renderNode = !renderNode;
						if(renderNode) p.sendMessage(prefix+LangManager.getText("CMD_DEBUGMOD_RENDERON"));
						else p.sendMessage(prefix+LangManager.getText("CMD_DEBUGMOD_RENDEROFF"));
					}else {
						p.sendMessage(prefix+"Usage : "+command_name+" debug {ON/OFF/RENDER}");
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" debug {ON/OFF/RENDER}");
				}
				
			}
			else if(args[0].equalsIgnoreCase("lang")) {
				if(args.length > 1) {
						LangManager.lang = args[1];
						p.sendMessage(prefix+LangManager.getText("CMD_LANG_SWITCH"));
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" lang {id_lang}");
				}
				
				
			}else if(args[0].equalsIgnoreCase("create")) {
				if(args.length > 1) {
					if(args[1].contentEquals("field_node")) {
						NodeProperties props = new NodeProperties();
						props.setField_influence(FieldsInfluence.ALL);
						props.setDrag_force(DragForce.ENABLE);
						Node node = new Node(p.getLocation(),new Vector(0,0,0),1,props);
						
						node.setMaterial(new NodeMaterial(Bukkit.createBlockData(Material.BAMBOO_PLANKS), 0.5f));
						
						PhysicEngine.nodes.insert(node);
					}else if(args[1].contentEquals("layered_node")) {
						NodeProperties props = new NodeProperties();
						props.setPlayer_collision(PlayerCollision.ENABLE);
						BoidNode node = new BoidNode(p.getLocation(),new Vector(0,0,0),1,props);
						
						ItemStack model = new ItemStack(Material.RED_DYE);
						ItemMeta meta = model.getItemMeta();
						meta.setCustomModelData(1);
						model.setItemMeta(meta);
						
						NodeMaterial n_mat = new NodeMaterial(model, (float) (0.7f + Math.random()));
						NodeMaterial n_mat_child = new NodeMaterial(model, n_mat.getSize() * 0.55f);
						n_mat_child.setGlow(Color.MAROON);
						node.setMaterial(n_mat);
						NodeProperties props_child = new NodeProperties();
						props_child.setNode_selection(NodeRange.NODE_PARENT);
						props_child.setField_influence(FieldsInfluence.ALL);
						
						for (int i = 0; i < 4; i++) {
							Node node_child = new Node((new Vector(-0.1 - Math.random()*1.5, 0, -0.1 - Math.random()*1.5)).toLocation(p.getWorld()), new Vector(0,0,0), 1, props_child);
							node_child.setMaterial(n_mat_child);
							node.insertNode(node_child);
						}
						
						PhysicEngine.nodes.insert(node);
					}
					else if(args[1].contentEquals("boid_node")) {
						NodeProperties props = new NodeProperties();
						props.setPlayer_collision(PlayerCollision.ENABLE);
						props.setPlayer_force(PlayerForce.ALL);
						BoidNode node = new BoidNode(p.getLocation(),new Vector(0,0,0),1,props);
						
						ItemStack model = new ItemStack(Material.RED_DYE);
						ItemMeta meta = model.getItemMeta();
						meta.setCustomModelData(1);
						model.setItemMeta(meta);
						
						NodeMaterial n_mat = new NodeMaterial(model, (float) (0.4f + Math.random()));
						node.setMaterial(n_mat);
						n_mat.setParticle_type(Particle.ASH);
						
						PhysicEngine.nodes.insert(node);
					}
					else if(args[1].contentEquals("node_array")) {
						
						int nbr_nodes = 1;
						try {
							nbr_nodes = Integer.parseInt(args[2]);
						} catch (Exception e) {
							p.sendMessage(prefix+"nbr_nodes doit être un entier !");
						}
						if(nbr_nodes < 1 ) {p.sendMessage(prefix+"nbr_nodes doit être supérieur à 1 !"); return false;}
						double inter_distance = 1.0;
						try {
							inter_distance = Double.parseDouble(args[3]);
						} catch (Exception e) {
							p.sendMessage(prefix+"interdist doit être un double !");
						}
						if(inter_distance <= 0 ) {p.sendMessage(prefix+"interdist ne peut pas être inférieur ou égale à 0 !"); return false;}
						
						Location loc = p.getLocation();
						Location node_loc = loc.clone();
						Vector direction = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize();
						Vector ortho = new Vector(-direction.getZ(),direction.getY(), direction.getX());
						
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.DISABLE);
						props.setStatic_prop(Static.DISABLE);
						props.setGrav_influence(GravitationalInfluence.ALL);
						
						List<List<Node>> nodes = new ArrayList<List<Node>>();
						for (int i=0; i < (int)Math.sqrt(nbr_nodes); i++) {
							nodes.add(new ArrayList<Node>());
							for (int j=0; j < (int)Math.sqrt(nbr_nodes); j++) {
								Node node = new Node(node_loc.clone().add(direction.clone().normalize().multiply(inter_distance).multiply(j)).add(ortho.clone().normalize().multiply(inter_distance).multiply(i)), new Vector(0,0,0), 1, props);
								nodes.get(i).add(node);
								PhysicEngine.nodes.insert(node);
							}
						}
						
						nodes.clear();
						nodes = null;
						
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
					p.sendMessage(prefix+LangManager.getText("CMD_SIMU_ALREADY_PAUSED",new String[] {command_name}));
				}else {
					PhysicEngine.isPaused = true;
					p.sendMessage(prefix+LangManager.getText("CMD_SIMU_PAUSE"));
				}
			}else if(args[0].equalsIgnoreCase("resume")) {
				if(!PhysicEngine.isPaused) {
					p.sendMessage(prefix+LangManager.getText("CMD_SIMU_ALREADY_RESUMED",new String[] {command_name}));
				}else {
					PhysicEngine.isPaused = false;
					p.sendMessage(prefix+LangManager.getText("CMD_SIMU_RESUME"));
				}
			}else if(args[0].equalsIgnoreCase("clear")) {
				PhysicEngine.cleanEngine();
				p.sendMessage(prefix+LangManager.getText("CMD_CLEAR"));
			}else if(args[0].equalsIgnoreCase("speed")) {
				if(args.length > 1) {
					if(Double.parseDouble(args[1]) > 0) {
						PhysicEngine.engineSpeed = Double.parseDouble(args[1]);
						p.sendMessage(prefix+LangManager.getText("CMD_SIMU_SPEED",new String[] {String.valueOf(PhysicEngine.engineSpeed)}));
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
