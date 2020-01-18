package deliveryRoutes;

import GeoUtils.Location;

import java.io.Serializable;
import java.util.ArrayList;


public class Vehicle implements Cloneable, Serializable{
	private String description;
	private double emissions;
	private double fixedCost;
	private double costKM;
	private int speed;
	private int capacity;
	private int myId;
	private Location start;
	private String type;//car,bike or walk
	//private Journey[] myRoute;
	
	private ArrayList<Delivery> deliveries;//Holds the deliveries allocated to this van
	
	//Automatically increment the vehicle ID and create an origin point for the delivery
	public Vehicle(String type)
	{
		this.type  = type;
		deliveries = new ArrayList<Delivery>();
		//start = new Location(x,y,z);
	}
	
	public String getType(){
		return type;
	}
	public void setID(int id){
		myId = id;
	}

	public void clearDeliveries(){
		this.deliveries.clear();
	}
	public double getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(double fixedCost) {
		this.fixedCost = fixedCost;
	}

	public double getCostKM() {
		return costKM;
	}

	public void setCostKM(double costKM) {
		this.costKM = costKM;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



	public void startLocation(double x, double y, double z)
	{
		start = new Location(x,y,z);
	}
	
	public boolean addDelivery(Delivery d) {
		if (this.getSpareCapacity() >= d.getWeight()) {
			deliveries.add(d);
			return true;
		}
		return false;
	}
	
	public int getSpareCapacity() {
		int used=0;
		for(Delivery d : deliveries)
			used += d.getWeight();
		
		return this.capacity - used;
	}
	
	//Getters and setters for the variables
	public double getEmissions() {
		return emissions;
	}
	public void setEmissions(double emissions) {
		this.emissions = emissions;
	}
	
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public  int getId() {
		return myId;
	}

	public ArrayList<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ArrayList<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public Location getStart() {
		return start;
	}

	public void setStart(Location start) {
		this.start = start;
	}
	
	
	
	public Object clone() {
		  try {
			  Vehicle res = (Vehicle) super.clone();
			  res.deliveries = new ArrayList<Delivery>(this.deliveries);
		   return res;
		 }
		  catch (CloneNotSupportedException e) {
		   System.out.println("CloneNotSupportedException comes out : "+e.getMessage());
		   return null;
		  }
		 }
		  
		  /*
	private  ArrayList<Delivery> nearestNeighbour()
	{
		//Sort into nearest neighbour order from currentLocation
		ArrayList<Delivery> ordered  = new ArrayList<Delivery>();
		//Dummy delivery start
		Delivery current = new Delivery(start,"start");//Dummy delivery representing the start
		Journey[] res;
		while(deliveries.size() > 0)
		{
			double bestD = Double.MAX_VALUE;
			Delivery next = null;
			for (Delivery possible: deliveries)
			{
				res = createpath.getJourney(current.getLocation(), possible.getLocation());
				double tempD = Double.MAX_VALUE;
				if (res != null)
				{
					tempD = res[0].getDistanceKM();
				}
				if (tempD < bestD)
				{
					bestD = tempD;
					next = possible;
				}
			}
			ordered.add(next);
			destinations.remove(next);
			current = next;
		}
		return ordered;
	}*/
}
