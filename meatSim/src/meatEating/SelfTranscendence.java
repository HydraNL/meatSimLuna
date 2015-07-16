/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.SocialPractice;
import framework.Value;

// End of user code

/**
 * Description of SelfTranscendence.
 * 
 * @author rijk
 */
public class SelfTranscendence extends Value {
	private double weightVegEating;
	
	/**
	 * The constructor.
	 */
	public SelfTranscendence(double strengthWeight) {
		super(strengthWeight, CFG.SELFT_beta(), CFG.SELFT_k()); //Different implementation of using constuctor than before.
		this.weightVegEating = CFG.SELFT_actionWeight();
	}
	
	@Override
	public void updateSatisfaction(SocialPractice actionDone){
		double eatenVeg = (actionDone instanceof VegEatingPractice) ? 1:0;
		double feature1 = weightVegEating * eatenVeg;
		double connectedFeaturesWeightedSum = feature1;
		
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum);
	}
	
	@Override
	public void updateSatisfactionEvaluative(SocialPractice actionDone){
		double connectedFeaturesWeightedSum;
		if(actionDone instanceof VegEatingPractice){
			connectedFeaturesWeightedSum=
					1 *
					(actionDone.getLastEvaluation().getGrade() + 0.5)*
					weightVegEating;
		}else{
			connectedFeaturesWeightedSum = 
					0;
		}
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum);
	}
	
	@Override
	public double getStrengthAvarage() {
		return CFG.SELFT_AVG_STRENGTH();
	}
}
