package fr.eazyender.physicengine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.eazyender.physicengine.commands.CommandPhysicEngine;
import fr.eazyender.physicengine.links.Connector;
import fr.eazyender.physicengine.links.RigidConnector;
import fr.eazyender.physicengine.loops.VerletLoop;
import fr.eazyender.physicengine.nodes.Node;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author Zack Ribeiro
 *
 */
public class PhysicEngine  extends JavaPlugin{
	
	private static PhysicEngine main_instance;
	public static double dt;
	
	public static final String text_prefix = "§r§f§l[" + ChatColor.of(new Color(199, 236, 238)) + "§lSirius§r§f§l] :§r ";
	
	private static List<Node> nodes = new CopyOnWriteArrayList<Node>();
	private static List<Connector> connectors = new CopyOnWriteArrayList<Connector>();
	
	public static boolean isPaused = false;
	public static double engineSpeed = 1;
	
	@Override
	public void onEnable() 
	{
		main_instance = this;
		
		createPhysicEngine(CalculIntegration.VERLET, 20);
		
		getCommand("sirius").setExecutor(new CommandPhysicEngine());
		
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	/**
	 * @param integration : VERLET or EULER; Change the calculation method
	 * @param tps : Integer from 1 to 20; Refresh time/dt/How many time we calculate in one second
	 */
	public static void createPhysicEngine(CalculIntegration integration, int tps) {
		
		if(tps>20)tps=20;
		if(tps<1)tps=1;
		dt = 1/(double)tps;
		tps=21-tps;
		
		//Simulation Loop
		new BukkitRunnable() {
			public void run() {
			
				if(!isPaused)
				switch(integration) {
				case VERLET:
					VerletLoop.update();
					break;
				case EULER:
					break;
				default:
					VerletLoop.update();
					break;
				}
				
			}
		}.runTaskTimer(main_instance, 0, tps);
		
		//Auxiliar Loop
		new BukkitRunnable() {
			public void run() {
				if(DebugMod.drawingEnabled)DebugMod.draw();
			}
			
		}.runTaskTimer(main_instance, 0, 1);
		
	}
	
	public static enum CalculIntegration{
		VERLET, EULER;
	}
	
	public static void createNode(Node node) {nodes.add(node);}
	
	public static List<Node> getNodes(){return nodes;}
	
	public static boolean removeNode(Node node) {return nodes.remove(node);}
	
	public static List<Connector> getConnectors(){return connectors;}
	
	public static void createRigidConnector(RigidConnector connector) {connectors.add(connector);}
	
	public static PhysicEngine getPhysicEngine() {
		return main_instance;
	}

}
