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
	public SelfEnhancement(double strength) {
		super(strength, CFG.SELFE_beta(), CFG.SELFE_k()); //Different implementation of using constuctor than before.
		this.weightMeatEating = CFG.SELFE_actionWeight();
	}
	
	public void updateSatisfaction(SocialPractice actionDone){
		double eatenMeat = (actionDone instanceof MeatEatingPractice) ? 1:0;
		double feature1 = weightMeatEating * eatenMeat;
		double connectedFeaturesWeightedSum = feature1;
		
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum);
	}
}
