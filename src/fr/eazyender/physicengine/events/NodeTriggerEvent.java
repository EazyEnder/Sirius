package fr.eazyender.physicengine.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.eazyender.physicengine.PhysicEngine;
import fr.eazyender.physicengine.nodes.Node;
import fr.eazyender.physicengine.nodes.NodeProperties.TriggerSource;

public class NodeTriggerEvent extends Event{
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private Node node;
	private TriggerSource trigger_source;
	private Object data;
	
	public NodeTriggerEvent(Node node, TriggerSource trigger_source, Object data) {
		this.node = node;
		this.trigger_source = trigger_source;
		this.data = data;
	}
	
	public void deleteNode(boolean delete_node) {
		PhysicEngine.removeNode(node);
	}
	
	public Node getNode() {
		return node;
	}

	public TriggerSource getTrigger_source() {
		return trigger_source;
	}

	public Object getData() {
		return data;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
