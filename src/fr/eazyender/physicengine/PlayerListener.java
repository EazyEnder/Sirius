package fr.eazyender.physicengine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.events.NodeInteractEvent;
import fr.eazyender.physicengine.nodes.Node;

public class PlayerListener implements Listener{
	
	@EventHandler
	public static void getRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Vector direction = player.getTargetBlock(null, 40).getLocation().toVector();
			Node node = Node.checkNodeUsingRay(player.getEyeLocation(), direction, 5, Node.hitbox_radius);
			if(node != null) {
				Bukkit.getPluginManager().callEvent(new NodeInteractEvent(player,node));
			}
		}
	}
	

}
