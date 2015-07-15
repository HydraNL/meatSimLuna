/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
// Start of user code (user defined imports)





import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import main.SocialPracticeConverter;
import main.CFG;
import main.Helper;
import meatEating.Conservation;
import meatEating.MeatEatingPractice;
import meatEating.MixedVenue;
import meatEating.SelfEnhancement;
import meatEating.SelfTranscendence;
import meatEating.VegEatingPractice;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameter;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/*
 * POssible speedincrease:
 * Only filter on openLocations once per timestep, not twice per agent
 */
// End of user code

/**
 * myAction is a Social Practice and a Social Practice has an embodiment variable, if we choose Social Practices to represent a multiple of actions we should make an action class, myAction should be an Action and action has an embodiment
 * chooseAction needs a temporary variable named candidateActions()
 * 
 * @author rijk
 */
public abstract class Agent {
	private Grid<Object> myGrid;
	private ArrayList<Agent> agents;
	private ArrayList<Location> candidateLocations; //Does not include Homes.
	private PContext myContext;
	private ArrayList<SocialPractice> mySocialPractices; //check with reseting model
	private HashMap<Class, Value> myValues;
	
	//For chosing context
	private Location myHome;
	private double diningOutRatio;
	private boolean isLocated;
	private double acceptRatio;
	
	//For Data Projection
	private int ID;
	private SocialPractice myAction;
	HashMap<SocialPractice, Double> frequencies; //TODO: Might be nicer to put it in the SocialPractice
	HashMap<SocialPractice, Double> habitStrengths;

	HashMap<Location, Double> frequenciesL;
	HashMap<Location, Double> habitStrengthsL;

	HashMap<Agent, Double> frequenciesA;
	HashMap<Agent, Double> habitStrengthsA;
	
	ActionType actionType;
	private enum ActionType{
		AFFORDED,
		HABITUAL,
		INTENTIONAL,
		NOACTION,
		RANDOM
	}
	private boolean isEating;
	
	

	public Agent(ArrayList<Agent> agents, ArrayList<Location> candidateLocations, ArrayList<Location> homes, Grid<Object> grid) {
		this.myGrid = grid;
		this.candidateLocations = candidateLocations;
		this.agents = agents;
		this.ID = CFG.getAgentID(); //for repast
		
		mySocialPractices=new ArrayList<SocialPractice>();
		myValues =new HashMap<Class, Value>();
		
		frequencies=new HashMap<SocialPractice, Double>();
		habitStrengths=new HashMap<SocialPractice, Double>();
		frequenciesL =new HashMap<Location, Double>();
		habitStrengthsL =new HashMap<Location, Double>();
		frequenciesA=new HashMap<Agent, Double>();
		habitStrengthsA =new HashMap<Agent, Double>();
		
		int randomIndex = RandomHelper.nextIntFromTo(0,
				homes.size() - 1);
		myHome= homes.get(randomIndex);
		diningOutRatio = CFG.getDiningOutRatio();
		acceptRatio = 0.5; //TODO: make it a distribution or so
	}
	
	
	/**
	 * Description of the method step.
	 * How is this done per agent?
	 */
	//This is if you want to let only a percentage of the agents dine out.
	@ScheduledMethod(start = 1, interval = 1, priority = 6)
	public void determineIfEating() {
		isEating = (RandomHelper.nextIntFromTo(0,100) <= CFG.diningOutPercent());
	}
	
	
	//Either attribute people randomly to the set of locations.
	@ScheduledMethod(start = 1, interval = 1, priority = 5)
	public void randomContext() {
		if(!CFG.chooseContext() && isEating){
			//Note that you don't add PContexts to the grid, nor move their location
			//When making a Pcontext the constructer automaticly sets the pcontext of the location.
			List<Location> openLocations = new ArrayList<Location>(candidateLocations);
			openLocations = filterOnAffordancesL(openLocations);
			int randomIndex = RandomHelper.nextIntFromTo(0,
					openLocations.size() - 1);
			Location randomLocation = openLocations.get(randomIndex);
			goTo(randomLocation);
		}
	}
	
	//Or let them choose their Context
	@ScheduledMethod(start =1, interval = 1, priority = 5)
	public void diningIn() {
		if(CFG.chooseContext() &&isEating){
			if(RandomHelper.nextDoubleFromTo(0, 1) > diningOutRatio)
				goTo(myHome);
		}
	}
	
	//When you go to something you create or join a Pcontext.
	public void goTo(Location l){
		if(!l.hasContext()) new PContext(l);
		myContext = l.getMyContext();
		myContext.addAgent(this);
		Helper.moveToObject(myGrid, this, l);
		setLocated(true);
	}
	
