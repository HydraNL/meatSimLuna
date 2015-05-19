/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import main.Helper;

// Start of user code (user defined imports)

// End of user code

/**
 * use instanceof or add compare method
 * 
 * @author rijk
 */
public abstract class Value {
	private double satisfaction;
	private double threshold;
	private double strength;
	private double beta;
	private double k;

	/**
	 * The constructor.
	 */
	public Value(double strength, double beta, double k){
		this.strength = strength;
		this.threshold = strength;
		this.satisfaction = RandomHelper.nextDoubleFromTo(0, 1); //begin on threshold
		this.beta = beta;
		this.k = k;
	}
	
	/**
	 * Description of the method getNeed.
	 */
	public double getNeed() {
		if(satisfaction < 0) satisfaction = 0.01; //does it give a overflow?
		return threshold/satisfaction;  //only works if satisfaction stays positive, else the Needs get lower when satisfaction gets lower
	}
	 
	/**
	 * Description of the method equals.
	 */
	public void equals() {
		// Start of user code for method equals
		// End of user code
	}
	
	public void updateSatisfactionFunction(double connectedFeaturesSum){
		double increment = Math.tanh( beta * (connectedFeaturesSum - k));
		System.out.println("Increment: " + this + "by: "+ increment);
		satisfaction += increment;
	}
	
	//Might give problems later as each value needs its own parameters when updating.
	public abstract void updateSatisfaction(SocialPractice myAction);
	
	protected void setBeta(double beta){
		this.beta = beta;
	}
	
	protected void setK(double k){
		this.k = k;
	}

	public double getStrength() {
		return strength;
	}
}
