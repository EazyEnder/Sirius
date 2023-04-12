package fr.eazyender.physicengine.links;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import fr.eazyender.physicengine.nodes.Node;

/** 
 * Connectors allow the user to simulate constraints, the simplest is a distance constraint (see {@link RigidConnector}). It also allows to apply a specific force between 2 nodes.
 */
public class Connector {
	
	protected Node node1;
	protected Node node2;
	protected ConnectorMaterial material; private BlockDisplay render_entity;
	
	public Connector(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	public void update() {
		
		if(node1.isDeleted || node2.isDeleted) {this.delete(); return;}
		
	}
	
	public void delete() {
		if(render_entity != null) render_entity.remove();
		
	}
	
	public void render() {
		if(material == null)return;

		Location center = node1.getPosition().clone();
		//center.add(new Vector(-material.size/2,-material.size/2,-material.size/2));
		
		if(render_entity == null) {render_entity = (BlockDisplay) center.getWorld().spawn(center, BlockDisplay.class, display -> {
			
			
			display.setGravity(false);
			display.setBlock(material.texture);
			display.setBrightness(new Brightness(15, 15));
			display.setInvulnerable(true);
			display.setCustomNameVisible(false);
			
			display.setDisplayHeight(material.size);
			display.setDisplayWidth(material.size);
			
			});
		return;
		}
		

					Vector3f pos = new Vector3f((float)(node1.getPosition().getBlockX()),
							(float)(node1.getPosition().getBlockY()), 
							(float)(node1.getPosition().getBlockZ()));
					Vector3f pos2 = new Vector3f((float)(node2.getPosition().getBlockX()),
							(float)(node2.getPosition().getBlockY()), 
							(float)(node2.getPosition().getBlockZ()));
//					
//					
//					
					Quaternionf orientation = new Quaternionf();
//					orientation = orientation.lookAlong(pos2.sub(pos),new Vector3f(0,1,0));
//					
//					Transformation transfo = new Transformation(new Vector3f(0,0,0),orientation,new Vector3f(material.size,(float)node1.getPosition().distance(node2.getPosition()),material.size),orientation);
					
					
		Transformation transfo = new Transformation(new Vector3f(0,0,0),orientation,new Vector3f(material.size,material.size,(float)node1.getPosition().distance(node2.getPosition())),orientation);
		render_entity.setTransformation(transfo);
		render_entity.teleport(center.clone().setDirection(node2.getPosition().toVector().clone().subtract(node1.getPosition().toVector())));
	}

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

	public ConnectorMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ConnectorMaterial material) {
		this.material = material;
	}

	public BlockDisplay getRender_entity() {
		return render_entity;
	}

	public void setRender_entity(BlockDisplay render_entity) {
		this.render_entity = render_entity;
	}
	
	
	
	
	
	

}
