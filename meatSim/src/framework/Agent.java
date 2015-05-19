/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
// Start of user code (user defined imports)





import java.util.Iterator;
import java.util.function.Predicate;

import main.SocialPracticeConverter;
import main.CFG;
import main.Helper;
import meatEating.Conservation;
import meatEating.SelfEnhancement;
import meatEating.SelfTranscendence;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameter;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

// End of user code

/**
 * myAction is a Social Practice and a Social Practice has an embodiment variable, if we choose Social Practices to represent a multiple of actions we should make an action class, myAction should be an Action and action has an embodiment
 * chooseAction needs a temporary variable named candidateActions()
 * 
 * @author rijk
 */
public abstract class Agent {
	private Grid<Object> myGrid;
	private ArrayList<PContext> candidateContexts; //Consider giving him candidateLocations
	private PContext myContext;
	private ArrayList<SocialPractice> mySocialPractices =new ArrayList<SocialPractice>(); //check with reseting model
	private HashMap<Class, Value> myValues=new HashMap<Class, Value>();
	private int ID;
	private SocialPractice myAction;
	

	public Agent(ArrayList<PContext> candidateContexts, Grid<Object> grid) {
		this.myGrid = grid;
		this.candidateContexts = candidateContexts;
		this.ID = CFG.getAgentID(); //for repast
	}
	
	
	/**
	 * Description of the method step.
	 * How is this done per agent?
	 */
	@ScheduledMethod(start = 1, interval = 1, priority = 3)
	public void step1() {
		move();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void step2() {
		myAction = chooseAction();
		act();
		updateValues();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step3(){
		learn();
	}
	
	
	 
	/**
	 * Moves agent to random context.
	 * Maybe put in Eater instead.
	 */
	private void move() {
		int randomIndex = RandomHelper.nextIntFromTo(0, candidateContexts.size() - 1) ;
		
		myContext = candidateContexts.get(randomIndex);
		myContext.addAgent(this);
		Helper.moveToObject(myGrid, this, myContext);
	}
	 
	/**
	 * Description of the method chooseAction.
	 */
	private SocialPractice chooseAction() {
		ArrayList<SocialPractice> candidateSocialPractices = (ArrayList<SocialPractice>) mySocialPractices.clone();
		ArrayList<SocialPractice> previousCandidates;
		SocialPractice chosenAction;
		
		if(CFG.isFilteredOnAffordances()){
			filterOnAffordances(candidateSocialPractices);
			if(candidateSocialPractices.size() == 1) return candidateSocialPractices.get(0);
		}
		
		if(CFG.isFilteredOnHabits()){
			previousCandidates = (ArrayList<SocialPractice>) candidateSocialPractices.clone();
			filterOnTriggers(candidateSocialPractices);
			if(candidateSocialPractices.size() == 1)	return candidateSocialPractices.get(0);
			if(candidateSocialPractices.size() < 1) candidateSocialPractices = previousCandidates; //Return to Afforded
		}
		
		if(CFG.isIntentional()) chosenAction = chooseOnIntentions(candidateSocialPractices);
		else{																						//Choose Randomly
			chosenAction = candidateSocialPractices.get(RandomHelper.nextIntFromTo(0, candidateSocialPractices.size()-1));
		}
		return chosenAction;
	}
	 
	


	/**
	 * Description of the method checkAffordances.
	 */
	private void filterOnAffordances(ArrayList<SocialPractice> candidateSocialPractices) {
		Iterator<SocialPractice> iter = candidateSocialPractices.iterator();		//use of iterator so one can remove from list
		
		while (iter.hasNext()){
			SocialPractice sp = iter.next();
			boolean contextAffordsSP = false;
			
			for(PContext affordance: sp.getAffordances()){ 
				
				/*check affordance location matches with current location*/
				Location locationToCheck = affordance.getPhysical().getMyLocation();
				Location l = getMyLocation();
				if(getMyLocation().getClass() == locationToCheck.getClass()) contextAffordsSP = true;
				//TODO: change affordances to list of classes. Problem though, it is a context object right now.
				/*check affordance social matches with current socialcontext*/
			}
			if(!contextAffordsSP) iter.remove();
		}
	}
	/*Filter candidate Social Practices on their relative Habit Strength by:
	 * 1. Calculate frequency per Social Practice
	 * 2. Calculate totalFrequency
	 * 3. Calculate Habit Strength per Social Practice
	 * 
	 */
	private void filterOnTriggers(
			ArrayList<SocialPractice> candidateSocialPractices) {
		HashMap<SocialPractice, Double> frequencies=new HashMap<SocialPractice, Double>();
		HashMap<SocialPractice, Double> habitStrengths =new HashMap<SocialPractice, Double>();
		double totalFrequency = 0;
		
		for(SocialPractice sp: mySocialPractices){					//Consider all practices when calculating freqeuency
			frequencies.put(sp, sp.calculateFrequency(myContext));
		}
		totalFrequency = Helper.sumDouble(frequencies.values());
		
		for(SocialPractice sp: candidateSocialPractices){			//Consider only affordad practices when determining habitStrength
			habitStrengths.put(sp, calculateHabitStrength(frequencies.get(sp), totalFrequency));
		}
		
		System.out.println("Habits: " + habitStrengths.toString());
		
		Helper.filter(candidateSocialPractices, habitStrengths, CFG.HABIT_THRESHOLD());
		
	}
	
	/*
	 * 
	 * If the practice has never been done before return 1.
	 */
	private double calculateHabitStrength(Double spFrequency, double totalFrequency) {
		return totalFrequency ==0 ? 1:spFrequency/totalFrequency;
	}
	 
	/**
	 * Description of the method checkIntentions.
	 * Choose random double in between 0 and total need.
	 * Choose socialpractice based on this double and need per sp.
	 */
	private SocialPractice chooseOnIntentions(
			ArrayList<SocialPractice> candidateSocialPractices) {
		SocialPractice chosenAction = null; //temp
		HashMap<SocialPractice, Double> needs=new HashMap<SocialPractice, Double>();
		for(SocialPractice sp: candidateSocialPractices){
			needs.put(sp, myValues.get(sp.getPurpose()).getNeed()); 
		}
		double totalNeed = Helper.sumDouble(needs.values()); //satisfaction can get <0, so need as well, so maybe not getting in while loop
		double randomDeterminer = RandomHelper.nextDoubleFromTo(0, totalNeed);
		System.out.println("Needs:" + needs);
		
		Iterator it = needs.entrySet().iterator();
		while(randomDeterminer > 0) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			chosenAction = (SocialPractice) pair.getKey();
			randomDeterminer = randomDeterminer - (double) pair.getValue();
		}
		return chosenAction;
	}
	 
	/**
	 * Description of the method act.
	 */
	private void act() {
		myAction.embodiment();
	}
	
	private void updateValues(){
		for(Value val: myValues.values()){
			val.updateSatisfaction(myAction);
		}
	}
	 
	/**
	 * Description of the method learn.
	 */
	private void learn() {
		if(CFG.isEvaluated()) evaluate();
		if(CFG.isFilteredOnHabits()){
			if(CFG.isEvaluated()) updateHistoryEvaluative();
			else{
				updateHistory();
			}
			
		}
	}
	 
	


	/**
	 * Description of the method evaluate.
	 */
	private void evaluate() {
		double Iweight = myValues.get(SelfEnhancement.class).getStrength();	//SelfEnhancement
		double Sweight = (myValues.get(Conservation.class).getStrength() + myValues.get(SelfTranscendence.class).getStrength()) /2; //Conservation + selfTranscendence
		double grade = Iweight * individualEvaluation()+ Sweight * socialEvaluation();
		System.out.println("Evaluation :" + Iweight + " " + individualEvaluation() + " " + Sweight + " " + socialEvaluation());
		myAction.addEvaluation(new Evaluation(grade, myContext));
	}
	
	
	//TODO: not working yet, always 1
	private double socialEvaluation() {
		double simAgents = 0;	//amount of similar agents
		for(Agent a: myContext.getMyAgents()){
			if(a.myAction.getClass() == myAction.getClass() && a != this) simAgents++;
		}
		double dissimAgents = myContext.getMyAgents().size() - simAgents; //amount of dissimilar agents
		double x = simAgents - dissimAgents;
		
		return 0.5 + 0.5 * Math.tanh((x-CFG.a)/CFG.b);
	}


	private double individualEvaluation() {
		return myValues.get(myAction.getPurpose()).getStrength();
	}


	public void updateHistory(){
			myAction.updatePerformanceHistory(myContext);
	}
	
	private void updateHistoryEvaluative() {
		myAction.updatePerformanceHistoryEvaluative(myContext);
		
	}
	 
	protected void addSocialPractice(SocialPractice sp){
		mySocialPractices.add(sp);
	}
	
	protected void addValue(Value val){
		myValues.put(val.getClass(), val);
	}
	
	@Parameter(usageName="myAction", displayName="Action", converter = "main.SocialPracticeConverter")
	public SocialPractice getMyAction() {
		return this.myAction;
	}
	
	public Location getMyLocation(){
		return myContext.getPhysical().getMyLocation();
	}
	
	/*Datacollectors*/
	public double meatNeed(){ 
		//System.out.println("needfuncmeat:" + myValues.get(SelfEnhancement.class).getNeed());
		double need = myValues.get(SelfEnhancement.class).getNeed();
		return (need<100) ? need:100;
	}
	
	public double vegNeed(){
		double need = myValues.get(SelfTranscendence.class).getNeed();
		return (need<100) ? need:100;
	}


	public int getID() {
		return ID;
	}
}