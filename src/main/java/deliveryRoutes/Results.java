package deliveryRoutes;

import EvolutionaryAlgorithm.Chromosome;
import EvolutionaryAlgorithm.ReferencePoint;

import java.io.Serializable;

public class Results implements Serializable{
	private int vehicles;
	private double fixedCost;
	private double runningCost;
	private double emissions;
	private double maxTime;
	private double distance;


	private int paretoRank;
	double sparsity;
	private Chromosome myCh;
	private ReferencePoint myRef;
	private double distanceFromRef;
	private double myReferenceCost;
	private double myReferenceEmission;
	private double myReferenceTime;
	private double myReferenceVehicle;
	private double myReferenceDistance;
	private String myKey;
	private int myAge;


	public Results(){
	    paretoRank = -1;
	    sparsity = 0.0;
	    myRef = null;
	    distanceFromRef = Double.MAX_VALUE;
    }

	public void setMyAge(int myAge) {
		this.myAge = myAge;
	}

	public int getMyAge() {
		return myAge;
	}

	public void setMyKey(String myKey) {
		this.myKey = myKey;
	}

	public String getMyKey() {
		return myKey;
	}

	public int getVehicles() {
		return vehicles;
	}
	public void setVehicles(int vehicles) {
		this.vehicles = vehicles;
	}
	public double getFixedCost() {
		return fixedCost;
	}
	public void setFixedCost(double fixedCost) {
		this.fixedCost = fixedCost;
	}
	public double getRunningCost() {
		return runningCost;
	}
	public void setRunningCost(double runningCost) {
		this.runningCost = runningCost;
	}
	public double getEmissions() {
		return emissions;
	}
	public void setEmissions(double emissions) {
		this.emissions = emissions;
	}
	public double getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getTotalCost() {
		return this.getFixedCost() + this.getRunningCost();
	}

	public void setParetoRank(int paretoRank){
	    this.paretoRank = paretoRank;
    }

    public int getParetoRank() {
        return paretoRank;
    }

    public void setMyCh(Chromosome myCh) {
        this.myCh = myCh;
    }

    public Chromosome getMyCh() {
        return myCh;
    }

    public void setSparsity(double sparsity) {
        this.sparsity = sparsity;
    }

    public double getSparsity() {
        return sparsity;
    }

    public boolean updateReference( ReferencePoint ref , double distance){
		if(distance<distanceFromRef){
			myRef = ref;
			distanceFromRef =distance;
			return true;
		}
		return false;
	}
	public void clearReference(){
		myRef = null;
		distanceFromRef =Double.MAX_VALUE;
		myReferenceCost=0;
		myReferenceEmission =0;
	}

	public void setMyRef(ReferencePoint myRef) {
		this.myRef = myRef;
	}

	public ReferencePoint getMyRef() {
		return myRef;
	}

	public double getDistanceFromRef() {
		return distanceFromRef;
	}

	public void setMyReferenceCost(double myReferenceCost) {
		this.myReferenceCost = myReferenceCost;
	}

	public double getMyReferenceCost() {
		return myReferenceCost;
	}

	public void setMyReferenceEmission(double myReferenceEmission) {
		this.myReferenceEmission = myReferenceEmission;
	}

	public double getMyReferenceEmission() {
		return myReferenceEmission;
	}

	public void setMyReferenceDistance(double myReferenceDistance) {
		this.myReferenceDistance = myReferenceDistance;
	}

	public void setMyReferenceTime(double myReferenceTime) {
		this.myReferenceTime = myReferenceTime;
	}

	public void setMyReferenceVehicle(double myReferenceVehicle) {
		this.myReferenceVehicle = myReferenceVehicle;
	}

	public double getMyReferenceVehicle() {
		return myReferenceVehicle;
	}

	public double getMyReferenceDistance() {
		return myReferenceDistance;
	}

	public double getMyReferenceTime() {
		return myReferenceTime;
	}

	public String toString(){
		return "Vehicles = " + vehicles +"\n" +
				"Fixed cost = " + fixedCost +"\n" +
				"Running cost = " + runningCost + "\n" +
				"Emissions = " + emissions + "\n" +
				"Max Time = " + maxTime + "\n" +
				"Distance = " + distance +"\n";
		
	}
}
