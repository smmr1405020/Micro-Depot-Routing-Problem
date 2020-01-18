package GeoUtils;

import java.util.HashMap;

/*
 * Used to specify the interface for any object that constructs Journey objects
 * The main purpose is to ensure that we can construct journeys from multiple sources
 * 
 * If a factory cannot construct a journey for any reason then NULL should be returned.
 * 
 *  Neil Urquhart 22/2/17
 * 
 */
public interface JourneyFactory {
	
	public Journey[] getJourney(Location start, Location finish);
	//Construct a basic journey between two locations
	//Some routers may return more than one option, hence an array is returned.
	
	public Journey[] getJourney(Location start, Location finish, HashMap<String, Object> options);
	//Some Factories will require extra parameters such as travel modes etc. options should contain appropriate key(String) value(object)
	//pairs which will be interpreted by the factory. Not all factories will recognise all options and will ignore any options that they 
	//do not "understand".
	//Some routers may return more than one option, hence an array is returned.
	
	public void  setAlternative(JourneyFactory alternative);
	
}