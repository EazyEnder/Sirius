package fr.eazyender.physicengine.nodes;

/**
 * Group in one object that contains all the generic attributes of a {@link Node}
 * <br /> To use it, check the methods it contains and modify the properties.
 * <br />
 * <b>List of properties</b><br />
 * -{@link TriggerSource} : On what condition/object the node will trigger, use {@link NodeTriggerEvent} to get it <br />
 * -{@link GraviationalForce} : If gravity is on<br />
 * -{@link DragForce} : If the drag force is on<br />
 * -{@link Static} : If the node don't move / is anchored<br />
 * -{@link PlayerCollision} : If a player can move the nodes when he collides with it<br />
 * -{@link Ghost} : If the node go through block <br />
 * -{@link InteractMode} : How the {@link NodeInteractEvent} will be triggered<br />
 * -{@link GravitationalInfluence} : If the node is attracted by other nodes and/or if the node can attract others using her mass<br />
 */
public class NodeProperties {
	
	private TriggerSource trigger_source = TriggerSource.TIMER;
	private GravitationalForce grav_force = GravitationalForce.DISABLE;
	private DragForce drag_force = DragForce.DISABLE;
	private Static static_prop = Static.DISABLE;
	private PlayerCollision player_collision = PlayerCollision.DISABLE;
	private Ghost ghost_attribute = Ghost.DISABLE;
	private InteractMode interact_attribute = InteractMode.DISABLE;
	private GravitationalInfluence grav_influence = GravitationalInfluence.DISABLE;
	
	public NodeProperties() {}
	
//----------------------------------------------------------
	
	public TriggerSource getTriggerSource() {
		return trigger_source;
	}

	public Static getStatic_prop() {
		return static_prop;
	}

	public void setStatic_prop(Static static_prop) {
		this.static_prop = static_prop;
	}

	public void setTriggerSource(TriggerSource trigger_source) {
		this.trigger_source = trigger_source;
	}

	public GravitationalForce getGrav_force() {
		return grav_force;
	}

	public void setGrav_force(GravitationalForce grav_force) {
		this.grav_force = grav_force;
	}

	public DragForce getDrag_force() {
		return drag_force;
	}

	public void setDrag_force(DragForce drag_force) {
		this.drag_force = drag_force;
	}
	
	public InteractMode getInteract_attribute() {
		return interact_attribute;
	}

	public void setInteract_attribute(InteractMode interact_attribute) {
		this.interact_attribute = interact_attribute;
	}

	public Ghost getGhost_attribute() {
		return ghost_attribute;
	}

	public void setGhost_attribute(Ghost ghost_attribute) {
		this.ghost_attribute = ghost_attribute;
	}
	
	

	public GravitationalInfluence getGrav_influence() {
		return grav_influence;
	}

	public void setGrav_influence(GravitationalInfluence grav_influence) {
		this.grav_influence = grav_influence;
	}

	public PlayerCollision getPlayer_collision() {
		return player_collision;
	}

	public void setPlayer_collision(PlayerCollision player_collision) {
		this.player_collision = player_collision;
	}
	
	
	
//----------------------------------------------------------


	public static enum TriggerSource { 
		TIMER, BLOCK, ENTITY
	}
	
	public static enum GravitationalForce {
		ENABLE, DISABLE;
	}
	
	public static enum DragForce {
		ENABLE, DISABLE;
	}
	
	public static enum Static{
		ENABLE, DISABLE;
	}
	
	public static enum PlayerCollision {
		ENABLE, DISABLE;
	}
	
	public static enum Ghost {
		ENABLE, DISABLE;
	}
	
	public static enum InteractMode {
		FREE, DISABLE;
	}
	
	public static enum GravitationalInfluence{
		DISABLE, ATTRACT_OTHER, IS_ATTRACTED, ALL;
	}

}
