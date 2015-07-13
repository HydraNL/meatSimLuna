/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;

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
	public static boolean chooseContext(){
		return RunEnvironment.getInstance().getParameters().getBoolean("chooseContext");
	}
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
	public static boolean complexEvaluation() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isComplexEvaluated");
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
	public static double HTR(){
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_RELATIVE"); //An action has to be done 70% in this context to become habitual.
	}
	public static double OUTSIDE_CONTEXT(){
		return RunEnvironment.getInstance().getParameters().getDouble("OUTSIDE_CONTEXT");
	}
	public static double HTA() {
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_ABSOLUTE");
	}
	
	/*Value Strength*/
	public static double SELFT_AVG_STRENGTH(){
		if(getTime() > INTERVENTION_TIME()) return RunEnvironment.getInstance().getParameters().getDouble("SELFT_INTERVENTION_STRENGTH");
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
		return RunEnvironment.getInstance().getParameters().getDouble("selfEbeta"); //NB: changed to selFEBETA!!!
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
	
	//Simulation parameters
	public static double endTime() {
		return RunEnvironment.getInstance().getParameters().getDouble("endTime");
	}
	private static double INTERVENTION_TIME() {
		return RunEnvironment.getInstance().getParameters().getDouble("INTERVENTION_TIME");
	}
	public static double getTime(){
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	//Chooce context para's
	public static int getHomesCount() {
		return (int) Math.round(agentCount()/2.5);
	}
	
	public static ArrayList<Double> ratios;
	public static void createDiningOutDistribution(){
		ratios=new ArrayList<Double>();
		int times = (int) Math.round(agentCount()/2.0);
		add(ratios,times,((double) 1/30));
		times  = (int) Math.round(agentCount()/3.0);
		add(ratios,times,((double) 1/7));
		times  = (int)Math.round( agentCount()/10.0);
		add(ratios,times,((double) 2.5/7));
		times  = (int)Math.round(agentCount()/25.0);
		add(ratios,times,((double) 3/7));
		times  = (int)Math.round(agentCount()/33.0);
		add(ratios,times,((double) 4/7));
		times  = (int)Math.round(agentCount()/50.0);
		add(ratios,times,((double) 5/7));
		times  = (int)Math.round(agentCount()/80.0);
		add(ratios,times,((double) 29/30));
	}
	
	public static double getDiningOutRatio() {
		int randomIndex = RandomHelper.nextIntFromTo(0, ratios.size()-1);
		System.out.println("Ratio:" + ratios.get(randomIndex));
		return ratios.remove(randomIndex);
	}
	
	public static void add(ArrayList<Double> l, int times, double ratio){
		for(int i =0; i < times; i++){
			l.add(ratio);
		}
	}
	
	//TODO: look in paper
	public static int inviteDistribution() {
		return 2;
	}
}
