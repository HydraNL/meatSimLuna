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
	private double strengthWeight;
	private double beta;
	private double k;

	/**
	 * The constructor.
	 */
	public Value(double strengthWeight, double beta, double k){
		//System.out.println(strengthWeight);
		this.strengthWeight = strengthWeight;
		this.satisfaction = RandomHelper.getNormal().nextDouble() * getThreshold();
		this.beta = beta;
		this.k = k;
	}
	
	public abstract double getStrengthAvarage();
	
	public double getStrength() {
		return strengthWeight * getStrengthAvarage();
	}
	
	public double getThreshold(){
		return getStrength();		
	}

	/**
	 * Description of the method getNeed.
	 */
	public double getNeed() {
		return getThreshold()/satisfaction;  //only works if satisfaction stays positive, else the Needs get lower when satisfaction gets lower
	}
	
	/**
	 * Description of the method equals.
	 */
	public void equals() {
		// Start of user code for method equals
		// End of user code
	}
	
	public void updateSatisfactionFunction(double connectedFeaturesSum){
				double increment = Math.tanh( beta/0.05 * (connectedFeaturesSum - getK())) /10; //beta = 0.03 //je kan de 0.05 eigenlijk wegstrepen, en beta wordt dan 1.
				System.out.println("Increment: " + this + "by: "+ increment +"because of: " +  beta * (connectedFeaturesSum - getK()));
		 		satisfaction += increment;
		 		if(satisfaction > 5 * getStrengthAvarage()) satisfaction = 5*getStrengthAvarage();
		 		if(satisfaction < 0.2 * getStrengthAvarage()) satisfaction = 0.2 * getStrengthAvarage();
		 	}
		 	
			private double getK() {
				double modifier = getStrength();
				return getStrength() * k;
			}
		
	//Might give problems later as each value needs its own parameters when updating.
	public abstract void updateSatisfaction(SocialPractice myAction);
	
	public abstract void updateSatisfactionEvaluative(SocialPractice myAction);
	
	protected void setBeta(double beta){
		this.beta = beta;
	}
	
	protected void setK(double k){
		this.k = k;
	}
	
	//for data purposes
	public double getSatisfaction() {
		return satisfaction;
	}

	
}
