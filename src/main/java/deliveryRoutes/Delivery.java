package deliveryRoutes;

import GeoUtils.Location;

public class Delivery {

	private static int idCounter;//Used to keep track of all IDs issued
	
	private int weight;
	private Location destination; //The actual location that the delivering is being made to
	private int id;
	private double time = 0;
	private boolean delivered = false;

	//Automatically increment the ID of the delivery and create a destination for the delivery
	public Delivery(double x, double y, double z, String description)
	{
		idCounter++;
		id = idCounter;//set the delivery id
		destination = new Location(x,y,z);
		destination.setDescription(description);
	}
	
	public Delivery(Location l, String desc){
		this(l.getLat(),l.getLon(),0,desc);
	}

	//Getters and setters for the variables
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public Location getLocation() {
		return destination;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	public String toString(){
		return this.destination +","+this.time;
	}
	
}
