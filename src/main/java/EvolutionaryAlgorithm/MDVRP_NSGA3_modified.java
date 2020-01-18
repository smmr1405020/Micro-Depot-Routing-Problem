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


public class MDVRP_NSGA3_modified {
	
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



	public static boolean doesParetoDominate(Results a, Results b){
		boolean ans = false;
		int A=0;
		int B=0;

		if(a.getTotalCost() < b.getTotalCost()){
			A++;
		}
		else{
			B++;
		}

		if(a.getEmissions() < b.getEmissions()){
			A++;
		}
		else{
			B++;
		}
		if(a.getVehicles() < b.getVehicles()){
			A++;
		}
		else{
			B++;
		}
		if(a.getDistance() < b.getDistance()){
			A++;
		}
		else{
			B++;
		}
		if(a.getMaxTime() < b.getMaxTime()){
			A++;
		}
		else{
			B++;
		}

		//System.out.println(A+"-"+B);
		if(A>4){
			return true;
		}
		return false;
	}

	public static ArrayList<Results> paretoFront(ArrayList<Results> population){
		ArrayList<Results> front = new ArrayList<>();

		for(Results res : population){
			boolean isParetoDominated = false;
			for(int i=0; i<front.size();i++){
				if(doesParetoDominate(front.get(i),res)){
					isParetoDominated = true;
					break;
				}
				else if(doesParetoDominate(res,front.get(i))){
					front.remove(front.get(i));
				}
			}
			if(!isParetoDominated){
				front.add(res);
			}
		}

		return front;
	}

	public static ArrayList<Results> assignRank(ArrayList<Results> population){
		for( Results individual : population){
			individual.setParetoRank(-1);
		}
		ArrayList<Results> pp = (ArrayList<Results>) population.clone();
		ArrayList<Results> newPopulation = new ArrayList<>();
		int rank = 1;
		while (pp.size()!=0){
			ArrayList<Results> currentFront = paretoFront(pp);
			currentFront = assignSparsity(currentFront);
			for( Results individual : currentFront){
				individual.setParetoRank(rank);
				newPopulation.add(individual);
				pp.remove(individual);
			}
			rank++;
		}
		return newPopulation;
	}

	public static ArrayList<Results> assignSparsity(ArrayList<Results> population){
		for( Results individual : population){
			individual.setSparsity(0);
		}
		Collections.sort(population,new CostComparator());
		double costRange = (highestFixedCost+highestRunCost) - (lowestFixedCost+lowestRunCost);
		for(int i=0;i<population.size();i++){
			if(i==0 || i==(population.size()-1)){
				population.get(i).setSparsity(Double.MAX_VALUE);
			}
			else{
				double add = (population.get(i+1).getTotalCost() - population.get(i-1).getTotalCost())/costRange;
				population.get(i).setSparsity(population.get(i).getSparsity() + add);
			}
		}

		Collections.sort(population,new EmissionComparator());
		double emissionRange = highestEmissions - lowestEmissions;
		for(int i=0;i<population.size();i++){
			if(i==0 || i==(population.size()-1)){
				population.get(i).setSparsity(Double.MAX_VALUE);
			}
			else{
				double add = (population.get(i+1).getEmissions() - population.get(i-1).getEmissions())/emissionRange;
				population.get(i).setSparsity(population.get(i).getSparsity() + add);
			}
		}

		Collections.sort(population,new VehicleComparator());
		double vehicleRange = highestVehicles - lowestVehicles;
		for(int i=0;i<population.size();i++){
			if(i==0 || i==(population.size()-1)){
				population.get(i).setSparsity(Double.MAX_VALUE);
			}
			else{
				double add = (population.get(i+1).getVehicles() - population.get(i-1).getVehicles())/vehicleRange;
				population.get(i).setSparsity(population.get(i).getSparsity() + add);
			}
		}

		Collections.sort(population,new DistanceComparator());
		double distanceRange = highestDist - lowestDist;
		for(int i=0;i<population.size();i++){
			if(i==0 || i==(population.size()-1)){
				population.get(i).setSparsity(Double.MAX_VALUE);
			}
			else{
				double add = (population.get(i+1).getDistance() - population.get(i-1).getDistance())/distanceRange;
				population.get(i).setSparsity(population.get(i).getSparsity() + add);
			}
		}

		Collections.sort(population,new TimeComparator());
		double timeRange = highestTime - lowestTime;
		for(int i=0;i<population.size();i++){
			if(i==0 || i==(population.size()-1)){
				population.get(i).setSparsity(Double.MAX_VALUE);
			}
			else{
				double add = (population.get(i+1).getMaxTime() - population.get(i-1).getMaxTime())/timeRange;
				population.get(i).setSparsity(population.get(i).getSparsity() + add);
			}
		}

		return population;
	}

