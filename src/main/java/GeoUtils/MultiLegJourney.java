package GeoUtils;

import java.util.ArrayList;

public class MultiLegJourney extends Journey {
	private ArrayList<Journey> legs = new ArrayList<Journey>();
	
	public MultiLegJourney() {
		super(null,null,"");
	}
	

	
	public void addLeg(Journey j){
		legs.add(j);
	}
	
	public ArrayList<Journey> getLegs(){
		return legs;
	}
	
	public double getDistanceKM() {
		double tot=0;
		for(Journey leg : legs)
			tot += leg.distanceKM;
		return tot;
	}

	
	public double getTravelTimeMS() {
		double tot=0;
		for(Journey leg : legs)
			tot += leg.travelTimeMS;
		return tot;
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
		return legs.get(0).getPointA();
	}

	public Location getPointB() {
		return legs.get(legs.size()-1).getPointB();
	}
	
	/*
	 * ToString
	 * 
	 */
	public String toString(){
		String buffer ="";
		
		buffer = this.getPointA() + " : " + this.getPointB();
		
		if (description != null)
			buffer += ", " +description;
		
		if (source != null)
			buffer += ", " +source + "\n";
		int l=1;
		for(Journey leg : legs){
			buffer += "\t"+l+" "+leg.toString()+"\n";
			l++;
			}
		return buffer;
	}

}
