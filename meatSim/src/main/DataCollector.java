package main;


import meatEating.MeatEatingPractice;
import meatEating.VegEatingPractice;
import framework.Agent;

//Needs to be added to the context to be able to collect data from.
public class DataCollector {
	AbstractBuilder main;
	public DataCollector(AbstractBuilder abstractBuilder) {
		main = abstractBuilder;
	}
	
	public int countVegAction(){
		int c=0;
		for(Agent a:main.agents){
			if(a.getMyAction() instanceof VegEatingPractice) c++;
		}
		return c;
	}
	
	public int countMeatAction(){
		int c=0;
		for(Agent a:main.agents){
			if(a.getMyAction() instanceof MeatEatingPractice) c++;
		}
		return c;
	}
}