	public static Results TournamentSelectionNSGA(){
		Random rand = new Random();
		int rand1 = rand.nextInt(currentPopulation.size()-1);
		Results res1 = currentPopulation.get(rand1);
		Results res2;
		// Tournament size 5
		for(int i=0 ; i<2 ; i++){
			rand1 = rand.nextInt(currentPopulation.size()-1);
			res2 = currentPopulation.get(rand1);
			if(res2.getParetoRank()<res1.getParetoRank()){
				res1 = res2;
			}
			else if(res2.getParetoRank()==res1.getParetoRank() && res2.getSparsity()>res1.getSparsity()){
				res1 = res2;
			}
		}
		return res1;
	}

	public static ArrayList<Results> normalizeResults(ArrayList<Results> population){

		double min_cost=Double.MAX_VALUE;
		double max_cost=0.001;
		double min_emission=Double.MAX_VALUE;
		double max_emission=0.001;
		double min_time=Double.MAX_VALUE;
		double max_time=0.001;
		double min_vehicles=Double.MAX_VALUE;
		double max_vehicles=0.001;
		double min_distance=Double.MAX_VALUE;
		double max_distance=0.001;


		for(int i=0;i<population.size();i++){

			population.get(i).clearReference();
			if(population.get(i).getTotalCost()<min_cost){
				min_cost=population.get(i).getTotalCost();
			}
			if(population.get(i).getTotalCost()>max_cost){
				max_cost=population.get(i).getTotalCost();
			}

			if(population.get(i).getEmissions()<min_emission){
				min_emission=population.get(i).getEmissions();
			}
			if(population.get(i).getEmissions()>max_emission){
				max_emission=population.get(i).getEmissions();
			}

			if(population.get(i).getMaxTime()<min_time){
				min_time=population.get(i).getMaxTime();
			}
			if(population.get(i).getMaxTime()>max_time){
				max_time=population.get(i).getMaxTime();
			}

			if(population.get(i).getVehicles()<min_vehicles){
				min_vehicles=population.get(i).getVehicles();
			}
			if(population.get(i).getVehicles()>max_vehicles){
				max_vehicles=population.get(i).getVehicles();
			}

			if(population.get(i).getEmissions()<min_distance){
				min_distance=population.get(i).getDistance();
			}
			if(population.get(i).getEmissions()>max_distance){
				max_distance=population.get(i).getDistance();
			}
		}
		for (int i=0;i<population.size();i++){
			population.get(i).setMyReferenceCost((population.get(i).getTotalCost() - min_cost)/max_cost);
			population.get(i).setMyReferenceEmission((population.get(i).getEmissions() - min_emission)/max_emission);
			population.get(i).setMyReferenceTime((population.get(i).getMaxTime() - min_time)/max_time);
			population.get(i).setMyReferenceVehicle((population.get(i).getVehicles() - min_vehicles)/max_vehicles);
			population.get(i).setMyReferenceDistance((population.get(i).getDistance() - min_distance)/max_distance);
		}


		return population;
	}

