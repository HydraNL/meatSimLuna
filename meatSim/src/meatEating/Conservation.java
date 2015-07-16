/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.SocialPractice;
import framework.Value;

// End of user code

/**
 * Description of Conservation.
 * 
 * @author rijk
 */
public class Conservation extends Value {
	// Start of user code (user defined attributes for Conservation)
	
	// End of user code
	
	/**
	 * The constructor.
	 */
	public Conservation(double strengthWeight) {
		super(strengthWeight, 0, 0);
	}

	@Override
	public void updateSatisfaction(SocialPractice myAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getStrengthAvarage() {
		return CFG.CONSERVATION_AVG_STRENGTH();
	}

	@Override
	public void updateSatisfactionEvaluative(SocialPractice myAction) {
		// TODO Auto-generated method stub
		
	}


}
