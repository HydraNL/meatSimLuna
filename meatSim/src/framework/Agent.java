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
import meatEating.MeatEatingPractice;
import meatEating.SelfEnhancement;
import meatEating.SelfTranscendence;
import meatEating.VegEatingPractice;
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
	private ArrayList<SocialPractice> mySocialPractices; //check with reseting model
	private HashMap<Class, Value> myValues;
	
	//For Data Projection
	private int ID;
	private SocialPractice myAction;
	HashMap<SocialPractice, Double> frequencies; //TODO: Might be nicer to put it in the SocialPractice
	HashMap<SocialPractice, Double> habitStrengths;
	ActionType actionType;
	private enum ActionType{
		AFFORDED,
		HABITUAL,
		INTENTIONAL,
		NOACTION,
		RANDOM
	}
	private boolean isDiningOut;
	
	

	public Agent(ArrayList<PContext> candidateContexts, Grid<Object> grid) {
		this.myGrid = grid;
		this.candidateContexts = candidateContexts;
		this.ID = CFG.getAgentID(); //for repast
		
		mySocialPractices=new ArrayList<SocialPractice>();
		myValues =new HashMap<Class, Value>();
		frequencies=new HashMap<SocialPractice, Double>();
		habitStrengths=new HashMap<SocialPractice, Double>();
	}
	
	
	/**
	 * Description of the method step.
	 * How is this done per agent?
	 */
	@ScheduledMethod(start = 1, interval = 1, priority = 3)
	public void step1() {
		isDiningOut = (RandomHelper.nextIntFromTo(0,100) <= CFG.diningOutPercent());
		if(isDiningOut) move();
		//Maybe move to another location
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void step2() {
		if(isDiningOut) myAction = chooseAction();
		else{
			myAction = new NoAction(); //Maybe change to just new Social Practice which will not be an instance of either;
			actionType = ActionType.NOACTION;
		}
		act();
		updateValues();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step3(){
		if(isDiningOut) learn();
	}
	
	
	 
	/**
	 * Moves agent to random context.
	 * Maybe put in Eater instead.
	 */
	private void move() {
		int randomIndex = RandomHelper.nextIntFromTo(0, candidateContexts.size() - 1) ;
		
		myContext = candidateContexts.get(randomIndex); //How can this work, doesn't this list get infinite.
														//No the reference to the list is given, and the list cleared everytimestep.
		myContext.addAgent(this);
		Helper.moveToObject(myGrid, this, myContext);
	}
	 
	/**
	 * Description of the method chooseAction.
	 */
	private SocialPractice chooseAction() {
		ArrayList<SocialPractice> candidateSocialPractices = (ArrayList<SocialPractice>) mySocialPractices
				.clone();
		ArrayList<SocialPractice> previousCandidates;
		SocialPractice chosenAction;

		if (CFG.isFilteredOnAffordances()) {
			filterOnAffordances(candidateSocialPractices);
			if (candidateSocialPractices.size() == 1) {
				actionType = ActionType.AFFORDED;
				return candidateSocialPractices.get(0);
			}
		}

		if (CFG.isFilteredOnHabits()) {
			previousCandidates = (ArrayList<SocialPractice>) candidateSocialPractices
					.clone();
			candidateSocialPractices = filterOnTriggers(candidateSocialPractices);
			if (candidateSocialPractices.size() == 1) {
				actionType = ActionType.HABITUAL;
				return candidateSocialPractices.get(0);
			}
			if (candidateSocialPractices.size() < 1)
				candidateSocialPractices = previousCandidates; // Return to
																// Afforded
		}

		if (CFG.isIntentional()) {
			actionType = ActionType.INTENTIONAL;
			chosenAction = chooseOnIntentions(candidateSocialPractices);
		} else {
			actionType = ActionType.RANDOM;// Choose Randomly
			chosenAction = candidateSocialPractices.get(RandomHelper
					.nextIntFromTo(0, candidateSocialPractices.size() - 1));
		}
		return chosenAction;
	}
	 
	


	/**
	 * Description of the method checkAffordances.
	 */
	private void filterOnAffordances(
			ArrayList<SocialPractice> candidateSocialPractices) {
		Iterator<SocialPractice> iter = candidateSocialPractices.iterator();
		while (iter.hasNext()) {
			SocialPractice sp = iter.next();
			boolean contextAffordsSP = false;

			for (PContext affordance : sp.getAffordances()) {

				/* check affordance location matches with current location */
				Location locationToCheck = affordance.getPhysical()
						.getMyLocation();
				Location l = getMyLocation();
				if (getMyLocation().getClass() == locationToCheck.getClass())
					contextAffordsSP = true;
				// TODO: change affordances to list of classes. Problem though,
				// it is a context object right now.
				/* check affordance social matches with current socialcontext */
			}
			if (!contextAffordsSP)
				iter.remove();
		}
	}
	
	/*Filter candidate Social Practices on their relative Habit Strength by:
	 * 1. Calculate frequency per Social Practice
	 * 2. Calculate totalFrequency
	 * 3. Calculate Habit Strength per Social Practice
	 * 
	 */
	private ArrayList<SocialPractice> filterOnTriggers(
			ArrayList<SocialPractice> candidateSocialPractices) {
		ArrayList<SocialPractice> newCandidates = new ArrayList<SocialPractice>();
		frequencies.clear();
		habitStrengths.clear();

		double totalF = 0;
		for (SocialPractice sp : mySocialPractices) {
			double F = sp.calculateFrequency(myContext);
			frequencies.put(sp, F);
			totalF += F;
		}
		for (SocialPractice sp : candidateSocialPractices) {
			habitStrengths.put(sp, habitStrength(frequencies.get(sp), totalF));
		}
		for (SocialPractice sp : candidateSocialPractices) {
			if (frequencies.get(sp) > RandomHelper.nextDoubleFromTo(0, 2)
					* CFG.HTA()) { // t = 25 -> F = 5
				if (habitStrengths.get(sp) > CFG.HTR()) {
					newCandidates.add(sp);
				}
			}
		}
		return newCandidates;
	}
	
	/*
	 * 
	 * If the practice has never been done before return 1.
	 */
	private double habitStrength(Double spFrequency, double totalFrequency) {
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
		//System.out.println("Needs:" + needs);
		
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
		if(CFG.isUpdatedPerformanceHistory()){
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
		//System.out.println("Evaluation :" + Iweight + " " + individualEvaluation() + " " + Sweight + " " + socialEvaluation());
		myAction.addEvaluation(new Evaluation(Iweight, individualEvaluation(), Sweight, socialEvaluation(), myContext));
	}
	private double socialEvaluation() {
		double simAgents = 0;	//amount of similar agents
		for(Agent a: myContext.getMyAgents()){
			if(a.myAction.getClass() == myAction.getClass() && a != this) simAgents++;
		}
		double dissimAgents = myContext.getMyAgents().size() - simAgents; //amount of dissimilar agents
		double x = simAgents - dissimAgents;
		//System.out.print("thisev: " + 0.5 + 0.5 * Math.tanh((x-CFG.a)/CFG.b));
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
	/*Aggregate*/
	public int dataMeatAction(){
		return (getMyAction() instanceof MeatEatingPractice) ? 1:0;
	}
	public int dataVegAction(){
		return (getMyAction() instanceof VegEatingPractice) ? 1:0;
	}
	public int dataAffAction(){
		return (actionType == ActionType.AFFORDED) ? 1:0;
	}
	public int dataHabitual(){
		return (actionType == ActionType.HABITUAL) ? 1:0;
	}
	public int dataIntentional(){
		return (actionType == ActionType.INTENTIONAL) ? 1:0;
	}

	/*Graph1*/
	public int dataEatingType(){
		return dataMeatAction() - dataVegAction(); 
	}
	
	/*Crosspoints*/
	public int dataMeatAfforded(){
		return (dataMeatAction() + dataAffAction() == 2) ? 1:0;
	}
	public int dataMeatHabitual(){
		return (dataMeatAction() + dataHabitual() == 2) ? 1:0;
	}
	public int dataMeatIntentional(){
		return (dataMeatAction() + dataIntentional() == 2) ? 1:0;
	}
	
	public int dataVegIntentional(){
		return (dataVegAction() + dataIntentional() == 2) ? 1:0;
	}
	public int dataVegHabitual(){
		return (dataVegAction() + dataHabitual() == 2) ? 1:0;
	}
	public int dataVegAfforded(){
		return (dataVegAction() + dataAffAction() == 2) ? 1:0;
	}
	
	/*Individual*/
	/*Graph 6*/
	public int dataOneAgent(){
		if(dataMeatAfforded() == 1) return 1;
		if(dataMeatHabitual() == 1) return 2;
		if(dataMeatIntentional() == 1) return 3;
		if(dataVegAfforded() == 1) return -1;
		if(dataVegHabitual() == 1) return -2;
		if(dataVegIntentional() == 1) return -3;
		return 0; //Should never get here.
	}
	
	/*Habitual Params*/
	//Now only returns habitStrength if agent has just done a habitualaction.
	//
	public double dataHabitStrength(Class spClass){
		for(SocialPractice sp: habitStrengths.keySet()){
			if(actionType == ActionType.HABITUAL && sp.getClass()==spClass) return habitStrengths.get(sp);
		}
		return -1.0;
	}
	
	public double dataFrequencyIndex(Class spClass){
		for(SocialPractice sp: frequencies.keySet()){
			if(sp.getClass()==spClass) return frequencies.get(sp);
		}
		return 0.0;
	}
	
	/*Intentional Params*/
	public double dataNeed(Class spClass){
		double need = myValues.get(spClass).getNeed();
		return (need<100) ? need:100;
	}
	public double dataThreshold(Class spClass){
		return myValues.get(spClass).getThreshold();
	}
	public double dataSatisfaction(Class spClass){
		return myValues.get(spClass).getSatisfaction();
	}
	
	/*Evaluation params*/
	public double dataEvIndividual(){
		return myAction.getLastEvaluation().getIndE() *myAction.getLastEvaluation().getIweight();
	}
	public double dataEvSocial(){
		return myAction.getLastEvaluation().getSocE() *myAction.getLastEvaluation().getSweight();
	}
	public double dataEvaluation(){
		return myAction.getLastEvaluation().getGrade();
	}
	public int getID() {
		return ID;
	}
}