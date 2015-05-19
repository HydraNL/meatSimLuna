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
		addValue(new SelfEnhancement(RandomHelper.nextDoubleFromTo(0, 1)));
		addValue(new SelfTranscendence(RandomHelper.nextDoubleFromTo(0, 1)));
		addValue(new Openness(RandomHelper.nextDoubleFromTo(0, 1)));
		addValue(new Conservation(RandomHelper.nextDoubleFromTo(0, 1)));
	}

	
	// Start of user code (user defined methods for Eater1)
	
	// End of user code


}
