package fr.eazyender.physicengine.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.DebugMod;
import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties;
import fr.eazyender.physicengine.nodes.NodeProperties.GravitationalForce;

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
						Vector velocity = p.getTargetBlock(null, 40).getLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize().multiply(3);
						
						NodeProperties props = new NodeProperties();
						props.setGrav_force(GravitationalForce.ENABLE);
						Node node = new Node(node_loc, velocity, 1, props);
						
						PhysicEngine.createNode(node);
					}
				}else {
					p.sendMessage(prefix+"Usage : "+command_name+" create {node}");
				}
				
				
			}else {
				p.sendMessage(prefix+"Usage : "+command_name+" {config/debug/create}");
			}
			
		}else {
			p.sendMessage(prefix+"Usage : "+command_name+" {config/debug/create}");
		}
		
		return false;
	}

}
