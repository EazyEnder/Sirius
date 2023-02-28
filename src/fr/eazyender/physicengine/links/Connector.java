package fr.eazyender.physicengine.links;

import fr.eazyender.physicengine.nodes.Node;

public class Connector {
	
	protected Node node1;
	protected Node node2;
	
	public Connector(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	public void update() {}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}
	
	
	

}
