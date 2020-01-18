package GeoUtils;

import java.util.HashMap;

public class SimpleRouter implements JourneyFactory {
	//A very simple router that uses the Haversine formula to construct straight line route between two locations
	
	private int _speedKMH; //Used to calulate travel time
	
	public SimpleRouter(int speed) {
		this._speedKMH = speed;
	}

	@Override
	public Journey[] getJourney(Location start, Location finish) {
		
		
		Journey[] j = new Journey[1];
		j[0] = new Journey(start,finish,"Euclidean distance.");
		j[0].setDescription("Euclidean distance, calculated using the Haversine formula.");
		j[0].setDistanceKM(Useful.haversine(start, finish));
		double t = j[0].distanceKM / this._speedKMH;
		t = t * 3600000;//convert hours to msec
		
		j[0].setTravelTimeMS(t);
		
		return j;
	}

	@Override
	public Journey[] getJourney(Location start, Location finish,HashMap<String, Object> options) {
		//No options for this simple router
		return this.getJourney(start, finish);
	}

	@Override
	public void setAlternative(JourneyFactory alternative) {
		//No need for an alternative as this Router will always work - Euclidean distance can always be found.
		
	}

}
