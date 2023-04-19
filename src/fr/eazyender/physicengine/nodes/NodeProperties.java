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
 * -{@link ChargeInfluence} : Same as Grav<br />
 * -{@link FieldsInfluence} : If fields can move the node<br />
 * -{@link NodeRange} : What is the list of nodes that grav (influence) force / charge force (and other stuff that interacts with other nodes) interacts with.<br />
 * -{@link PlayerForce} : If a note is attracted by players. To change how the force is applied , change the "force_type" object in the enum<br />
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
	private ChargeInfluence charge_influence = ChargeInfluence.DISABLE;
	private FieldsInfluence field_influence = FieldsInfluence.DISABLE;
	private NodeRange node_selection = NodeRange.ALL;
	private PlayerForce player_force = PlayerForce.DISABLE;
	
	public NodeProperties() {}
	
//----------------------------------------------------------
	
	
	
	
	public TriggerSource getTriggerSource() {
		return trigger_source;
	}

	public FieldsInfluence getField_influence() {
		return field_influence;
	}

	public void setField_influence(FieldsInfluence field_influence) {
		this.field_influence = field_influence;
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
	
	public ChargeInfluence getCharge_influence() {
		return charge_influence;
	}

	public void setCharge_influence(ChargeInfluence charge_influence) {
		this.charge_influence = charge_influence;
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
	
	public PlayerForce getPlayer_force() {
		return player_force;
	}

	public void setPlayer_force(PlayerForce player_force) {
		this.player_force = player_force;
	}

	public NodeRange getNode_selection() {
		return node_selection;
	}

	public void setNode_selection(NodeRange node_selection) {
		this.node_selection = node_selection;
	}
	
	
	
//----------------------------------------------------------


	public static enum PlayerForce {
		ALL, WHITELIST, BLACKLIST, STAY, DISABLE;
		PlayerForce_Type force_type = PlayerForce_Type.CONSTANT;
		double intensity = 1;
		double max_intensity = 5;
		double min_distance = 0.05;
		// =0 for infinity
		double max_distance = 50;
		enum PlayerForce_Type {
			CONSTANT, INVERT_LINEAR, INVERT_SQUARE, LINEAR, SQUARE;
		}
			
	}
	
	public static enum NodeRange { 
		ALL, QUADTREE_NEIHBORS_AND_MEANNODES, QUADTREE_PARENT_AND_MEANNODES, QUADTREE_NEIGHBORS, QUADTREE_PARENT, NODE_PARENT;
	}

	public static enum TriggerSource { 
		TIMER, BLOCK, ENTITY;
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
	
	public static enum ChargeInfluence{
		DISABLE, ATTRACT_OTHER, IS_ATTRACTED, ALL;
	}
	
	public static enum FieldsInfluence{
		DISABLE, ENABLE;
	}

}
