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
	private ArrayList<PContext> performanceHistory = new ArrayList<PContext>();
	private HashMap<Object, Double>  performanceHistoryMap = new HashMap<Object, Double>();
	private DoubleMatrix performanceHistoryMatrix;
	
	private ArrayList<Evaluation> evaluations = new ArrayList<Evaluation>();
	private double evaluationSum = 0;
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
		performanceHistory.add(currentContext);
		updatePerformanceHistoryMap(currentContext);
	}
	
	public void updatePerformanceHistoryEvaluative(PContext currentContext){
		performanceHistory.add(currentContext);
		double grade = evaluations.get(evaluations.size() -1).getGrade();
		updatePerformanceHistoryMap(currentContext, grade);
	}
	 
	//TODO: update mapAdd st it adds not 1 but the amount of grade points to the location and agents
	private void updatePerformanceHistoryMap(PContext currentContext) {
		Helper.mapAdd(performanceHistoryMap, currentContext.getMyLocation());

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		for(Agent a: agents){
			double add = 1.0/agents.size();
			Helper.mapAdd(performanceHistoryMap, a, add);
		}	
	}
	
	private void updatePerformanceHistoryMap(PContext currentContext, double grade){
		Helper.mapAdd(performanceHistoryMap, currentContext.getMyLocation(), grade);

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		for(Agent a: agents){
			double add = grade/agents.size();
			Helper.mapAdd(performanceHistoryMap, a, add);
		}	
	}
	
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
	public double calculateFrequency(PContext myContext) {
		double totalFrequency = Helper.sumDouble(performanceHistoryMap.values());
		double freqInsideContext = getFreqInsideContext(myContext);
		double freqOutsideContext = totalFrequency - freqInsideContext;
		double weightedFrequency = freqInsideContext + CFG.OUTSIDE_CONTEXT() * freqOutsideContext;
		return weightedFrequency;
	}

	private double getFreqInsideContext(PContext myContext) {
		double frequencyLocation = performanceHistoryMap.getOrDefault(myContext.getMyLocation(), 0.0);
		double frequencyAgents = 0;
		for(Agent a: myContext.getMyAgents()){
			frequencyAgents+=performanceHistoryMap.getOrDefault(a, 0.0);
		}
		return frequencyLocation + frequencyAgents;
	}

	public void addEvaluation(Evaluation ev) {
		evaluations.add(ev); //TODO: Maybe save per context
		evaluationSum+= ev.getGrade();
	}

	public double getEvaluationAvarage(){
		return evaluationSum/ (double) evaluations.size();
	}

	public Evaluation getLastEvaluation(){
		return evaluations.get(evaluations.size() -1);
	}
}
