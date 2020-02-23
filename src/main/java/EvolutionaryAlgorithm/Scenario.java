package EvolutionaryAlgorithm;

import GeoUtils.Journey;
import GeoUtils.Location;
import deliveryRoutes.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class Scenario {
	/*
	 * Singleton
	 */
	
	private Scenario(){ }//Blocked constructor
	private static Scenario instance;
	
	public static Scenario getInstance(){
		if (instance==null)
			instance = new Scenario();
		return instance;
	}

	private  ArrayList<Location> rvLocations = new ArrayList<Location>();
	private  ArrayList<Vehicle>rvVehicles = new ArrayList<Vehicle>();
	private  ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	public Vehicle primaryVehicle = null;
	private final int MAX_VEHICLES =10;
	private  Random rnd = new Random();
	private String workingDir = ".";//Working directory 
	private String osmFile = Main.OSMFILE_NAME;//default
	private boolean twoOpt = true;// use 2-opt
	
	public void setTwoOpt(boolean to) {
		twoOpt = to;
		
	}
	public int getNoVehicleTypes() {
		return rvVehicles.size();
	}
	
	public Location getRVLocation(int idx) {
		return rvLocations.get(idx);
	}
	
	public ArrayList<Location> getRVLocations(){
		return rvLocations;
	}
	
	public ArrayList<Vehicle> getVehicles(){
		return rvVehicles;
	}
	
	public String getOsmFile() {
		return osmFile;
	}

	public void setOsmFile(String osmFile) {
		this.osmFile = osmFile;
	}

	public String getWorkingDir() {

		System.out.println(workingDir);
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public int getMAX_VEHICLES() {
		return MAX_VEHICLES;
	}
	
	public int getRndRVLocatioIndx(){
		return rnd.nextInt(rvLocations.size());
	}
	
	public Vehicle getRVVehicle(int idx){
		return rvVehicles.get(idx);
	}

	public double getHighestPolluter(){
		double res = primaryVehicle.getEmissions();
		for(Vehicle v : rvVehicles)
			if(v.getEmissions()>res){
				res = v.getEmissions();
			}
		return res;
	}
	
	public double getMostExpensiveRun(){
		double res = primaryVehicle.getCostKM();
		for(Vehicle v : rvVehicles)
			if(v.getCostKM()>res){
				res = v.getCostKM();
			}
		return res;
	}
	
	public double getCheapestRun(){
		double res = primaryVehicle.getCostKM();
		for(Vehicle v : rvVehicles)
			if(v.getCostKM()<res){
				res = v.getCostKM();
			}
		return res;
	}
	
	public double getCheapestFixed(){
		double res = primaryVehicle.getFixedCost();
		for(Vehicle v : rvVehicles)
			if(v.getFixedCost()<res){
				res = v.getFixedCost();
			}
		return res;
	}
	
	public double getMostExpensiveFixed(){
		double res = primaryVehicle.getFixedCost();
		for(Vehicle v : rvVehicles)
			if(v.getFixedCost()>res){
				res = v.getFixedCost();
			}
		return res;
	}
	
	public double getSlowest(){
		double res = primaryVehicle.getSpeed();
		for(Vehicle v : rvVehicles)
			if(v.getSpeed()<res){
				res = v.getSpeed();
			}
		return res;
	}
	
	public double getQuickest(){
		double res = primaryVehicle.getSpeed();
		for(Vehicle v : rvVehicles)
			if(v.getSpeed()>res){
				res = v.getSpeed();
			}
		return res;
	}
	
	
	public  Results evaluate(ArrayList<Gene> chromo, boolean silent, String name) {
		MAPElites_change1.TOTAL_EVAL++;
		//Setup a plan based on the gene and evaluate it.
		Model m = new Model();
		primaryVehicle.clearDeliveries();
		m.addVehicle(primaryVehicle);
		ArrayList<Delivery> plan = new ArrayList<Delivery>();
		plan.addAll(deliveries);
		primaryVehicle.setDeliveries(plan);
		//Process Gene
		int vehicleID=0;
		for(Gene g : chromo){
			Location l = (Location) rvLocations.get(g.location);
			
			//g.insertPoint = findNearest(l,plan);
			
			
			//if (g.insertPoint < plan.size()){	//There is a possibility of the insrt point being off the end ofthe plan as the plan gets smaller as deliveries 
				vehicleID++;								//are passed to sub vehicles
				//1. insert
				Vehicle v = (Vehicle)g.rvVehicle.clone();
				v.setID(vehicleID);
				v.clearDeliveries();
				Rendezvous rv = new Rendezvous(l,v);
				boolean added=false;
				//2. Transfer deliveries
				/* Old way
				boolean added=false;
					for (int c=0; c < g.delCount; c++){
						if (g.insertPoint+1 < plan.size()){
							Delivery d = plan.get(g.insertPoint+1);
							if (!(d instanceof Rendezvous)){
								if (v.addDelivery(d)){
									plan.remove(d);//Only remove, if transferred
									added=true;
								}
							}

						}
					}
					}
				*/
				//New way
				Location current = l;
				for (int c=0; c < g.delCount; c++){
					
					Delivery d = findClosest(l,plan,v.getType());
						if (!(d instanceof Rendezvous)){
							if (v.addDelivery(d)){
								plan.remove(d);//Only remove, if transferred
								l = d.getLocation();
								added=true;
							}
						}

					}
				
				if (added) {
					g.insertPoint = findNearest(l,plan);//Update insert point
					plan.add(g.insertPoint +1, rv);//should this be insertPoint + 1?
					//this is for the supply van who will drop deliveries to a micro depot
				}
				
			//}
		}
		return m.run(silent,name);
	}
	private Delivery findClosest(Location l, ArrayList<Delivery> plan, String routeType) {
		Delivery res=null;
		double best = Double.MAX_VALUE;
		for (Delivery d : plan){
			//Location y = d.getLocation();
			double dist = OSMHelper.getJourney(l,d.getLocation(),routeType).getDistanceKM();
			if (dist < best){
				best =dist;
				res=d;
			}
		}
		return res;
	}
	
	private int findNearest(Location l, ArrayList<Delivery> plan) {
		int res=0;
		double best = Double.MAX_VALUE;
		for (int c=0; c < plan.size()-1; c++){
			Location x =plan.get(c).getLocation();
			Location y = plan.get(c+1).getLocation();
			double dist = OSMHelper.getJourney(x,l,"car").getDistanceKM() + OSMHelper.getJourney(l,y,"car").getDistanceKM();
			if (dist < best){
				best =dist;
				res=c;
			}
		}
		return res;
	}

	public  void setup(File scenarioFile){

		rvLocations.clear();
		rvVehicles.clear();
		deliveries.clear();
		primaryVehicle=null;
		workingDir=".";
		osmFile = Main.OSMFILE_NAME;
		twoOpt = true;

		ScenarioCSVReader reader = new ScenarioCSVReader();
		reader.readFile(scenarioFile, this);
		deliveries = this.NNSort(primaryVehicle.getStart(),deliveries,"car");
		deliveries = this.twoOptSort(primaryVehicle.getStart(),deliveries,"car");
		
	}
	
	public void addRVlocation(Location l){
		rvLocations.add(l);
	}
	
	public void addRVvehicles(Vehicle v){
		rvVehicles.add(v);
	}
	public void addelivery(Delivery d){
		deliveries.add(d);
	}
	
	public void setPrimaryVehicle(Vehicle v){
		primaryVehicle = v;
	}
	
	public ArrayList<Delivery> NNSort(Location startP,ArrayList<Delivery> deliveries, String type){
		//System.out.println("NN sort");
		ArrayList<Delivery> result = new ArrayList<Delivery>();
		Delivery currentLoc = new Delivery(startP,"");
		double dist=0;
		double shortest = Double.MAX_VALUE;
		Delivery best=null;
		
		while (deliveries.size()> 0){
			//if ((deliveries.size() % 100) ==0) System.out.println("NN " +deliveries.size());
			best=null;
			shortest = Double.MAX_VALUE;
			
			for(Delivery d : deliveries){
				Journey j= OSMHelper.getJourney(currentLoc.getLocation(), d.getLocation(),type);
				dist = j.getDistanceKM();
				if (dist < shortest){
					shortest = dist;
					best =d;
				}
			}
			currentLoc = best;
			deliveries.remove(best);
			result.add(best);
		}
		//System.out.println("Dist="+this.getLen(startP, result,"car"));
		return result;
	}
	
	public ArrayList<Delivery> twoOptSort(Location startP,ArrayList<Delivery> deliveries, String type){ //2OPt       
		if (! twoOpt) 
			return deliveries;// don't use 2opt
		boolean improved = true;
		//System.out.println("2-opt");
//		repeat until no improvement is made {
		while(improved == true){
				improved= false;
			   double best_distance = this.getLen(startP, deliveries,type);
			   for(int i=0; i< deliveries.size(); i++){
				   //if ((i%100)==0)System.out.println("2opt " + i );
		   			
		           for (int k = i + 1; k < deliveries.size(); k++) {
		        	   
		        	   
		        	   ArrayList<Delivery> new_route = twoOptSwap(deliveries,i,k);
		        	   double new_distance = this.getLen(startP, new_route,type);
		               if (new_distance < best_distance) {
		            	   //System.out.println("Improved "+new_distance);
		            	   deliveries = new_route;
		                   improved = true;
		                   best_distance = new_distance;
		                   break;
		               }
		           }
		           if (improved)
		        	   break;
		       }
		   }
		//System.out.println("Done "+ this.getLen(startP, deliveries, type));
		return deliveries;
	}

	public ArrayList<Delivery>  twoOptSwap(ArrayList<Delivery> route, int i, int k) {
		ArrayList<Delivery> new_route = new ArrayList<Delivery>();
		Delivery[] old = new Delivery[route.size()];
		old = route.toArray(old);
		//	       1. take route[0] to route[i-1] and add them in order to new_route
			for(int c=0; c <= i; c++)
				new_route.add(old[c]);
		
		//	       2. take route[i] to route[k] and add them in reverse order to new_route
			for(int c= k; c > i; c--)
				new_route.add(old[c]);
		
		//	       3. take route[k+1] to end and add them in order to new_route
			for(int c= k+1; c < route.size(); c++)
				new_route.add(old[c]);
		
	return new_route;
	}

	/*
	private ArrayList<Delivery> sort(Location startP,ArrayList<Delivery> deliveries){
		//Climber
		double len = getLen(startP, deliveries);
		int lastCh=0;
		while(lastCh <300){
			//swap
			int out = rnd.nextInt(deliveries.size());
			int in = rnd.nextInt(deliveries.size());
			deliveries.add(in,deliveries.remove(out));
			double newL = getLen(startP, deliveries);
			if(newL > len){
				deliveries.add(out,deliveries.remove(in));
				lastCh++;
			}else{
				len = newL;
				System.out.println(newL);
				lastCh =0;
			}
			
		}
		
		
		return deliveries;
	}*/

	private double getLen(Location startP,ArrayList<Delivery> deliveries, String mode){
	Delivery start = new Delivery(startP,"");
	Delivery currentLoc = start;
	
	double dist=0;
	
		
		for(Delivery d : deliveries){
			Journey j= OSMHelper.getJourney(currentLoc.getLocation(), d.getLocation(),mode);
			dist += j.getDistanceKM();
			currentLoc =d;
		}
		Journey j= OSMHelper.getJourney(currentLoc.getLocation(),start.getLocation(),mode);
		dist += j.getDistanceKM();
		
	
	return dist;
	}
	
	public int getRndInsertPointIndx() {
		return rnd.nextInt(deliveries.size());
	}

	public int getRndRVVehicleIndx() {
		return rnd.nextInt(rvVehicles.size());
	}
	
}
