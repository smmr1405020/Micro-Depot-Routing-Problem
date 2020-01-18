package EvolutionaryAlgorithm;

import GeoUtils.Location;
import deliveryRoutes.Main;
import deliveryRoutes.Results;
import deliveryRoutes.Vehicle;

import javax.print.attribute.standard.Finishings;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;


public class MAPElites_change1 {
	
	public static HashMap<String,MAP_Entry> map = new HashMap<String,MAP_Entry>();
	private static ArrayList<Results> currentPopulation = new ArrayList<>();
	private static Results [] emissionResults = new Results[21];
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

	private static int INIT;
   	private static String outName = "";
   	private static boolean twoOpt = true;
   	private static int evals=0;

   	private static int MAX_EVALUATION = 800;
	public static int TOTAL_EVAL = 0;
	
	
   private static BufferedWriter bw = null;//for log file
   private static FileWriter fw = null;


   public static boolean updateResult(Results res){
   		boolean changed = false;
	   double em = (highestEmissions - lowestEmissions)/20;
	   long emNorm = Math.round((res.getEmissions()-lowestEmissions)/em);
	   if (emNorm > 20)
		   emNorm = 20;
	   if (emNorm < 1)
		   emNorm = 1;
	   if(emissionResults[(int) emNorm] == null){
		   emissionResults[(int) emNorm] = res;
		   changed = true;
	   }
	   else {
		   if(emissionResults[(int)emNorm].getTotalCost() > res.getTotalCost()){
			   emissionResults[(int) emNorm] = res;
			   changed = true;
		   }
	   }
	   return changed;
   }


   public static boolean doesParetoDominate(Results a, Results b){
   		boolean ans = false;
   		if(a.getTotalCost() < b.getTotalCost()){
   			ans = true;
		}
   		else{
   			return false;
		}
   		if(a.getEmissions() < b.getEmissions()){
   			ans = true;
		}
   		else{
   			return false;
		}
   		return ans;
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

		return population;
	}


