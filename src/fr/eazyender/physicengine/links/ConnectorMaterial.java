package fr.eazyender.physicengine.links;

import org.bukkit.block.data.BlockData;

public class ConnectorMaterial {
	
	BlockData texture;
	float size;
	
	public ConnectorMaterial(BlockData texture, float size) {
		
		this.texture = texture;
		this.size = size;
		
	}

	public BlockData getTexture() {
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
	
	

}

