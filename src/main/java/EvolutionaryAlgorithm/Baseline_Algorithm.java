package EvolutionaryAlgorithm;

import GeoUtils.Location;
import deliveryRoutes.Main;
import deliveryRoutes.Results;
import deliveryRoutes.Vehicle;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.zip.ZipOutputStream;


public class Baseline_Algorithm {
	
	private static HashMap<String,MAP_Entry> map = new HashMap<String,MAP_Entry>();
	private static Random rnd = new Random();
	private static double highestEmissions;
	private static double lowestEmissions;
	
	private static double highestFixedCost;
	private static double lowestFixedCost;
	
	private static double highestRunCost;
	private static double lowestRunCost;
	
	private static double highestTime;
	private static double lowestTime;
	
	private static double highestVehicles;
	private static double lowestVehicles;
	
	private static double highestDist;
	private static double lowestDist;
	
	private static int qty=0;
	private static int improvements=0;
  
	private static int INIT = 2;
	private static int MAX_EVALS;
   private static int timeOut;
   private static String outName = "";
   private static boolean twoOpt = true;
   private static int evals=0;
	
	
   private static BufferedWriter bw = null;//for log file
   private static FileWriter fw = null;

	public static void main(String[] args){

		String a = Main.FILENAME;

		File scenarioFile = new File(a);


		INIT = 100;
		MAX_EVALS = 1500;

		
		outName = "OutputDir" + rnd.nextInt(Integer.MAX_VALUE);
		
		if (true)
			twoOpt = true;
		else
			twoOpt = false;
		
		
		Scenario myScenario = Scenario.getInstance();
		myScenario.setTwoOpt(twoOpt);
		myScenario.setup(scenarioFile);
		
	    setHighLows();
	
	    int count=0;
	    int chSize=1;
	    while(count <INIT){
	    		System.out.println("Init: " +count +" : " + improvements +":" + map.size());
	    		count++;
	    		evals++;
	    		chSize = rnd.nextInt(Scenario.getInstance().getMAX_VEHICLES());
	    		Chromosome ch = new Chromosome(chSize);
	    		Results res= myScenario.evaluate(ch,true,"");
	    		String key = getKey(res);
	    		addToMap(key,new MAP_Entry(res,ch),"NEW");
	    }
	    System.out.println("Solutions -" +map.size());

	    int lastCh = timeOut;
	    //while(lastCh > 0){
	    while(evals < MAX_EVALS) {
	    		lastCh --;

		    	System.out.println("Evals " +evals +" : " + improvements +":" + map.size());
		    	count++;
		    	String parentage = "";
		    	MAP_Entry p1 = randomEntry();
		    	MAP_Entry p2 = randomEntry();
		    	Chromosome ch =null;
		    	if (rnd.nextBoolean()) {
		    		ch = new Chromosome(p1.myChromo,p2.myChromo);//Recombination
		    		parentage = p1.key+"/" +p2.key;
		    	}
		    	else {
		    		ch = (Chromosome) p1.myChromo.clone();
		    		parentage = p1.key;
		    	}
	    	
		    	ch.mutate();
		    	Results res= myScenario.evaluate(ch,true,"");
		    	evals++;
		    	String key = getKey(res);
		    	if (addToMap(key,new MAP_Entry(res,ch),parentage))
		    		{
		    			lastCh = timeOut;
		    		}

	    		}

	    writeRes2();

	}

	public static MAP_Entry randomEntry() {
	    ArrayList<String>temp = new ArrayList<String>(map.keySet());
	    String k = temp.get(rnd.nextInt(temp.size()));
	    
	    MAP_Entry res =  map.get(k);
	    res.key = k;
	    return res;
	}
	
