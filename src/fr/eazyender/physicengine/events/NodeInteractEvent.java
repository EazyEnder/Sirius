package fr.eazyender.physicengine.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.eazyender.physicengine.nodes.Node;

public class NodeInteractEvent extends Event implements Cancellable{
	
	 private static final HandlerList HANDLERS = new HandlerList();
	 
	 private Player player;
	 private Node node;
	 private boolean isCancelled;
	 
	 public NodeInteractEvent(Player player, Node node) {
		 this.player = player;
		 this.node = node;
	 }

	 public Player getPlayer() {
		return player;
	}

	public Node getNode() {
		return node;
	}
	
	public void triggerNode() {
		node.trigger(null);
	}
	
	@Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }


	public static HandlerList getHandlerList() {
	      return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
