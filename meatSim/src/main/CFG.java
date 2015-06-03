/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import repast.simphony.engine.environment.RunEnvironment;

// Start of user code (user defined imports)

// End of user code

/**
 * Configuration file to store global variables.
 * Maybe don't need it due to paramfile
 * @author rijk
 */
public class CFG {
	public static boolean GUI;
	
	/*Changes in deliberation*/
	public static boolean isFilteredOnAffordances(){
		return RunEnvironment.getInstance().getParameters().getBoolean("filterOnAffordances");
	}
	public static boolean isFilteredOnHabits(){
		return RunEnvironment.getInstance().getParameters().getBoolean("filterOnHabits");
	}
	public static boolean isEvaluated(){
		return RunEnvironment.getInstance().getParameters().getBoolean("isEvaluated");
	}
	public static boolean isIntentional() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isIntentional");
	}	
	public static boolean isUpdatedPerformanceHistory() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isUpdatedPHistory");
	}

	
	/*Agent and Venue Counts*/
	public static int agentCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("eater1Count");
	}
	public static int mixedVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("mixedVenueCount");
	}
	public static int meatVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("meatVenueCount");
	}
	public static int vegetarianVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("vegetarianVenueCount");
	}
	public static int venueCount(){
		return mixedVenueCount() + meatVenueCount() + vegetarianVenueCount();
	}
	
	/*Habitual Parameters*/
	public static double HABIT_THRESHOLD_RELATIVE(){
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_RELATIVE"); //An action has to be done 70% in this context to become habitual.
	}
	public static double OUTSIDE_CONTEXT(){
		return RunEnvironment.getInstance().getParameters().getDouble("OUTSIDE_CONTEXT");
	}
	public static double HABIT_THRESHOLD_ABSOLUTE() {
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_ABSOLUTE");
	}
	
	/*Value Strength*/
	public static double SELFT_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("SELFT_AVG_STRENGTH");
	}
	
	public static double SELFE_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("SELFE_AVG_STRENGTH");
	}
	
	public static double OPENNESS_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("OPENNESS_AVG_STRENGTH");
		
	}
	public static double CONSERVATION_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("CONSERVATION_AVG_STRENGTH");
	}
	
	/*Satisfaction parameters*/
	public static double SELFT_k(){
		return RunEnvironment.getInstance().getParameters().getDouble("selfTk");
	}
	public static double SELFT_beta(){ //achievementdrive
		return RunEnvironment.getInstance().getParameters().getDouble("selfTbeta");
	}
	public static double SELFT_actionWeight(){
		return RunEnvironment.getInstance().getParameters().getDouble("weightVegEatingAction");
	}
	public static double SELFE_k(){
		return RunEnvironment.getInstance().getParameters().getDouble("selfEk");
	}
	public static double SELFE_beta(){ //achievementdrive
		return RunEnvironment.getInstance().getParameters().getDouble("selfEbeta");
	}
	public static double SELFE_actionWeight(){
		return RunEnvironment.getInstance().getParameters().getDouble("weightMeatEatingAction");
	}
	
	//SocialEvaluation
	public static double a;
	public static double b; 
	
	public static void setA() {
		//a = 0.5 * (agentCount -1) * (1/ (double) venueCount);
		a= 0;
	}
	
	public static void setB() {
		//b = Math.sqrt(0.5 *a);
		b = Math.sqrt(0.5 * 0.5 * (agentCount() -1) * (1 / (double) venueCount()) *2 );
	}

	
	public static int getAgentID() {
		agentID++;
		return agentID%agentCount();
	}
	public static int getLocationID() {
		return locationID++;
	}

	//How to reset
	private static int agentID = 0;
	private static int locationID = 0;

	public static int diningOutPercent() {
		return RunEnvironment.getInstance().getParameters().getInteger("diningOutPercent");
	}
	public static double endTime() {
		return RunEnvironment.getInstance().getParameters().getDouble("endTime");
	}
}
