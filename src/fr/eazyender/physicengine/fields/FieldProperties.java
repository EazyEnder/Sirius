package fr.eazyender.physicengine.fields;

/**
 * Group in one object that contains all the generic attributes of a {@link Field}
 * <br /> To use it, check the methods it contains and modify the properties.
 * <br />
 * <b>List of properties</b><br />
 * -{@link NodeInteraction} : How the field will change the trajectory of a node <br />
 */
public class FieldProperties {
	
	private NodeInteraction interactWthNode = NodeInteraction.DISABLE;
	
	public FieldProperties() {}
	
	
	
	//----------------------------------------------------------

	public NodeInteraction getInteractWthNode() {
		return interactWthNode;
	}

	public FieldProperties setInteractWthNode(NodeInteraction interactWthNode) {
		this.interactWthNode = interactWthNode;
		return this;
	}

	//----------------------------------------------------------


	public static enum NodeInteraction { 
		DISABLE, VELOCITY, FORCE
	}

}
