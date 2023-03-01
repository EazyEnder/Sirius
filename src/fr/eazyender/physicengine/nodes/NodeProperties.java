package fr.eazyender.physicengine.nodes;

public class NodeProperties {
	
	private TriggerSource trigger_source = TriggerSource.BLOCK;
	private GravitationalForce grav_force = GravitationalForce.DISABLE;
	private DragForce drag_force = DragForce.DISABLE;
	private Static static_prop = Static.DISABLE;
	private PlayerCollision player_collision = PlayerCollision.DISABLE;
	
	public NodeProperties() {}
	
	public NodeProperties(TriggerSource trigger_source, GravitationalForce grav_force, DragForce drag_force) {
		this.trigger_source = trigger_source;
		this.grav_force = grav_force;
		this.drag_force = drag_force;
	}
	
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

}
