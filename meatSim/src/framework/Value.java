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
		this.strengthWeight = strengthWeight; //ND and correlated over 1, 0.25
		this.satisfaction = RandomHelper.getNormal().nextDouble() * strengthWeight *getStrengthAvarage(); //take original strength
		this.beta = beta;
		this.k = k;
	}
	
	public abstract double getStrengthAvarage(); //Normally also 1
	
	public abstract double getMyEvaluation(PContext myContext);
	
	public double getStrength(PContext myContext) {
		System.out.println("Evaluation in context is"+ getMyEvaluation(myContext));
		return strengthWeight * getStrengthAvarage() *getMyEvaluation(myContext);
	}
	
	public double getThreshold(PContext myContext){
		return getStrength(myContext);		
	}

	/**
	 * Description of the method getNeed.
	 */
	public double getNeed(PContext myContext) {
		return getThreshold(myContext)/satisfaction;  //only works if satisfaction stays positive, else the Needs get lower when satisfaction gets lower
	}
	
	/**
	 * Description of the method equals.
	 */
	public void equals() {
		// Start of user code for method equals
		// End of user code
	}
	
	public void updateSatisfactionFunction(double connectedFeaturesSum, PContext myContext){
				double increment = Math.tanh( beta/0.05 * (connectedFeaturesSum - getK(myContext))) /10; //beta = 0.03 //je kan de 0.05 eigenlijk wegstrepen, en beta wordt dan 1.
				//System.out.println("Increment: " + this + "by: "+ increment +"because of: " +  beta * (connectedFeaturesSum - getK()));
		 		satisfaction += increment;
		 		if(satisfaction > 5 * getStrengthAvarage()) satisfaction = 5*getStrengthAvarage();
		 		if(satisfaction < 0.2 * getStrengthAvarage()) satisfaction = 0.2 * getStrengthAvarage();
		 	}
		 	
			private double getK(PContext myContext) {
				return getStrength(myContext) * k;
			}
		
	//Might give problems later as each value needs its own parameters when updating.
	public abstract void updateSatisfaction(PContext myContext, SocialPractice myAction);
	
	public abstract void updateSatisfactionEvaluative(PContext myContext, SocialPractice myAction);
	
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
