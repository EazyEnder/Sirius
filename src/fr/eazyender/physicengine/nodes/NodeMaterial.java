package fr.eazyender.physicengine.nodes;


import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

public class NodeMaterial {
	
	protected Object texture;
	protected float size;
	protected Color glow = null;
	protected Particle particle_type = null;
	protected Particle.DustOptions particle_option = new Particle.DustOptions(Color.WHITE, 0.5f);
	
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

	public Particle getParticle_type() {
		return particle_type;
	}

	public void setParticle_type(Particle particle_type) {
		this.particle_type = particle_type;
	}

	public Particle.DustOptions getParticle_option() {
		return particle_option;
	}

	public void setParticle_option(Particle.DustOptions particle_option) {
		this.particle_option = particle_option;
	}
	
	
	
	
	

}
