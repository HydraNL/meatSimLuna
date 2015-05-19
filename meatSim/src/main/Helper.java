/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import framework.Location;
import framework.PContext;
import framework.SocialPractice;

// Start of user code (user defined imports)

// End of user code

/**
 * the Builder creates one context object per venue per round<br />
 * make sure the Builder does this before an agent moves
 * 
 * @author rijk
 */
public class Helper {
	// Start of user code (user defined attributes for Helper)
	
	// End of user code
	
	/**
	 * The constructor.
	 */
	public Helper() {
		// Start of user code constructor for Helper)
		super();
		// End of user code
	}
	/**
	 * Move one object to the same location as another on the grid.
	 * 
	 * @param grid Grid objects move on
	 * @param movingObject object that moves
	 * @param targetObject object where movingObject moves to
	 */
	public static void moveToObject(Grid<Object> grid, Object movingObject,
			Object targetObject) {
		GridPoint pt = grid.getLocation(targetObject);
		grid.moveTo(movingObject, pt.getX(), pt.getY());
	}
	
	/**
	 * Move one object to the same location as another on the grid.
	 * 
	 * @param grid Grid objects move on
	 * @param movingObject object that moves
	 * @param targetObject object where movingObject moves to
	 */
	public void moveNextToObject(Grid<Object> grid, Object movingObject,
			Object targetObject) {
		//TODO: make
		//GridPoint pt = grid.getLocation(targetObject);
		//grid.moveTo(movingObject, pt.getX(), pt.getY());
	}
	
	
	public static void filter(
			ArrayList<SocialPractice> candidateSocialPractices,
			HashMap<SocialPractice, Double> habitStrengths, double HABIT_THRESHOLD) {
		Iterator<SocialPractice> iter = candidateSocialPractices.iterator();
		
		while(iter.hasNext()){
			SocialPractice sp = iter.next();
			if(habitStrengths.get(sp) < HABIT_THRESHOLD) iter.remove(); 
		}
		
	}
	public static int sum(Collection<Integer> values) {
		int sum = 0;
		for(Integer i:values){
			sum = sum + i;
		}
		return sum;
	}
	
	public static double sumDouble(Collection<Double> values) {
		double sum = 0;
		for(Double i:values){
			sum = sum + i;
		}
		return sum;
	}
	
	//Wrapper
	public static void mapAdd(HashMap<Object, Double> map,
			Object key){
		mapAdd(map, key, 1);
	}
	
	/*
	 * Adds 'add'-points to HashMap entry;
	 */
	public static void mapAdd(HashMap<Object, Double> map,
			Object key, double add) {
		Double currentValue = map.get(key);
		if(currentValue == null){		//no Entry yet
			map.put(key, add);
		}
		else{
			currentValue+=add;
			map.put(key, currentValue);
		}
	}
	
	
	
	// Start of user code (user defined methods for Helper)
	
	// End of user code


}
