/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import repast.simphony.dataLoader.ContextBuilder;
import main.AbstractBuilder;
import main.CFG;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of MeatSimBuilder.
 * 
 * @author rijk
 */
public class MeatSimBuilder extends AbstractBuilder {

	@Override
	public void setID() {
		context.setId("meatSim");
	}

	@Override
	public void addAgents() {
		int eater1Count = (Integer) params.getValue("eater1Count");
		for (int i=0; i < eater1Count; i++){
			Eater1 newAgent=new Eater1(agents, locations, homes, grid);
			context.add(newAgent);
			agents.add(newAgent);
		}
	}
	
	@Override
	public void setCFG(){
		
		/*Values to calculate Social Norm*/
		CFG.setA();
		CFG.setB();
		
	}
	
	@Override
	public void addEnvironment(){
		int mixedVenueCount = (Integer) params.getValue("mixedVenueCount");
		for (int i=0; i < mixedVenueCount; i++){
			MixedVenue newMixedVenue=new MixedVenue(grid);
			context.add(newMixedVenue);
			locations.add(newMixedVenue);
		}
		
		int meatVenueCount = (Integer) params.getValue("meatVenueCount");
		for (int i=0; i < meatVenueCount; i++){
			MeatVenue newMeatVenue=new MeatVenue(grid);
			context.add(newMeatVenue);
			locations.add(newMeatVenue);
		}
		
		int vegetarianVenueCount = (Integer) params.getValue("vegetarianVenueCount");
		for (int i=0; i < vegetarianVenueCount; i++){
			VegVenue newVegVenue=new VegVenue(grid);
			context.add(newVegVenue);
			locations.add(newVegVenue);
		}
		
		if(CFG.chooseContext()){
			int homesCount = CFG.getHomesCount();
			for (int i=0; i < homesCount; i++){
				Home newHome=new Home(grid);
				context.add(newHome);
				homes.add(newHome);
			}
		}
	}
}
