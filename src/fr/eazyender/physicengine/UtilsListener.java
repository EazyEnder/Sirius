package fr.eazyender.physicengine;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.eazyender.physicengine.commands.CommandPhysicEngine;
import fr.eazyender.physicengine.events.NodeRenderEvent;

public class UtilsListener implements Listener{
	
	@EventHandler
	public static void getNodeRender(NodeRenderEvent event) {
		
		if(!CommandPhysicEngine.renderNode) event.setCancelled(true);
		
	}

}