	private static boolean addToMap(String key, MAP_Entry mr, String parentage){
		if (map.containsKey(key)){
			MAP_Entry current = map.get(key);
			if (mr.myResults.getTotalCost()<current.myResults.getTotalCost()){
				//System.out.println("Update" + key);
				improvements++;
				//timeOutCount=timeOutMAX;
				map.remove(key);
				mr.improves = current.improves+1;
				map.put(key, mr);
				return true;
			}else{
				return false;
			}
		}else{
			mr.improves=0;
			map.put(key, mr);
			return true;
		}
	}
	private static void setHighLows(){
		Chromosome ch = new Chromosome(0);
		Results baseLine = Scenario.getInstance().evaluate(ch,true,"");
		
		double dist = baseLine.getDistance();
		highestEmissions = dist * Scenario.getInstance().getHighestPolluter();
		lowestEmissions =0;//aim for 0!!
		
		highestFixedCost = Scenario.getInstance().getMostExpensiveFixed()*(Scenario.getInstance().getMAX_VEHICLES()/2);
		lowestFixedCost = baseLine.getFixedCost();//Lowest fixed cost is basic vehicle on its own.
		
		highestRunCost = (dist * Scenario.getInstance().getMostExpensiveRun())*2;
		lowestRunCost = 0;// baseLine.getRunningCost()/2;//Half the running cost
		
		highestTime = (dist/ Scenario.getInstance().getSlowest())*60;
		lowestTime =baseLine.getMaxTime()/2;//(dist/ Scenario.getInstance().getQuickest())*60;
		
		highestVehicles = Scenario.getInstance().getMAX_VEHICLES();
		lowestVehicles = 1;
		
		highestDist = dist*4;
		lowestDist= dist;

		 
		
	}
	private static String getKey(Results res){
		int buckets=20;
		String key = "";
		//Emissions
		double em = (highestEmissions - lowestEmissions)/buckets;
		long emNorm = Math.round((res.getEmissions()-lowestEmissions)/em);
		if (emNorm > buckets) 
			emNorm = buckets;
		if (emNorm < 1)
			emNorm =1;
		
		//Fixed cost
		double cst = (highestFixedCost - lowestFixedCost)/buckets;
		long fxdCstNorm = Math.round((res.getFixedCost()-lowestFixedCost)/cst);
		if (fxdCstNorm>buckets) 
			fxdCstNorm = buckets;
		if(fxdCstNorm<1)
			fxdCstNorm=1;
		
		//Run cost
		cst = (highestRunCost - lowestRunCost)/buckets;
		long runCstNorm =Math.round((res.getRunningCost()-lowestRunCost)/cst);
		if(runCstNorm>buckets) 
			runCstNorm = buckets;
		if(runCstNorm <1)
			runCstNorm=1;
		
		//Time
		double tme = (highestTime - lowestTime)/buckets;
		long timeNorm = Math.round((res.getMaxTime()-lowestTime)/tme);
		if(timeNorm>buckets) 
			timeNorm = buckets;
		if(timeNorm<1)
			timeNorm=1;
		//Vehicles
		double veh = (highestVehicles - lowestVehicles)/buckets;
		long vehNorm = Math.round((res.getVehicles()-lowestVehicles)/veh);
		if(vehNorm>buckets)
			vehNorm = buckets;
		if(vehNorm<1)
			vehNorm=1;
		
		//dist
				double dist = (highestDist - lowestDist)/buckets;
				long distNorm = Math.round((res.getDistance()-lowestDist)/veh);
				if(distNorm>buckets)
					distNorm = buckets;
				if(distNorm<1)
					distNorm=1;
				
		//key = emNorm +":" + fxdCstNorm +":"+runCstNorm+":" +timeNorm + ":" +vehNorm;
		key = emNorm +":" + timeNorm + ":" +vehNorm +":"+distNorm;
		
		return key;
	}

	
	private static void writeRes(){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("original.ser");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(map);
		} catch (IOException e) {
			e.printStackTrace();
		}


//
		File file = new File("./"+outName+".csv");

		try{
			DecimalFormat df = new DecimalFormat("#.00");
			PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
			//key = emNorm +":" + timeNorm + ":" +vehNorm +":"+distNorm;
			pw.write("Dimensions,4\nNormalised,20\nkey,dist,emissions,time,vehicles,dist,Actualemissions,ActualTime,ActualVehicles,ActualDist,");
			for (Location l : Scenario.getInstance().getRVLocations()) {
				pw.write( l.getDescription() +",");
			}
			//pw.write(",,");
			for (Vehicle v : Scenario.getInstance().getVehicles()) {
				pw.write( v.getDescription() +",");
			}
			pw.write("\n");
			for (String key : map.keySet()){
				MAP_Entry me = map.get(key);
				String buffer = key + ","+ df.format(me.myResults.getTotalCost()) +","+key.replace(':', ',') ;
				buffer = buffer + ","+ df.format(me.myResults.getEmissions())+"," +df.format(me.myResults.getMaxTime())+","+df.format(me.myResults.getVehicles())+","+df.format(me.myResults.getDistance());
				//buffer = buffer + ","+ df.format(me.myResults.getEmissions())+"," +df.format(me.myResults.getMaxTime())+","+df.format(me.myResults.getVehicles()+","+df.format(me.myResults.getDistance()));

				//Extra cols for stats
				//Count MicroDepot use

				HashMap<Location,Integer> depotCount = new HashMap<Location,Integer>();

				for (Gene g : me.myChromo) {
					Location currentDepot = Scenario.getInstance().getRVLocation(g.location);
					if (!depotCount.containsKey(currentDepot))
						depotCount.put(currentDepot,new Integer(1));
					else {
						int c = depotCount.get(currentDepot);
						c++;
						depotCount.put(currentDepot,new Integer(c));
					}

				}
				//Print depots in order
				System.out.println(Scenario.getInstance().getRVLocations().size());
				buffer = buffer + ",";
				for (Location l : Scenario.getInstance().getRVLocations()) {
					//buffer = buffer + "," + l.getDescription() +",";
					if(depotCount.containsKey(l)) {
						buffer = buffer + depotCount.get(l);
					}
					else {
						buffer = buffer + "0";
					}
					buffer = buffer + ",";
				}

				//Count vehicles
				HashMap<Vehicle,Integer> vehCount = new HashMap<Vehicle,Integer>();

				for (Gene g : me.myChromo) {
					if (!vehCount.containsKey(g.rvVehicle))
						vehCount.put(g.rvVehicle,new Integer(1));
					else {
						int c = vehCount.get(g.rvVehicle);
						c++;
						vehCount.put(g.rvVehicle,new Integer(c));
					}

				}
				//Print vehicles in order
				for (Vehicle v : Scenario.getInstance().getVehicles()) {
					//buffer = buffer + "," + v.getDescription() +",";
					if(vehCount.containsKey(v)) {
						buffer = buffer + vehCount.get(v);
					}
					else {
						buffer = buffer + "0";
					}
					buffer = buffer + ",";
				}
				//Done extra cols
				buffer = buffer + "\n";
				pw.write(buffer);

			}

			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void writeRes2(){

		String fs = Main.FILENAME.split(".")[0];
//
		File file = new File("OriginalAlgo_" + fs + "_" +java.time.LocalDateTime.now()+".csv");

		try{
			DecimalFormat df = new DecimalFormat("#.00");
			PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));

			for (String key : map.keySet()){
				MAP_Entry me = map.get(key);
				String buffer = key + ","+ df.format(me.myResults.getTotalCost());
				buffer = buffer + "\n";
				pw.write(buffer);

			}
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
