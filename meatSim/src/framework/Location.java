/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import main.CFG;
import repast.simphony.space.grid.Grid;

/**
 * Description of PhysicalContext.
 * 
 * @author rijk
 */
public abstract class Location {
	private int ID;
	private Grid<Object> myGrid;

	/**
	 * The constructor.
	 */
	public Location(Grid<Object> grid) {
		myGrid = grid;
		ID = CFG.getLocationID();
	}
	/*
	 * Constructor for affordance placeholder.
	 */
	public Location(){
		
	}
}
