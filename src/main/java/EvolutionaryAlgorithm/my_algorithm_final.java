package EvolutionaryAlgorithm;

import GeoUtils.Location;
import deliveryRoutes.Main;
import deliveryRoutes.Results;
import deliveryRoutes.Vehicle;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class my_algorithm_final {

	private static HashMap<String,MAP_Entry> map = new HashMap<String,MAP_Entry>();
	private static Random rnd = new Random();
	private static ArrayList<Results> currentPopulation = new ArrayList<>();
	private static ArrayList<ReferencePoint> referencePoints = new ArrayList<>();
	private static HashMap<String,Integer> taboo_keys = new HashMap<>();
	private static ArrayList<String> taboo_list = new ArrayList<String>();

	private static double highestEmissions;
	private static double lowestEmissions;

	private static int improvements_new = 0;
	private static int improvements_mutation= 0;
	private static int improvements_crossover = 0;

	private static int attempt_new = 0;
	private static int attempt_mutation= 0;
	private static int attempt_crossover = 0;


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
	private static int MAX_EVALUATION;
	private static int timeOut;
	private static String outName = "";
	private static boolean twoOpt = true;
	private static int TOTAL_EVALUATION =0;


	private static BufferedWriter bw = null;//for log file
	private static FileWriter fw = null;


	public static Results TournamentSelection(){
		Random rand = new Random();
		int rand1 = rand.nextInt(currentPopulation.size()-1);
		Results res1 = currentPopulation.get(rand1);
		Results res2;
		for(int i=0 ; i<3 ; i++){
			rand1 = rand.nextInt(currentPopulation.size()-1);
			res2 = currentPopulation.get(rand1);
			if(res2.getTotalCost()<res1.getTotalCost()){
				res1 = res2;
			}
		}
		return res1;
	}

	public static void main(String[] args){

		String a = Main.FILENAME;
		File scenarioFile = new File(a);

		INIT = 10;
		MAX_EVALUATION = 20;
		TOTAL_EVALUATION = 0;
		improvements=0;

		Scenario myScenario = Scenario.getInstance();

		if(Main.setupFlag == false){

			outName = "OutputDir" + rnd.nextInt(Integer.MAX_VALUE);

			if (true)
				twoOpt = true;
			else
				twoOpt = false;


			myScenario = Scenario.getInstance();
			myScenario.setTwoOpt(twoOpt);
			myScenario.setup(scenarioFile);

			setHighLows();
			Main.setupFlag = true;
		}

		int count=0;
		int chSize=1;
		while(count <INIT){
			System.out.println("Init: " +count);
			count++;
			chSize = rnd.nextInt(Scenario.getInstance().getMAX_VEHICLES());
			Chromosome ch = new Chromosome(chSize);
			Results res= myScenario.evaluate(ch,true,"");
			TOTAL_EVALUATION++;
			if(TOTAL_EVALUATION >= MAX_EVALUATION){
				break;
			}
			res.setMyCh(ch);
			String key_res = getKey(res);
			addToMap(key_res,new MAP_Entry(res,res.getMyCh()),"NEW");
			currentPopulation.add(res);
		}

		int N = currentPopulation.size();
		System.out.println("Population Size: "+N);
		double MUTATION_RATE = 0.5;


		while(TOTAL_EVALUATION < MAX_EVALUATION) {
			//int no_change = 0;
			ArrayList<Results> children = new ArrayList<>();
			//int imp_in_prevIteration = improvements+map.size();
			int total_mutation_in_itr=0;
			int mut_sucess=0;
			for(int i=0;i<N;i++){
				Random rand = new Random();
				double ratio_mut_crs = 0.5;
				double p1 = rand.nextDouble();
				double p2 = rand.nextDouble();
				int mem1 = rand.nextInt(currentPopulation.size());
				int mem2 = rand.nextInt(currentPopulation.size());
				while (mem2==mem1){
					mem2=rand.nextInt(currentPopulation.size());
				}
				if(p1<ratio_mut_crs){
					total_mutation_in_itr++;
					Results res1 = currentPopulation.get(mem1);
					Chromosome newCh = (Chromosome) res1.getMyCh().clone();
					newCh.mutate(MUTATION_RATE);
					Results newRes = Scenario.getInstance().evaluate(newCh,true,"");
					TOTAL_EVALUATION++;
					System.out.println("Evals " + TOTAL_EVALUATION +" : " + improvements +":" + map.size());
					if(TOTAL_EVALUATION>=MAX_EVALUATION){
						break;
					}
					newRes.setMyCh(newCh);
					String key_newres = getKey(newRes);
					boolean imp = addToMap(key_newres, new MAP_Entry(newRes,newRes.getMyCh()),"MUTATION");
					if(imp == true){
						System.out.println("Improvement by mutation: "+getKey(res1)+" | "+MUTATION_RATE+" | "+getKey(newRes));
						mut_sucess++;
					}
					else{
						if(taboo_keys.containsKey(getKey(res1))){
							int val = taboo_keys.get(getKey(res1));
							taboo_keys.put(getKey(res1),val+1);
						}
						else{
							taboo_keys.put(getKey(res1),1);
						}
					}
					children.add(newRes);
				}
				else{
					Results res1 = currentPopulation.get(mem1);
					Results res2 = currentPopulation.get(mem2);


					Chromosome newCh = new Chromosome(res1.getMyCh(),res2.getMyCh());
					if(p2>0)
						newCh.mutate(0.3);
					Results newRes = Scenario.getInstance().evaluate(newCh,true,"");

					TOTAL_EVALUATION++;
					System.out.println("Evals " + TOTAL_EVALUATION +" : " + improvements +":" + map.size());
					if(TOTAL_EVALUATION>=MAX_EVALUATION){
						break;
					}
					newRes.setMyCh(newCh);
					String key_newres = getKey(newRes);
					boolean imp = addToMap(key_newres, new MAP_Entry(newRes,newRes.getMyCh()),"CROSSOVER");
					if(imp == true){
						System.out.println("Improvement by crossover: "+getKey(res1)+" | "+getKey(res2)+" | "+getKey(newRes));
					}
					else{
						if(taboo_keys.containsKey(getKey(res1))){
							int val = taboo_keys.get(getKey(res1));
							taboo_keys.put(getKey(res1),val+1);
						}
						else{
							taboo_keys.put(getKey(res1),1);
						}
						if(taboo_keys.containsKey(getKey(res2))){
							int val = taboo_keys.get(getKey(res2));
							taboo_keys.put(getKey(res1),val+1);
						}
						else{
							taboo_keys.put(getKey(res2),1);
						}
					}
					children.add(newRes);
				}
			}

			if((double)mut_sucess/(double) total_mutation_in_itr > 0.4 && MUTATION_RATE>0.25){
				MUTATION_RATE -= 0.1;
			}
			else if((double)mut_sucess/(double) total_mutation_in_itr < 0.3 && MUTATION_RATE<0.75){
				MUTATION_RATE += 0.1;
			}


			ArrayList<Results> elites = new ArrayList<>();
			for(String key : map.keySet()){
				Results res = map.get(key).myResults;
				elites.add(res);
			}

			Collections.sort(elites,new CostComparator());


			HashMap<String,String> interim_map = new HashMap<>();
			currentPopulation.clear();

			for(String str : taboo_keys.keySet()){
				int val = taboo_keys.get(str);
				if(val>=2){
					taboo_list.add(str);
				}
			}

			if(taboo_list.size() > N){
				taboo_list.remove(0);
			}

			for(String str : taboo_list){
				if(taboo_keys.containsKey(str)){
					taboo_keys.remove(str);
					break;
				}
			}

			for(int i=0;i<elites.size();i++){
				if(currentPopulation.size()>=N){
					break;
				}
				String ky = getKey(elites.get(i));
				int isTaboo=0;
				for(int j=0;j<taboo_list.size();j++){
					if(taboo_list.get(j).equals(ky)){
						isTaboo=1;
						break;
					}
				}
				if(isTaboo==0){
					currentPopulation.add(elites.get(i));
					interim_map.put(ky," ");
				}
			}


		}

		writeRes2();
		map.clear();
		currentPopulation.clear();
		taboo_keys.clear();
		taboo_list.clear();
	}

	private static boolean addToMap(String key, MAP_Entry mr, String parentage){
		if(parentage.equals("NEW")){
			attempt_new++;
		}
		else if(parentage.equals("MUTATION")){
			attempt_mutation++;
		}
		else if(parentage.equals("CROSSOVER")){
			attempt_crossover++;
		}

		if (map.containsKey(key)){
			MAP_Entry current = map.get(key);
			if (mr.myResults.getTotalCost()<current.myResults.getTotalCost()){
				//System.out.println("Update" + key);
				improvements++;
				if(parentage.equals("NEW")){
					improvements_new++;
				}
				else if(parentage.equals("MUTATION")){
					improvements_mutation++;
				}
				else if(parentage.equals("CROSSOVER")){
					improvements_crossover++;
				}
				//timeOutCount=timeOutMAX;
				map.remove(key);
				if(taboo_keys.containsKey(key)){
					taboo_keys.remove(key);
				}
				for(int i=0;i<taboo_list.size();i++){
					if(taboo_list.get(i).equals(key)){
						taboo_list.remove(i);
						break;
					}
				}
				mr.improves = current.improves+1;
				map.put(key, mr);
				return true;
			}else{
				return false;
			}
		}else{
			mr.improves=0;
			map.put(key, mr);

			if(parentage.equals("NEW")){
				improvements_new++;
			}
			else if(parentage.equals("MUTATION")){
				improvements_mutation++;
			}
			else if(parentage.equals("CROSSOVER")){
				improvements_crossover++;
			}

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
				//System.out.println(Scenario.getInstance().getRVLocations().size());
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

		String fs = Main.FILENAME.split("Dataset")[1];
		fs = fs.substring(1);
		fs = fs.split(".csv")[0];
		System.out.println(fs);

		File file = new File("MyAlgo"  + "_"+ fs + "_" +System.currentTimeMillis()+".csv");

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
