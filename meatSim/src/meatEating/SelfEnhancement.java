/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.SocialPractice;
import framework.Value;

// End of user code

/**
 * Description of SelfEnhancement.
 * 
 * @author rijk
 */
public class SelfEnhancement extends Value {
	private double weightMeatEating;
	
	
	/**
	 * The constructor.
	 */
	public SelfEnhancement(double strengthWeigth) {
		super(strengthWeigth, CFG.SELFE_beta(), CFG.SELFE_k()); //Different implementation of using constuctor than before.
		this.weightMeatEating = CFG.SELFE_actionWeight();
	}
	
	public void updateSatisfaction(SocialPractice actionDone){
		double eatenMeat = (actionDone instanceof MeatEatingPractice) ? 1:0;
		double feature1 = weightMeatEating * eatenMeat;
		//Room for more features.
		double connectedFeaturesWeightedSum = feature1;
		
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum);
	}
	
	@Override
	public void updateSatisfactionEvaluative(SocialPractice actionDone){
		double connectedFeaturesWeightedSum;
		if(actionDone instanceof MeatEatingPractice){
			connectedFeaturesWeightedSum=
					1 *
					(actionDone.getLastEvaluation().getGrade() + 0.5)*
					weightMeatEating;
		}else{
			connectedFeaturesWeightedSum = 
					0;
		}
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum);
	}
	

	@Override
	public double getStrengthAvarage() {
		return CFG.SELFE_AVG_STRENGTH();
	}
}