	//Others diningOut
	@ScheduledMethod(start =1, interval = 1, priority = 4)
	public void diningOut(){
		if(CFG.chooseContext() &&isEating && !isLocated){
			Location chosenLocation;
			ArrayList<Agent> diningGroup =new ArrayList<Agent>();
			diningGroup.add(this);
			
			boolean chooseOnPhysical = physicalOrSocial();
			
			if(chooseOnPhysical){
				chosenLocation = pickLocation(candidateLocations);
				for(int i = 0; i < CFG.inviteDistribution(); i++){
					Agent a = pickEatBuddy();
					if(a != null && a.acceptInvitation(chosenLocation)) diningGroup.add(a); //If people avariblae and it accepts invitation.
				}
			}
			else{ //chooseOnSocial
				for(int i = 0; i < CFG.inviteDistribution(); i++){
					Agent a = pickEatBuddy();
					if(a != null) diningGroup.add(a); //Everybody accepts!
				}
				List<Location> affordedLocations = filterOnGroupsPreference(diningGroup, candidateLocations);
				chosenLocation = pickLocation(affordedLocations); //Lijst kan leeg zijn als er geen mixed zijn. Je zou dan een willekeurige kunnen pakken ofzo.
			}
			
			for(Agent a:diningGroup){
				a.goTo(chosenLocation);
			}
		}
	}
	
	

	private List<Location> filterOnGroupsPreference(
			ArrayList<Agent> diningGroup,
			ArrayList<Location> candidateLocations2) {
		List<Location> newCandidates=new ArrayList<Location>();
		
		for(Location l:candidateLocations2){
			boolean accepted = true;
			for(Agent a:diningGroup){
				accepted = a.acceptInvitation(l); //Geeft vast errors als je geen mixed restaurants, meer hebt, omdat lijst dan leeg is.
			}
			if(accepted) newCandidates.add(l);
		}
		return newCandidates;
	}


	//Might extend to choice on values.
	private boolean physicalOrSocial(){
		return RandomHelper.nextIntFromTo(0, 1) == 1;
	}
	
	private Location pickLocation(List<Location> locations){
		//You could add an affordance variable for open and close.
		List<Location> temp =new ArrayList<Location>(locations); 
		
		temp = filterOnAffordancesL(temp); //Easier to give a new list back and change ref.
		List<Location> aFiltered = new ArrayList<Location>(temp);
		temp = filterOnHabitsL(temp);
		List<Location> hFiltered = new ArrayList<Location>(temp);
		if(hFiltered.isEmpty()) temp = aFiltered; //Reroll if empty
		temp = filterOnIntentionsL(temp);
		List<Location> iFiltered = new ArrayList<Location>(temp);
		
		return pickRandomly(temp);
	}
	
	private List<Location> filterOnAffordancesL(List<Location> temp) {
		List<Location> newCandidates=new ArrayList<Location>();
		for(Location l:temp){
			if(l.isOpen()) newCandidates.add(l);
		}
		return newCandidates;
	}
	private List<Location> filterOnHabitsL(List<Location> aFiltered) {
		List<Location> newCandidates=new ArrayList<Location>();
		frequenciesL.clear();
		habitStrengthsL.clear();
		
		double totalF =0;
		for(Location l: candidateLocations){
			double F = 0;
			for(SocialPractice sp:mySocialPractices){
				F+= sp.calculateFrequencyL(l);
			}
			frequenciesL.put(l, F);
			totalF += F;
		}
		for (Location l: aFiltered) { //notice again cLoc vs aFilt
			habitStrengthsL.put(l, habitStrength(frequenciesL.get(l), totalF));
		}
		for(Location l: aFiltered){
			if (frequenciesL.get(l) > RandomHelper.nextDoubleFromTo(0, 2)
					* CFG.HTA()) { // t = 25 -> F = 5
				if (habitStrengthsL.get(l) > CFG.HTR()) {
					newCandidates.add(l);
				}
			}
		}
		return newCandidates;
	}
	private List<Location> filterOnIntentionsL(List<Location> hFiltered) {
		List<Location> newCandidates=new ArrayList<Location>();
		SocialPractice chosenAction= chooseOnIntentions(mySocialPractices);
		for(Location l:hFiltered){
			for (PContext affordance : chosenAction.getAffordances()) {
				if(l.getClass() == affordance.getMyLocation().getClass()) newCandidates.add(l);
			}
		}
		return newCandidates;
	}
	
	
	private <T> T pickRandomly(List<T> list){
		return list.get(RandomHelper.nextIntFromTo(0, list.size()-1));
	}
	
	
	
