package framework;

public class PhysicalContext {
	private Location myLocation;
	
	public PhysicalContext(Location myLocation){
		this.setMyLocation(myLocation);
	}

	public Location getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(Location myLocation) {
		this.myLocation = myLocation;
	}
}
