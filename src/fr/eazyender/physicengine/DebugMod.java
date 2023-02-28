package fr.eazyender.physicengine;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.util.Vector;

import fr.eazyender.physicengine.links.Connector;
import fr.eazyender.physicengine.nodes.Node;

public class DebugMod {
	
	public static boolean drawingEnabled = true;
	private static List<Node> nodes = PhysicEngine.getNodes(); 
	private static List<Connector> connectors = PhysicEngine.getConnectors();
	
	
	public static void draw() {
		
		for (Node node : nodes) {
			
			World world = node.getPosition().getWorld();
			Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 0.8F);
			world.spawnParticle(Particle.REDSTONE, node.getPosition() , 5, 0D, 0D, 0D, 0, dustOptions, true);
			
			
		}
		
		for (Connector connector : connectors) {
			
			Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 0.3F);
			drawLineOfParticle(dustOptions, 0.2, connector.getNode1().getPosition(), connector.getNode2().getPosition());
			
		}
		
	}
	
	private static void drawLineOfParticle(DustOptions option, double len, Location target1, Location target2) {
		Location d1 = target1.clone();
		Location d2 = target2.clone();
		Vector t1 = d2.toVector().clone().subtract(d1.toVector()).normalize().multiply(0.2);
		double length = 0.0D;
		for (double k = 0; length < d1.distance(d2);) {
			Vector v0 = d1.toVector();
			for (double j = 0; j < k; j+=len) {
			v0.add(t1.clone().multiply(1/0.2).multiply(len));
			}
			target1.getWorld().spawnParticle(Particle.REDSTONE, v0.getX(),v0.getY(), v0.getZ() , 0, 0D, 0D, 0D, option);
			k += len;
			length += len;
		}
	}
	
	

}
