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
	private PhysicalContext myPhysicalContext;

	/**
	 * The constructor.
	 */
	public Location(Grid<Object> grid) {
		myGrid = grid;
		ID = CFG.getLocationID();
		setMyPhysicalContext(new PhysicalContext(this));
	}
	/*
	 * Constructor for affordance placeholder.
	 */
	public Location(){
		setMyPhysicalContext(new PhysicalContext(this));
	}
	
	public PhysicalContext getMyPhysicalContext(){
		return myPhysicalContext;
	}
	
	public void setMyPhysicalContext(PhysicalContext p){
		myPhysicalContext = p;
	}
	
	//This is a bridge
	public PContext getMyContext(){
		return myPhysicalContext.getMyPContext();
	}
	
	public boolean hasContext(){
		return getMyContext() != null;
	}
}

