package GeoUtils;

import java.io.Serializable;

/*
 * An abstract class that defines a geographical location.
 * The basic coordinate system is lattitude and longitude.
 * 
 * Neil Urquhart 21/2/17
 * 
 */
public class Location implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -683917161904070450L;
	//Base location. Set by constructor and not changed
	protected double lat = 0;
	protected double lon =0;
	protected double alt = 0;
	
	//A Desctiption of the point
	protected String description=null;
	
	//Holds a string that denotes what data source supplied the point
	protected String source=null;
	
	//Constructor
	/*
	 * Thorw an illegal argument exception, if we try to setup a location
	 * that is not legal within the lat/lon system
	 */
	public Location(double lat, double lon, double alt) throws IllegalArgumentException{
		//Validate
		if ((lat < -90)||(lat > 90))
			throw new IllegalArgumentException();
		
		if ((lon < -180)||(lon > 180))
			throw new IllegalArgumentException();
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
	}
	
	public Location(double lat, double lon, String source)
			throws IllegalArgumentException {
		// Validate
		if ((lat < -90) || (lat > 90))
			throw new IllegalArgumentException();

		if ((lon < -180) || (lon > 180))
			throw new IllegalArgumentException();
		this.lat = lat;
		this.lon = lon;
		this.alt = 0;
		this.source = source;
	}
	
	public Location(double lat, double lon) throws IllegalArgumentException{
		//Validate
		if ((lat < -90)||(lat > 90))
			throw new IllegalArgumentException();
		
		if ((lon < -180)||(lon > 180))
			throw new IllegalArgumentException();
		this.lat = lat;
		this.lon = lon;
		this.alt = 0;
	}
	
	public Location() {
		
	}

	//Accessors
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

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}	
	
	public double getAlt(){
		return alt;
	}
	
	public void setLat (double lat) throws IllegalArgumentException{
		if ((lat < -90)||(lat > 90))
			throw new IllegalArgumentException();
		
		this.lat = lat;
	}
	
	public void setLon (double lon) throws IllegalArgumentException{
		if ((lon < -90)||(lon > 90))
			throw new IllegalArgumentException();
		
		this.lon = lon;
	}
	
	public void setAlt (double alt) throws IllegalArgumentException{
		if ((alt < -90)||(alt > 90))
			throw new IllegalArgumentException();
		
		this.alt = alt;
	}
	
	public boolean hasSameLocationAs(Location location) {
		if (location.getLat() == this.getLat()
				&& location.getLon() == this.getLon())
			return true;
		return false;
	}
	
	/*
	 * To String
	 */
	public String toString(){
		String buffer;
		buffer = lat +"," + lon;
		
		if (description != null)
			buffer += ", " + description;
		
		if (source != null)
			buffer += ", " + source;
			
		return buffer;
	}
}