	public static ArrayList<Results> associateResults(ArrayList<Results> population){

		for(int i = 0; i< referencePoints.size();i++){
			referencePoints.get(i).clearAll();
			for(int j=0; j< population.size();j++){
				population.set(j,referencePoints.get(i).distanceFromSolution_fivedim(population.get(j)));
			}
		}
		for(int i=0; i<population.size();i++){
			for(int j=0;j<referencePoints.size();j++){
				if(ReferencePoint.isEqual(population.get(i).getMyRef(),referencePoints.get(j))){
					referencePoints.get(j).setNicheCount(referencePoints.get(j).getNicheCount()+1);
				}
			}
		}

   		/*
   		for(Results res: population){
			System.out.println(res.getMyReferenceCost()+" "+res.getMyRerenceEmission());
		}
		System.out.println("\n\n");
		*/

		/*int totniche = 0;
		for(ReferencePoint rf : referencePoints){
			totniche += rf.nicheCount;
		}
		System.out.println("total nichecount: "+totniche+"\n\n");*/

		return population;
	}

	public static ArrayList<Results> niching(int K, ArrayList<Results> F, ArrayList<Results> P){
		int k=0;
		while(k<K){
			//System.out.println("k: "+k);
			ArrayList<Integer> indices = new ArrayList<>();
			int minCount = Integer.MAX_VALUE;
			for(int i=0; i<referencePoints.size();i++){
				if(referencePoints.get(i).nicheCount != 0 &&
						referencePoints.get(i).nicheCountNew<referencePoints.get(i).nicheCount){
					if(referencePoints.get(i).nicheCountNew < minCount){
						indices.clear();
					}
					minCount = referencePoints.get(i).nicheCountNew;
					indices.add(i);
				}
			}

			Random rand = new Random();
			int jbar = rand.nextInt(indices.size());
			jbar = indices.get(jbar);

			if(true){
				referencePoints.get(jbar).setNearestSolution(null);
				referencePoints.get(jbar).setNearestSolutionDistance(Double.MAX_VALUE);
				for(int i=0;i<F.size();i++){
					if(ReferencePoint.isEqual(F.get(i).getMyRef(),referencePoints.get(jbar))
							&& F.get(i).getDistanceFromRef() < referencePoints.get(jbar).nearestSolutionDistance){
						referencePoints.get(jbar).setNearestSolution(F.get(i));
						referencePoints.get(jbar).setNearestSolutionDistance(F.get(i).getDistanceFromRef());
					}
				}
				//System.out.println(referencePoints.get(jbar));
				if(referencePoints.get(jbar).getNearestSolution() != null){
					F.remove(referencePoints.get(jbar).getNearestSolution());
					P.add(referencePoints.get(jbar).getNearestSolution());
					k = k+1;
					//System.out.println("K: "+k);
					referencePoints.get(jbar).setNicheCountNew(referencePoints.get(jbar).getNicheCountNew() + 1);
				}
			}
		}

		return P;
	}


	public static void main(String[] args){

		String a = Main.FILENAME;

		File scenarioFile = new File(a);


		INIT = 100;
		MAX_EVALUATION = 1500;

		
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
			res.setMyKey(key_res);
			addToMap(key_res,new MAP_Entry(res,res.getMyCh()),"NEW");
			currentPopulation.add(res);
	    }


		double P = 0.25;
		for (double i = 0; i<=1; i = i+P){
			double cst = i;
			for(double j = 0; j<=1; j = j+P){
				double ems;
				if(i+j<=1)
					ems = j;
				else
					break;
				for (double k = 0; k<=1; k=k+P){
					double tm;
					if(i+j+k<=1)
						tm = k;
					else
						break;
					for (double l = 0; l <=1; l=l+P){
						double vh;
						if(i+j+k+l<=1)
							vh = l;
						else
							break;
						for(double m=0; m<=1; m=m+P){
							double ds;
							if(i+j+k+l+m<=1)
								ds = m;
							else
								break;
							referencePoints.add(new ReferencePoint(cst,ems,tm,vh,ds));
						}
					}
				}
			}
		}


		int N = currentPopulation.size();
		System.out.println("Population Size: "+N);
		double MUTATION_RATE = 0.5;