	private Agent pickEatBuddy(){
		List<Agent> temp =new ArrayList<Agent>(agents); 
		
		temp = filterOnAffordancesA(temp); //Easier to give a new list back and change ref.
		List<Agent> aFiltered = new ArrayList<Agent>(temp);
		if(aFiltered.isEmpty()) return null; //Niemand available
		temp = filterOnHabitsA(temp);
		List<Agent> hFiltered = new ArrayList<Agent>(temp);
		if(hFiltered.isEmpty()) temp = aFiltered; //Reroll if empty
		
		return pickRandomly(temp);

	}
	private List<Agent> filterOnAffordancesA(List<Agent> temp) {
		List<Agent> newCandidates=new ArrayList<Agent>();
		for(Agent a:temp){
			if(!a.isLocated()) newCandidates.add(a);
		}
		return newCandidates;
	}
	
	private List<Agent> filterOnHabitsA(List<Agent> aFiltered) {
		List<Agent> newCandidates=new ArrayList<Agent>();
		frequenciesA.clear();
		habitStrengthsA.clear();
		
		double totalF =0;
		for(Agent a: agents){
			double F = 0;
			for(SocialPractice sp:mySocialPractices){
				F+= sp.calculateFrequencyA(a);
			}
			frequenciesA.put(a, F);
			totalF += F;
		}
		for (Agent a: aFiltered) { //notice again cLoc vs aFilt
			habitStrengthsA.put(a, habitStrength(frequenciesA.get(a), totalF));
		}
		for(Agent a: aFiltered){
			if (frequenciesA.get(a) > RandomHelper.nextDoubleFromTo(0, 2)
					* CFG.HTA()) { // t = 25 -> F = 5
				if (habitStrengthsA.get(a) > CFG.HTR()) {
					newCandidates.add(a);
				}
			}
		}
		return newCandidates;
	}
	
	private boolean acceptInvitation(Location chosenLocation) {
		boolean accept = false;
		for(PContext affordance: chooseOnIntentions(mySocialPractices).getAffordances()){
			if(chosenLocation.getClass() == affordance.getMyLocation().getClass()) accept = true;
		}
		if(!accept){ //Sometimes even accept if it does not cater values
			if(RandomHelper.nextDoubleFromTo(0, 1) < acceptRatio){
				accept = true;
			}
		}
		return accept;
	}
	
	
	
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void chooseFood() {
		if(isEating) myAction = chooseAction();
		else{
			myAction = new NoAction(); //Maybe change to just new Social Practice which will not be an instance of either;
			actionType = ActionType.NOACTION;
		}
		act();
		updateValues();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step3(){
		if(isEating) learn();
	}
	
	
	 
	/**
	 * Moves agent to random context.
	 * Maybe put in Eater instead.
	 */
//	private void randomContext() {
		
//		if(CFG.chooseContext()){
//			if(!located){
//				ArrayList<Agent> diningGroup = new ArrayList<Agent>();
//				Location chosenRestaurant;
//				
//				System.out.print(RandomHelper.nextDouble());
//				
//				/*Eat at home*/
//				if(RandomHelper.nextDoubleFromTo(0, 1) <diningOutRatio){
//					chosenRestaurant = myHome;
//					for(int i = 0; i < CFG.nrOfPeople(); i++){
//						diningGroup.add(socialDefault());
//					}
//				}
//				/*Eat out*/
//				else{
//					/*Physical First*/
//					if(RandomHelper.nextDoubleFromTo(0, 1) <exploreRatio){
//						chosenRestaurant = physicalDefault();
//					}
//					else chosenRestaurant = exploreNew();
//				}
//				
//				
//			}
//		}
		
//		//Note that you don't add PContexts to the grid, nor move their location
//		int randomIndex = RandomHelper.nextIntFromTo(0,
//				candidateLocations.size() - 1);
//		Location randomLocation = candidateLocations.get(randomIndex);
//		myContext = (randomLocation.hasContext()) ? randomLocation.getMyContext():new PContext(candidateLocations.get(randomIndex));
//		myContext.addAgent(this);
//		Helper.moveToObject(myGrid, this, randomLocation);
//	}
	 
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
		return 1 + 0.5 * Math.tanh((x-CFG.a)/CFG.b);
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


	public boolean isLocated() {
		return isLocated;
	}


	public void setLocated(boolean isLocated) {
		this.isLocated = isLocated;
	}
}