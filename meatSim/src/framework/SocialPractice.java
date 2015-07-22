/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import main.CFG;
import main.Helper;






import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
// Start of user code (user defined imports)
import org.jblas.DoubleMatrix;

// End of user code

/**
 * use Hashmap with agents and venue ID
 * Make affordances a list of context objects. Each object represent a context precondition (i.e. Meat Venue).<br />
 * Less than ideal is that these objects are thus fake, they only represent a class type that will be used in checkAffordances.<br />
 * Another option would be to make a seperate checkAffordances method for each SocialPractice
 * 
 * @author rijk
 */
public abstract class SocialPractice {
	private Class<? extends Value> purpose;
	private ArrayList<PContext> affordances=new ArrayList<PContext>();
	//private ArrayList<PContext> performanceHistory = new ArrayList<PContext>();
	private HashMap<Object, Double>  performanceHistoryMap = new HashMap<Object, Double>();
	//private DoubleMatrix performanceHistoryMatrix;
	private HashMap<Object, Double> evaluationHistoryMap = new HashMap<Object, Double>();
	
	//private ArrayList<Evaluation> evaluations = new ArrayList<Evaluation>(); //Iig nu niet nodig en erg veel ruimte
	private Evaluation lastEvaluation;
	private double evaluationAvarage = 1;
	//TODO: add a lastgrade option to reduce runningtime?

	/**
	 * The constructor.
	 */
	public SocialPractice() {
		super();
	}
	
	/**
	 * Description of the method embodiment.
	 * Abstract or not?
	 */
	public void embodiment(){
		//satisfy values
	}
	 
	/**
	 * Description of the method updatePerformanceHistory.
	 */
	public void updatePerformanceHistory(PContext currentContext) {
		//performanceHistory.add(currentContext); <- requires space
		updatePerformanceHistoryMap(currentContext);
	}
	
	public void updateEvaluationHistoryMap(PContext currentContext, double grade) {
		Helper.mapLearn(evaluationHistoryMap, currentContext.getMyLocation(), grade);

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		double add = grade; //agents.size()?
		for(Agent a: agents){
			Helper.mapLearn(evaluationHistoryMap, a, add);
		}	
	}
	
	//Don't know if this is still neccesary.
	//Note that you use the last grade not the avarage
//	public void updatePerformanceHistoryEvaluative(PContext currentContext){
//		//performanceHistory.add(currentContext); <- requires space
//		double grade = lastEvaluation.getGrade();
//		updatePerformanceHistoryMap(currentContext, grade);
//	}
	 
	
	private void updatePerformanceHistoryMap(PContext currentContext) {
		Helper.mapAdd(performanceHistoryMap, currentContext.getMyLocation());

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		for(Agent a: agents){
			double add = 1.0/agents.size();
			Helper.mapAdd(performanceHistoryMap, a, add);
		}	
	}
	
	//Dont know if this is still neccesary.
//	private void updatePerformanceHistoryMap(PContext currentContext, double grade){
//		Helper.mapAdd(performanceHistoryMap, currentContext.getMyLocation(), grade);
//
//		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
//		for(Agent a: agents){
//			double add = grade/agents.size();
//			Helper.mapAdd(performanceHistoryMap, a, add);
//		}	
//	}
	
	protected void addAffordance(PContext affordance){
		getAffordances().add(affordance);
	}
	
	protected void addPurpose(Class<? extends Value> purpose){
		this.purpose = purpose;
	}
	
	public Class<? extends Value> getPurpose(){
		return purpose;
	}

	public ArrayList<PContext> getAffordances() {
		return affordances;
	}

	/*
	 * Lazy evaluation?
	 */
	public double calculateFrequency(PContext myContext, double OCweight) {
		double weightedFrequency = (1- CFG.OUTSIDE_CONTEXT(OCweight)) *getFreqInsideContext(myContext) + 
				CFG.OUTSIDE_CONTEXT(OCweight) * getFreqOutsideContext(myContext);
		return weightedFrequency;
	}
	
	//For filtering locations on habits.
	public double calculateFrequencyL(Location l){
		return performanceHistoryMap.getOrDefault(l, 0.0);
	}
	public double calculateFrequencyA(Agent a){
		return performanceHistoryMap.getOrDefault(a, 0.0);
	}
	
	private double getFreqInsideContext(PContext myContext) {
		double frequencyLocation = performanceHistoryMap.getOrDefault(myContext.getMyLocation(), 0.0);
		double frequencyAgents = 0;
		for(Agent a: myContext.getMyAgents()){
			frequencyAgents+=performanceHistoryMap.getOrDefault(a, 0.0);
		}
		return frequencyLocation + frequencyAgents;
	}

	public double calculateEvaluation(PContext myContext, double OCweight){
		if(myContext == null){
			System.out.println("Called without context");
			return getEvaluationAvarage(); //Abstraheer over context als je er geen hebt.
		}
		
		double weightedEvaluation = (1- CFG.OUTSIDE_CONTEXT(OCweight)) *getEVInsideContext(myContext)+
				CFG.OUTSIDE_CONTEXT(OCweight) * getEVOutsideContext(myContext);
		return weightedEvaluation;
	}
	
	private double getEVInsideContext(PContext myContext) {
		double evLocation = evaluationHistoryMap.getOrDefault(myContext.getMyLocation(), 1.0);
		double sumAgents = 0;
		for(Agent a: myContext.getMyAgents()){
			sumAgents+=evaluationHistoryMap.getOrDefault(a, 1.0);
		}
		double sumEvaluation = evLocation + sumAgents;
		double evaluationObjects = 1.0 + myContext.getMyAgents().size();
		return sumEvaluation/evaluationObjects;
	}
	
	private double getEVOutsideContext(PContext myContext){
		HashMap<Object, Double> temp =new HashMap<Object, Double>(evaluationHistoryMap);
		temp.remove(myContext.getMyLocation());
		for(Object o: myContext.getMyAgents()){
			temp.remove(o);
		}
		if(temp.values().isEmpty()) return 1;
		return Helper.sumDouble(temp.values())/temp.values().size();
	}
	
	private double getFreqOutsideContext(PContext myContext){
		HashMap<Object, Double> temp =new HashMap<Object, Double>(performanceHistoryMap);
		temp.remove(myContext.getMyLocation());
		for(Object o: myContext.getMyAgents()){
			temp.remove(o);
		}
		return Helper.sumDouble(temp.values());
	}
	
	//Don't know if these our neccessary. Maybe for data or something.
	public void addEvaluation(Evaluation ev) {
		//System.out.println("Evaluation: "+ev.getGrade());
		lastEvaluation = ev; 
		evaluationAvarage = (1 - CFG.LEARN_RATE()) *evaluationAvarage + CFG.LEARN_RATE() * ev.getGrade();
		updateEvaluationHistoryMap(ev.getContext(),ev.getGrade());
	}

	public double getEvaluationAvarage(){
		return evaluationAvarage;
	}
	/*
	 * If Evaluation isn't use return a 0-Evaluation.
	 */
	public Evaluation getLastEvaluation(){
		if(lastEvaluation == null) return new Evaluation(0,0,0,0, null);
		else{
			return lastEvaluation;
		}
	}
}
