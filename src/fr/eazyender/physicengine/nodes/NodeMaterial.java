package fr.eazyender.physicengine.nodes;


import org.bukkit.Color;
import org.bukkit.block.data.BlockData;

public class NodeMaterial {
	
	Object texture;
	float size;
	Color glow = null;
	
	//BlockData for BlockDisplay or ItemStack for ItemDisplay
	public NodeMaterial(Object texture, float size) {
		
		this.texture = texture;
		this.size = size;
		
	}

	public Object getTexture() {
		return texture;
	}

	public void setTexture(BlockData texture) {
		this.texture = texture;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Color getGlow() {
		return glow;
	}

	public void setGlow(Color glow) {
		this.glow = glow;
	}
	
	
	
	

}
