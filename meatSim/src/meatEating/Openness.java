/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.SocialPractice;
import framework.Value;

// End of user code

/**
 * Description of Openness.
 * 
 * @author rijk
 */
public class Openness extends Value {
	// Start of user code (user defined attributes for Openness)
	
	// End of user code
	
	/**
	 * The constructor.
	 */
	public Openness(double strengthWeigth) {
		super(strengthWeigth, 0, 0); //Different implementation of using constuctor than before.
	}
	// Start of user code (user defined methods for Openness)


	@Override
	public void updateSatisfaction(SocialPractice myAction) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public double getStrengthAvarage() {
		return CFG.OPENNESS_AVG_STRENGTH();
	}
	
	// End of user code


}
