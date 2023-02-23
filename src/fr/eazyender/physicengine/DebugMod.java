package fr.eazyender.physicengine;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;

import fr.eazyender.physicengine.nodes.Node;

public class DebugMod {
	
	public static boolean drawingEnabled = true;
	private static List<Node> nodes = PhysicEngine.getNodes(); 
	
	
	public static void draw() {
		
		for (Node node : nodes) {
			
			World world = node.getPosition().getWorld();
			Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 1.0F);
			world.spawnParticle(Particle.REDSTONE, node.getPosition() , 5, 0D, 0D, 0D, 0, dustOptions, true);
			
			
		}
		
	}
	
	

}
