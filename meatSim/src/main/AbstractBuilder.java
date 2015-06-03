/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;

import framework.Agent;
import framework.Location;
import framework.PContext;
import framework.PhysicalContext;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.PriorityType;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of AbstractBuilder.
 * 
 * @author rijk
 */
public abstract class AbstractBuilder implements ContextBuilder<Object> {
	public Context<Object> context;
	public Grid<Object> grid;
	public GridFactory myGridFactory;
	public Parameters params;	
	public ArrayList<Agent> agents;
	public ArrayList<Location> locations;
	public ArrayList<PContext>	pContexts; 
	public DataCollector myDataCollector;
	
	/**
	 * Description of the method build.
	 */
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		params = RunEnvironment.getInstance().getParameters(); /*retrieves GUI-made parameters*/
		agents=new ArrayList<Agent>();
		locations=new ArrayList<Location>();
		pContexts=new ArrayList<PContext>();
		

		setCFG();
		
		setID();
		makeGrid();
		addAgents();
		addEnvironment();
		
		//after making agents
		myDataCollector=new DataCollector(this);
		context.add(myDataCollector);
		
		/*Schedules a performance context task each timestep.*/
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params, this, "createPContexts");
		
		/*Specifies simulation endTime*/
		RunEnvironment.getInstance().endAt(CFG.endTime());
		
		return context;
	}
	 
	public abstract void setCFG();
	/**
	 * Description of the method setID.
	 */
	public abstract void setID();
	 
	/**
	 * Description of the method makeGrid.
	 */
	public void makeGrid() {
		myGridFactory = GridFactoryFinder.createGridFactory(null);
		
		grid = myGridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
				new RandomGridAdder<Object>(),
				true, 50, 50));
	}	
	 
	/**
	 * Description of the method addAgents.
	 */
	public abstract void addAgents();
	 
	/**
	 * Description of the method addEnvironment.
	 */
	public abstract void addEnvironment();

	/**
	 * Removes all previous performanceContexts
	 * Creates one performance context per location per timestep.
	 * Adds it to the list of pContexts and to the Repast context and (thus) grid.
	 * Moves the pContext to the point in the Grid of the location.
	 */
	
	//@ScheduledMethod(start = 1, interval = 1)
	public void createPContexts() {
		pContexts.clear(); //List doesn't become infinite, there are new pContexts every time step.
		
		for(int i =0; i < locations.size(); i++){
			Location location = locations.get(i);
			PContext newPContext=new PContext(new PhysicalContext(location));
			pContexts.add(newPContext);
			context.add(newPContext);
			Helper.moveToObject(grid, newPContext, location);
		}
	}
}
