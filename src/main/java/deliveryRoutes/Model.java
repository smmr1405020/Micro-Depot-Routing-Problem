package deliveryRoutes;


import EvolutionaryAlgorithm.MarkDownHelper;
import EvolutionaryAlgorithm.Scenario;
import GeoUtils.*;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Model {
	
	private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	private double totalEmissions;
	private double totalFixedCost;
	private double totalRunningCost;
	private double totalTime;
	private double totalDist;
	private int totalVehicles;
	private boolean silent;
	
	//Add the vehicle to an array of vehicles
	public void addVehicle(Vehicle myVan)
	{
		vehicles.add(myVan);
	}
	
	public int getTotalVehicles(){
		return totalVehicles;
	}
	
	public double getTotalEmissions() {
		return totalEmissions;
	}



	public double getTotalFixedCost() {
		return totalFixedCost;
	}



	public double getTotalDist() {
		return totalDist;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public double getTotalRunningCost() {
		return totalRunningCost;
	}

	public Results run(String name){
		return run(false,name);
	}

	//Create a KML file and a CSV file from the points provided
	public Results run(boolean silent, String name)
	//if silent = true, don't print files
	{
		this.silent = silent;
		//KMLWriter kml = new KMLWriter();

		//Create a route for the deliveries each vehicle holds in the system
		MarkDownHelper md = new MarkDownHelper();
		
		
		//Track end time of vehicles to find the total time taken
		double endTime=0;
		for (Vehicle myVan : vehicles)
		{
			double t = processVehicle(myVan,0,md);
			if (endTime < t)
				endTime = t;
		}
		

		//Add in details for RVs
		for (Vehicle myVan : vehicles)
		{
			for (Delivery d : myVan.getDeliveries()) {
				if (d instanceof Rendezvous) {
					myVan = ((Rendezvous) d).getVehicle();
					double t= processVehicle( myVan, ((Rendezvous) d).getTime(),md); //bujinai
					if (endTime < t)
						endTime = t;
				}
			}
		}
		
		PrintWriter pw = null;
		if(!silent){
			
			try{
				pw = new PrintWriter(Scenario.getInstance().getWorkingDir()+"/result.md");
				md.setName(name.replace('_','-'));
				pw.write(md.getMarkDown());
				pw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		totalTime = endTime;
		Results res = new Results();
		res.setDistance(this.totalDist);
		res.setEmissions(this.totalEmissions);
		res.setFixedCost(this.totalFixedCost);
		res.setMaxTime(totalTime);
		res.setRunningCost(this.totalRunningCost);
		res.setVehicles(this.getTotalVehicles());

		//System.out.println(res.toString());
		return res;
		
	}

	private double processVehicle(Vehicle myVan, double startTime, MarkDownHelper html) {
		/*
		 * Process all ofthe deliveries associated with this vehicle
		 * Return the time of the last delivery
		 * 
		 */
		KMLWriter kml = new KMLWriter();
		
		double time = startTime;
		double dist = 0;
		double emissions = 0;
		double runningCost=0;
		//Set the starting points for the vehicle
		Location previous = myVan.getStart();
		Location last = myVan.getStart();
		//An arraylist that holds the deliveries of each vehicle
		ArrayList<Delivery> delivery = myVan.getDeliveries();
		kml.addPlacemark(previous.getLat(), previous.getLon(), "Start", "Main depot.","rv");
		
		//Get the destinations for each delivery held by the vehicle
		/*int idx=0;
		for (Delivery d : delivery)
		{
			Location currentlocation = d.getLocation();
			String type = "del";
			if (d instanceof Rendezvous)
				type = "rv";
			kml.addPlacemark(currentlocation.getLat(), currentlocation.getLon() ,totalVehicles+"."+idx, "("+currentlocation.getLat()+":"+currentlocation.getLon()+")",type);
			
			idx++;
		}*/
		//Add return to depot
		//Create a new journey factory for the van
		JourneyFactory createpath = new SimpleRouter(myVan.getSpeed());
		
		//Create an arraylist of each location sorted by which destination is closest to the previous
		ArrayList<Delivery> inOrder = optimiseRoute(previous, delivery, createpath, myVan.getType());
		myVan.setDeliveries(inOrder);
		
		//Get the destinations for each delivery held by the vehicle
				int idx=0;
				for (Delivery d : inOrder)
				{
					Location currentlocation = d.getLocation();
					String type = "del";
					if (d instanceof Rendezvous)
						type = "rv";
					kml.addPlacemark(currentlocation.getLat(), currentlocation.getLon() ,totalVehicles+"."+idx, "("+currentlocation.getLat()+":"+currentlocation.getLon()+")",type);
					
					idx++;
				}
		
		//Create a journey that supports multiple points
		MultiLegJourney route = new MultiLegJourney();

			//StringBuilder sb = new StringBuilder();
			totalVehicles++;
			//sb.append("Delivery details for vehicle ID: " + myVan.getId() +","+myVan.getDescription());HTML
			//HTMLhelper.addVehicle(""+myVan.getId(), myVan.getDescription(), "??");
			
			//Append return to depot 
			String dels="";
			inOrder.add(new Delivery(last,"depot"));
			//Use an algorithm to create a route between each destination for the deliveries
			for (Delivery nextDel : inOrder)
			{
				if(nextDel instanceof  Rendezvous && myVan != Scenario.getInstance().primaryVehicle){
					System.out.println("holaha");
				}
				Location nextDest = nextDel.getLocation();
				dels += nextDest.getDescription() + "," + nextDest.getLat() + "," +nextDest.getLon() +"<br>";

				Journey fullTravel = OSMHelper.getJourney(last, nextDest,myVan.getType());
				//Work out the total distance travelled by vehicle and the total emissions of the vehicle
				double currentDist = fullTravel.getDistanceKM();
				dist = dist + currentDist;
				emissions = emissions + (fullTravel.getDistanceKM() * myVan.getEmissions());
				time = time + (currentDist / myVan.getSpeed())*60;//Time mins
				nextDel.setTime(time);
				//Add the current route to the journey
				route.addLeg(fullTravel);
				//Append the points travelled to the CSV file
				//The last location travelled to becomes the current one
				last = nextDest;
				
			}
			
			//Work out the total amount of time taken to deliver the deliveries and add details of the delivery to the csv file
			totalDist += dist;
			runningCost += (myVan.getCostKM()*dist);
			totalEmissions += emissions;
			totalFixedCost += myVan.getFixedCost();
			totalRunningCost += runningCost;


			DecimalFormat df = new DecimalFormat("#.00");
		html.addVehicle(""+myVan.getId(), myVan.getDescription(), dels, df.format(time),df.format(emissions), df.format(dist));
		//Add the route to the kml file
		//kml.addJourney(route);
		ArrayList<Double> lat  = new ArrayList<Double>();
		ArrayList<Double> lon  = new ArrayList<Double>();
		for (Journey r : route.getLegs()) {
			ArrayList<Location> locs = (ArrayList) r.getAttribute(Journey.PATH);
				for (Location l : locs) {
					lat.add(l.getLat());
					lon.add(l.getLon());
				}
				//Add walks 
				if (r.getAttribute("walkA")!= null) {
					//System.out.println("Additional walk");
					ArrayList<Location> walklocs = (ArrayList) r.getAttribute("walkA");
					ArrayList<Double> wlat = new ArrayList<Double>();
					ArrayList<Double> wlon = new ArrayList<Double>();
					
					for (Location l : walklocs) {
						wlat.add(l.getLat());
						wlon.add(l.getLon());
					}
					kml.addRoute(wlat,wlon, myVan.getId() + " (walk) " + myVan.getType(), myVan.getDescription(), "green");
				}

				String colour = "";


				if (myVan.getDescription().equals("ConventionalVan"))
					colour = "red";
				if (myVan.getDescription().equals("bike"))
					colour = "blue";
				if (myVan.getDescription().equals("walk"))
					colour = "green";
				if (myVan.getDescription().equals("electric van"))
					colour = "yellow";

				kml.addRoute(lat,lon, myVan.getId() + " " + myVan.getType(), myVan.getDescription(), colour);



				if (r.getAttribute("walkB")!= null) {
					//System.out.println("Additional walk");
					//System.out.println("Additional walk");
					ArrayList<Location> walklocs = (ArrayList) r.getAttribute("walkB");
					ArrayList<Double> wlat = new ArrayList<Double>();
					ArrayList<Double> wlon = new ArrayList<Double>();
					
					for (Location l : walklocs) {
						wlat.add(l.getLat());
						wlon.add(l.getLon());
					}
					kml.addRoute(wlat,wlon, myVan.getId() + " (walk) " + myVan.getType(), myVan.getDescription(), "green");
				}

		}
		
		

		//Add walks
		
		//Try catch for saving the kml file
		try
		{
			if (!silent){
				String kmlName = Scenario.getInstance().getWorkingDir()+"/Vehicle " + myVan.getId();
				kml.writeFile( kmlName);
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error saving KML");
		}
		return time;
	}

	
	private static ArrayList<Delivery> optimiseRoute(Location previous, ArrayList<Delivery> destinations, JourneyFactory createpath, String type)
	{
		
		//Sort into nearest neighbour order from currentLocation
		ArrayList<Delivery> ordered  = new ArrayList<Delivery>();
		//Dummy delivery start
		ordered = Scenario.getInstance().NNSort(previous, destinations,type);
		ordered = Scenario.getInstance().twoOptSort(previous, ordered,type);
		
		return ordered;
		
	}
	/*
	private static ArrayList<Delivery> optimiseRoute(Location previous, ArrayList<Delivery> destinations, JourneyFactory createpath, String type)
	{
		
		//Sort into nearest neighbour order from currentLocation
		ArrayList<Delivery> ordered  = new ArrayList<Delivery>();
		//Dummy delivery start
		Delivery start = new Delivery(previous.getLat(),previous.getLon(),previous.getAlt(),"start");
		Delivery current = start;
		
		while(destinations.size() > 0)
		{
			double bestD = Double.MAX_VALUE;
			Delivery next = null;
			for (Delivery possible: destinations)
			{
			
				double tempD = OSMHelper.getJourney(current.getLocation(), possible.getLocation(), type).getDistanceKM();
				
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
