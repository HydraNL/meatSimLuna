/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import framework.Agent;
import framework.PContext;
import framework.SocialPractice;

// End of user code

/**
 * Description of Eater1.
 * 
 * @author rijk
 */
public class Eater1 extends Agent {
	
	/**
	 * The constructor.
	 */
	public Eater1(ArrayList<PContext> candidateContexts, Grid<Object> grid) {
		super(candidateContexts, grid);
		addSocialPractice(new MeatEatingPractice());
		addSocialPractice(new VegEatingPractice());
		addValue(new SelfEnhancement(RandomHelper.nextDoubleFromTo(0, 2))); //0 to 2 because this gives the wished avg strength
		addValue(new SelfTranscendence(RandomHelper.nextDoubleFromTo(0, 2)));
		addValue(new Openness(RandomHelper.nextDoubleFromTo(0, 2)));
		addValue(new Conservation(RandomHelper.nextDoubleFromTo(0, 2)));
	}
	
	/*DataCollectors*/
	public double dataHabitStrengthMeat(){
		return dataHabitStrength(MeatEatingPractice.class);
	}
	public double dataHabitStrengthVeg(){
		return dataHabitStrength(VegEatingPractice.class);
	}
	public double dataFrequencyIndexMeat(){
		return dataFrequencyIndex(MeatEatingPractice.class);
	}
	public double dataFrequencyIndexVeg(){
		return dataFrequencyIndex(VegEatingPractice.class);
	}
	public double dataNeedSelfEnhancement(){
		return dataNeed(SelfEnhancement.class);
	}
	public double dataNeedSelfTranscendence(){
		return dataNeed(SelfTranscendence.class);
	}
	public double dataMeatEvaluation(){
		return dataMeatAction() * dataEvaluation();
	}
	public double dataVegEvaluation(){
		return dataVegAction() *dataEvaluation();
	}
}
