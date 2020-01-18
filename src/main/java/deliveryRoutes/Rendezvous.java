package deliveryRoutes;

import GeoUtils.Location;

import java.util.ArrayList;


public class Rendezvous extends Delivery {
	//Represents a set of deliveries which are to be transferred en-bloc to another vehicle

	//private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	private Vehicle myVehicle = null;
	
	public Rendezvous(double x, double y, double z, Vehicle v) {
		super(x,y,z,"RV Point - Vehicle "+ v.getId() + "(" + v.getDescription()+")");
		myVehicle = v;
		Location start = new Location(x,y,z);
		v.setStart(start);
	}
	public Rendezvous(Location l, Vehicle v) {
		super(l.getLat(),l.getLon(),l.getAlt(),"RV Point");
		myVehicle = v;
		myVehicle.setStart(l);
	}
	public boolean includeDelivery(Delivery d) {
		//deliveries.add(d);
		return myVehicle.addDelivery(d);
	}
	
	public ArrayList<Delivery> getDeliveries(){
		return myVehicle.getDeliveries();
	}
	
	public int getWeight() {//Overidden
		int w=0;
		for(Delivery d : myVehicle.getDeliveries()) {
			w+= d.getWeight();
		}
		return w;
	}
	
	public void setVehicle(Vehicle v) {
		this.myVehicle = v;
	}
	
	public Vehicle getVehicle() {
		return this.myVehicle;
	}
	/*
	 * Transfer all of the deliveries to v
	 * 
	 * If v does not have sufficient capacity... transfer as many as
	 * will fit, then return false - getDeliveries() will return the remaining.
	 */
	/*
	public boolean transfer(Vehicle v) throws Exception {
		while(deliveries.size()>0) {
			Delivery d = deliveries.get(0);
			if (v.addDelivery(d))
				deliveries.remove(d);//remove if transferred
			else
				return false;//v full
		}
		return true;//All deliveries transferred
		
	}*/
	
}
