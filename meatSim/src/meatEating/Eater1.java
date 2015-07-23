/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import framework.Agent;
import framework.Location;
import framework.PContext;
import framework.SocialPractice;

// End of user code

/**
 * Description of Eater1.
 * 
 * @author rijk
 */
public class Eater1 extends Agent {
	
	/**
	 * The constructor.
	 */
	public Eater1(ArrayList<Agent> agents, ArrayList<Location> candidateLocations, ArrayList<Location> homes, Grid<Object> grid) {
		super(agents, candidateLocations, homes, grid);
		addSocialPractice(new MeatEatingPractice());
		addSocialPractice(new VegEatingPractice());
		double[] sampleValues = correlated();
		addValue(new SelfEnhancement(sampleValues[0], this)); //ND, 1, 0.25 (maybe higher sigma?)
		addValue(new SelfTranscendence(sampleValues[1], this));
		sampleValues = correlated();
		addValue(new Openness(sampleValues[0])); //ND defined in abstractbuilder as 1,0.25
		addValue(new Conservation(sampleValues[1]));
		
		RandomHelper.createNormal(1, 0.1);
		double conservativeSchale = sampleValues[1] -sampleValues[0];
		double conservativeSchaleN = 0.5 + (conservativeSchale)/2; //Normalized to fit 0.5-1.5
		setHabitWeight(conservativeSchaleN);
		setOCweight(RandomHelper.getNormal().nextDouble());
		
		double enhancementSchale = sampleValues[0] - sampleValues[1]; //range 4
		double enhancementSchaleN = 0.5 + (conservativeSchale+2)/4;
		
		
		
		RandomHelper.createNormal(1, 0.25);
		
		//Or iniate everything with RandomHelper.getNormal().nextDouble()
	}
	
	
	public double[] correlated(){
		double[] means = {1,1};
		double variance = 0.0625;
		double correlation = -0.8; //Could be normally distributed
		//double correlation = Math.min(0, Math.max(-1, RandomHelper.getNormal().nextDouble() - 1.5)); //I'm not sure if the correlation, should differ, that seems strange.
		double[][] covariance_matrix = {{variance, variance * correlation},{variance*correlation, variance}};
		MultivariateNormalDistribution m= new MultivariateNormalDistribution(means,covariance_matrix);
		double[] l =m.sample();
		l[0] = Math.max(0, Math.min(2, l[0]));
		l[1] = Math.max(0, Math.min(2, l[1]));
		//System.out.println("correlation: " + correlation + "gives strengtWeight:" + l[0] + "and"+ l[1]);
		return l;
		/*
		double correlation = RandomHelper.getNormal().nextDouble() -1.5;
		double x2= RandomHelper.getNormal().nextDouble();
		double y1= correlation * x1 + Math.sqrt(1 - (correlation * correlation))* x2;
		
		System.out.println("x1: " + x1);
		System.out.println("x2: " + x2);
		System.out.println("correlation: "+correlation);
		System.out.println("y1: "+y1);
		
		return y1;
		*/
	}
	
	/*DataCollectors*/
	public double dataHabitStrengthMeat(){
		return dataHabitStrength(MeatEatingPractice.class);
	}
	public double dataHabitStrengthVeg(){
		return dataHabitStrength(VegEatingPractice.class);
	}
	public double dataFrequencyIndexMeat(){
		return dataFrequencyIndex(MeatEatingPractice.class);
	}
	public double dataFrequencyIndexVeg(){
		return dataFrequencyIndex(VegEatingPractice.class);
	}
	public double dataMeatEvaluation(){
		return dataMeatAction() * dataEvaluation();
	}
	public double dataVegEvaluation(){
		return dataVegAction() *dataEvaluation();
	}
	public double dataSatisfactionSelfEnhancement(){
		return dataSatisfaction(SelfEnhancement.class);
	}
	public double dataSatisfactionSelfTranscendence(){
		return dataSatisfaction(SelfTranscendence.class);
	}
	public double dataThresholdSelfEnhancement(){
		return dataThreshold(SelfEnhancement.class);
	}
	public double dataThresholdSelfTranscendence(){
		return dataThreshold(SelfTranscendence.class);
	}
	public double dataNeedSelfEnhancement(){
		return dataNeed(SelfEnhancement.class);
	}
	public double dataNeedSelfTranscendence(){
		return dataNeed(SelfTranscendence.class);
	}
}