	    while(TOTAL_EVALUATION < MAX_EVALUATION) {
			int no_change = 0;
			ArrayList<Results> children = new ArrayList<>();
			int imp_in_currentIteration = improvements;
			int total_mutation_in_itr=0;
			int mut_sucess=0;
			for(int i=0;i<N;i++){
				Random rand = new Random();
				double mut = (double) improvements_mutation / (double) attempt_mutation;
				double crs = (double) improvements_crossover / (double) attempt_crossover;
				double ratio_mut_crs = 0.5;
				double p1 = rand.nextDouble();
				if(p1<ratio_mut_crs){
					total_mutation_in_itr++;
					Results res1 = TournamentSelectionNSGA();
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
						System.out.println("Improvement: "+getKey(res1)+" | "+MUTATION_RATE+" | "+getKey(newRes));
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
					Results res1 = TournamentSelectionNSGA();
					Results res2 = TournamentSelectionNSGA();
					Chromosome newCh = new Chromosome(res1.getMyCh(),res2.getMyCh());
					newCh.mutate(0.3);
					Results newRes = Scenario.getInstance().evaluate(newCh,true,"");
					TOTAL_EVALUATION++;
					System.out.println("Evals " + TOTAL_EVALUATION +" : " + improvements +":" + map.size());
					if(TOTAL_EVALUATION>=MAX_EVALUATION){
						break;
					}
					newRes.setMyCh(newCh);
					String key_newres = getKey(newRes);
					newRes.setMyKey(key_newres);
					boolean imp = addToMap(key_newres, new MAP_Entry(newRes,newRes.getMyCh()),"MUTATION");
					if(imp == true){
						System.out.println("Improvement: "+getKey(res1)+" | "+getKey(res2)+" | "+getKey(newRes));
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

			children.addAll((ArrayList<Results>) currentPopulation.clone());
			assignRank(children);
			currentPopulation.clear();

			for(String str : taboo_keys.keySet()){
				int val = taboo_keys.get(str);
				if(val>=2){
					taboo_list.add(str);
				}
			}

			if(taboo_list.size() > N){
				int del = taboo_list.size()-N;
				for(int i=0;i<del;i++){
					taboo_list.remove(0);
				}
			}

			for(String str : taboo_list){
				if(taboo_keys.containsKey(str)){
					taboo_keys.remove(str);
					break;
				}
			}

			children=normalizeResults(children);
			System.out.println("Normalize done");
			children=associateResults(children);
			System.out.println("Association done");

			//exit(1);
			int itr=1;
			while (currentPopulation.size()<N){
				ArrayList<Results> currentFront = paretoFront(children);
				System.out.println("Selection Iteration "+itr+" CurrentFront size: "+currentFront.size());
				if(currentPopulation.size() + currentFront.size() <= N){
					for(Results transferRes : currentFront){
						transferRes.getMyRef().setNicheCountNew(transferRes.getMyRef().getNicheCountNew() + 1);
						children.remove(transferRes);
						currentPopulation.add(transferRes);
					}
				}
				else{
					System.out.println("Selection Iteration "+itr+" Niching ");
					niching((N-currentPopulation.size()),children,currentPopulation);
				}
				itr++;
			}

			int curr_itr = 0;
			ArrayList<String> population_keys = new ArrayList<>();
			while (curr_itr<currentPopulation.size()){
				String ky = getKey(currentPopulation.get(curr_itr));
				int isTaboo = 0;
				for(int i=0;i<taboo_list.size();i++){
					if(taboo_list.get(i).equals(ky)){
						isTaboo=1;
						break;
					}
				}
				if(isTaboo==0){
					population_keys.add(ky);
					curr_itr++;
					continue;
				}
				else{
					currentPopulation.remove(curr_itr);
				}
			}
			if(currentPopulation.size()<N){
				ArrayList<Results> elites = new ArrayList<>();
				for(String key : map.keySet()){
					Results res = map.get(key).myResults;
					elites.add(res);
				}
				Collections.sort(elites,new CostComparator());

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
					for(int j=0;j<population_keys.size();j++){
						if(population_keys.get(j).equals(ky)){
							isTaboo=1;
							break;
						}
					}
					if(isTaboo==0){
						currentPopulation.add(elites.get(i));
					}
				}
			}
	    }




	    writeRes2();

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
	public static String getKey(Results res){
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

//
		File file = new File("myalgo_NSGA3_modified.csv");

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
