package GeoUtils;
import java.util.HashMap;


/*
 * A basic class to represent a journey that links two locations
 * 
 * Neil Urquhart 21/2/17
 */
public class Journey {
	
	/*
	* The journey takes place between A and B 
	*/
	protected Location locationA;
	protected Location locationB;
	
	protected double distanceKM=-1; 
	// The distance travelled in KM 
	protected double travelTimeMS=-1; 
	// Traveltime in MSecs

	protected String description=null; 
	// An optional description of the journey
	protected String source=null; 
	//An optional source of the journey data 
	
	protected HashMap<String,Object> attributes = new HashMap<String,Object>();
	//Additional attributes stored in attributes.  Allowable keys are defined below
	
	public static final String PATH = "path"; //A List of Locations representing the path
	public static final String INSTRUCTIONS = "instructions"; // A List of Strings representing instructions to the traveller
	public static final String FROM_CALENDAR_ENTRY = "from_calendar_entry";  //A 
	public static final String TO_CALENDAR_ENTRY = "to_calendar_entry";
	
	
	/*
	* Constructor
	*/
	public Journey(Location a, Location b, String source){
		locationA = a;
		locationB = b;
		this.source = source;
	}

	/*
	 * Accessor Methods
	 * 
	 */
	public double getDistanceKM() {
		return distanceKM;
	}

	public void setDistanceKM(double distanceKM) {
		this.distanceKM = distanceKM;
	}

	public double getTravelTimeMS() {
		return travelTimeMS;
	}

	public void setTravelTimeMS(double travelTimeMS) {
		this.travelTimeMS = travelTimeMS;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Location getPointA() {
		return locationA;
	}

	public Location getPointB() {
		return locationB;
	}
	
	public Object getAttribute(String key){
		return this.attributes.get(key);
	}
	
	public void putAttribute(String key, Object value){
		this.attributes.put(key, value);
	}
	/*
	 * ToString
	 * 
	 */
	public String toString(){
		String buffer ="";
		
		buffer = locationA + " : " + locationB;
		
		if (description != null)
			buffer += ", " +description;
		
		if (source != null)
			buffer += ", " +source;
		
		return buffer;
	}
}
