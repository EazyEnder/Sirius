package fr.eazyender.physicengine.fields;

import fr.eazyender.physicengine.maths.MathField;

/**
 * 
 * Fields allow customs forces/velocity. <br />
 * For example if you want a vortex you can just create a field with a curl function (like : f(x,y,z)=(-z,0,x)).
 * See {@link FieldProperties} for the options you can use. 
 * The power is a scalar which will multiply the function for any position (any x,y,z).
 */
public class Field {
	
	private MathField field;
	private FieldProperties properties;
	private double power = 0.05;
	
	
	/**
	 * Fields allow customs forces/velocity. <br />
	 * For example if you want to make a vortex you can just create a field with a curl function (like : f(x,y,z)=(-z,0,x)). <br />
	 * @param field : A {@link MathField} which contains a lambda function.
	 * @param properties : Properties of the field, see {@link FieldProperties}.
	 * @param power : scalar which will multiply the function for any position (any x,y,z).
	 */
	public Field(MathField field, FieldProperties properties, double power) {
		this.field = field;
		this.properties = properties;
		this.power = power;
	}
	
	public void render() {}

	public MathField getField() {
		return field;
	}

	public void setField(MathField field) {
		this.field = field;
	}

	public FieldProperties getProperties() {
		return properties;
	}

	public void setProperties(FieldProperties properties) {
		this.properties = properties;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
	
	
	

}
