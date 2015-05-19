/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import framework.PContext;
import framework.PhysicalContext;
import framework.SocialPractice;

// End of user code

/**
 * Description of VegEatingPractice.
 * 
 * @author rijk
 */
public class VegEatingPractice extends SocialPractice {
	
	/**
	 * The constructor.
	 */
	public VegEatingPractice() {
		super();
		addAffordance(new PContext(new PhysicalContext(new MixedVenue())));
		addAffordance(new PContext(new PhysicalContext(new VegVenue())));
		addPurpose(SelfTranscendence.class);
	}
	
	/**
	 * Description of the method embodiment.
	 * Overwrite?
	 */
	public void embodiment() {
	}
	 


}