	public static Results TournamentSelection(){
   		Random rand = new Random();
   		int rand1 = rand.nextInt(currentPopulation.size()-1);
   		Results res1 = currentPopulation.get(rand1);
   		Results res2;
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

	public static void main(String[] args){
		try {
			fw = new FileWriter("trustLog.csv");
			bw = new BufferedWriter(fw);
		}catch(IOException e) {
			e.printStackTrace();
		}
		String a = Main.FILENAME;

		File scenarioFile = new File(a);
		//timeOutMAX= Integer.parseInt(args[1]);
		//INIT=Integer.parseInt(args[1]);
		//MAX_EVALS= Integer.parseInt(args[2]);
		INIT = (int)Math.round(MAX_EVALUATION*0.1);


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

	    while(INIT > 0 && TOTAL_EVAL<0.3*MAX_EVALUATION){
	    		boolean changed = false;
	    		System.out.println("Init: " +count);
	    		count++;
	    		chSize = rnd.nextInt(Scenario.getInstance().getMAX_VEHICLES());
	    		Chromosome ch = new Chromosome(chSize);
	    		Results res= myScenario.evaluate(ch,true,"");
	    		res.setMyCh(ch);

				changed = updateResult(res);

				if(changed == false){
					INIT--;
					if((INIT==0 || TOTAL_EVAL>0.25*MAX_EVALUATION) && currentPopulation.size()<Math.round(MAX_EVALUATION*0.1)){
						while (currentPopulation.size()<Math.round(MAX_EVALUATION*0.1)){
							System.out.println("Init: " +count);
							count++;
							chSize = rnd.nextInt(Scenario.getInstance().getMAX_VEHICLES());
							Chromosome ch2 = new Chromosome(chSize);
							Results res2= myScenario.evaluate(ch,true,"");
							res2.setMyCh(ch);
							currentPopulation.add(res2);
						}
					}
				}
				else if(changed == true && INIT<Math.round(MAX_EVALUATION*0.02)){
					INIT = (int)Math.round(MAX_EVALUATION*0.02);
				}
	    }

		ArrayList<Results> bestFront;


		int N = currentPopulation.size();
		System.out.println("Population Size: "+N);
		int MUTATION_RATE = 3;
		double random_population = 0.2;


	    while(MAX_EVALUATION>TOTAL_EVAL) {
		    	/*if (evals%10==0)
		    		writeRes(count);*/
				System.out.println(evals);
				evals++;
				int no_change = 0;
		    	ArrayList<Results> children = new ArrayList<>();
		    	for(int i=0;i<N;i++){
		    		if(TOTAL_EVAL>=MAX_EVALUATION){
		    			break;
					}
		    		Random rand = new Random();
		    		double p1 = rand.nextDouble();
		    		double p2 = rand.nextDouble();
		    		if(p2 < random_population){
						chSize = rnd.nextInt(Scenario.getInstance().getMAX_VEHICLES());
						Chromosome ch = new Chromosome(chSize);
						Results newRes= myScenario.evaluate(ch,true,"");
						newRes.setMyCh(ch);
						updateResult(newRes);
						children.add(newRes);
					}
		    		else{
						if(p1<0.5){
							Results res1 = TournamentSelection();
							Chromosome newCh = (Chromosome) res1.getMyCh().clone();
							newCh.mutate(MUTATION_RATE);
							Results newRes = Scenario.getInstance().evaluate(newCh,true,"");
							newRes.setMyCh(newCh);
							updateResult(newRes);
							children.add(newRes);
						}
						else{
							Results res1 = TournamentSelection();
							Results res2 = TournamentSelection();
							Chromosome newCh = new Chromosome(res1.getMyCh(),res2.getMyCh());
							Results newRes = Scenario.getInstance().evaluate(newCh,true,"");
							newRes.setMyCh(newCh);
							updateResult(newRes);
							children.add(newRes);
						}
					}
				}
		    	if(TOTAL_EVAL>MAX_EVALUATION){
		    		break;
				}
		    	children.addAll((ArrayList<Results>) currentPopulation.clone());
				bestFront = paretoFront(children);
				assignRank(children);
		    	currentPopulation.clear();
		    	while (currentPopulation.size()<N){
		    		ArrayList<Results> currentFront = paretoFront(children);
		    		if(currentPopulation.size() + currentFront.size() <= N){
						children.removeAll(currentFront);
						currentPopulation.addAll(currentFront);
					}
		    		else{
		    			Collections.sort(currentFront,new SparsityComparator());
		    			for(int i=0 ; i<(N-currentPopulation.size());i++){
		    				currentPopulation.add(currentFront.get(i));
						}
					}
				}

		    	for(Results res : bestFront){
					double em = (highestEmissions - lowestEmissions)/20;
					long emNorm = Math.round((res.getEmissions()-lowestEmissions)/em);
					if (emNorm > 20)
						emNorm = 20;
					if (emNorm < 1)
						emNorm = 1;
					if(emissionResults[(int) emNorm] == null){
						emissionResults[(int) emNorm] = res;
						no_change++;
					}
					else {
						if(emissionResults[(int)emNorm].getTotalCost() > res.getTotalCost()){
							emissionResults[(int) emNorm] = res;
							no_change++;
						}
					}

				}

		    	if(no_change==0){
		    		MUTATION_RATE++;
		    		random_population += 0.1;
		    		if(random_population>0.5){
		    			random_population=0.5;
					}
				}

			for(int i=0; i<=20; i++){
				if(emissionResults[i] != null){
					System.out.println(i + " : " + emissionResults[i].getTotalCost());
				}
				else {
					System.out.println(i + ": null");
				}
			}
			System.out.println("\n");
	    }

	    try {
	    bw.close();
	    }catch(IOException e) {
	    	e.printStackTrace();
	    }
	    
	    writeRes(INIT);
	    createZip();

		System.out.println("Final Results: ");
		for(int i=0; i<=20; i++){
			if(emissionResults[i] != null){
				System.out.println(i + " : " + emissionResults[i].getTotalCost());
			}
			else {
				System.out.println(i + ": null");
			}
		}
		System.out.println("\n");
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
	
	private static void createZip(){
		try{
			
			
		new File(outName).mkdir();
		
		for (int key=0;key<21;key++){
			Results myResult = emissionResults[key];
			new File(outName+"/"+key).mkdir();
			File dir = new File(outName+"/"+key);
			//Change working directory
			Scenario.getInstance().setWorkingDir(dir.getAbsolutePath());
			Scenario.getInstance().evaluate(myResult.getMyCh(),false,String.valueOf(key));
		}
		//Now copy directory structure to the zip
		String zipFile = outName+".zip";
		
		String srcDir = outName;
		
		try {

			FileOutputStream fos = new FileOutputStream(zipFile);

			ZipOutputStream zos = new ZipOutputStream(fos);
			
			File srcFile = new File(srcDir);
			
			ZipHelper.addDirToArchive(zos, srcFile,"");

			// close the ZipOutputStream
			zos.close();
			
		}
		catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void writeRes(int evals){

//		
		File file = new File("./"+evals+"-"+outName+".csv");

		try{
			DecimalFormat df = new DecimalFormat("#.00");
			PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
			//key = emNorm +":" + timeNorm + ":" +vehNorm +":"+distNorm;
			pw.write("Dimensions,4\nNormalised,20\nevals,"+evals+"\nkey,dist,Actualemissions,ActualTime,ActualVehicles,ActualDist,");
			for (Location l : Scenario.getInstance().getRVLocations()) {
				pw.write( l.getDescription() +",");
			}
			//pw.write(",,");
			for (Vehicle v : Scenario.getInstance().getVehicles()) {
				pw.write( v.getDescription() +",");
			}
			pw.write("\n");
			for (int key=0;key<=20;key++){
				Results myResults = emissionResults[key];
 				String buffer = key + ","+ df.format(myResults.getTotalCost());
				buffer = buffer + ","+ df.format(myResults.getEmissions())+"," +df.format(myResults.getMaxTime())+","+df.format(myResults.getVehicles())+","+df.format(myResults.getDistance());
				//buffer = buffer + ","+ df.format(me.myResults.getEmissions())+"," +df.format(me.myResults.getMaxTime())+","+df.format(me.myResults.getVehicles()+","+df.format(me.myResults.getDistance()));
				
				//Extra cols for stats
				//Count MicroDepot use
				
				HashMap<Location,Integer> depotCount = new HashMap<Location,Integer>();
				
				for (Gene g : myResults.getMyCh()) {
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
				
				for (Gene g : myResults.getMyCh()) {
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
}
