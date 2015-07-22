/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.Agent;
import framework.PContext;
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
	private Agent myAgent;
	
	/**
	 * The constructor.
	 */
	public SelfTranscendence(double strengthWeight, Agent myAgent) {
		super(strengthWeight, CFG.SELFT_beta(), CFG.SELFT_k()); //Different implementation of using constuctor than before.
		this.weightVegEating = CFG.SELFT_actionWeight();
		this.myAgent = myAgent;
	}
	
	@Override
	public void updateSatisfaction(PContext myContext, SocialPractice actionDone){
		double eatenVeg = (actionDone instanceof VegEatingPractice) ? 1:0;
		double feature1 = weightVegEating * eatenVeg;
		double connectedFeaturesWeightedSum = feature1;
		
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum, myContext);
	}
	
	//dunno if still necc
	@Override
	public void updateSatisfactionEvaluative(PContext myContext, SocialPractice actionDone){
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
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum, myContext);
	}
	
	@Override
	public double getStrengthAvarage() {
		return CFG.SELFT_AVG_STRENGTH();
	}
	
	@Override
	public double getMyEvaluation(PContext myContext) {
		double ev = myAgent.getMySocialPractices().get(0).calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(myAgent.getOCweight()));
		return ev;
	}
}
