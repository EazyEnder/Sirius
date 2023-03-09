package fr.eazyender.physicengine.maths;

import org.bukkit.util.Vector;


/**
 * 
 * Store a 3D Field. <br />
 * Take a lambda function as argument. <br />
 * <b>Example</b> : new MathField((v) -> new double[] {v[0],-v[1],v[2]}); <br />
 * v[0] = x ; v[1] = y; v[2] = z => field result : (x,-y,z)
 */
public class MathField {
	
	MathFunction function = (v) -> new double[3];
	public MathField(MathFunction function) {
		this.function = function;
	}
	
	public Vector calc(Vector vec) {

		double[] coo = new double[] {vec.getX(),vec.getY(), vec.getZ()};
		if(coo.length != 3) return null;
		
		boolean flag = false;
		try {
			flag = function.calc(new double[3]).length != 3;
		} catch (Exception e) {
			flag = true;
		}
		if(flag) return null;
		
		double[] res = function.calc(coo);
		return new Vector(res[0],res[1],res[2]);
	}
	 

}