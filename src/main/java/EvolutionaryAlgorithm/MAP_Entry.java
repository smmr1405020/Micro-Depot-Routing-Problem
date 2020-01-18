package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.io.Serializable;

class MAP_Entry implements Serializable{
	public MAP_Entry(Results r, Chromosome c){
		myResults=r;
		myChromo =c;
		count=0;
	}
	public Results myResults;
	public Chromosome myChromo;
	public int improves;
	public String key;
	public int count;

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return myResults.getVehicles() +"," +myResults.getFixedCost()+","+myResults.getRunningCost()+","+myResults.getEmissions()+","+myResults.getMaxTime()+","+myResults.getDistance();
	}
}
