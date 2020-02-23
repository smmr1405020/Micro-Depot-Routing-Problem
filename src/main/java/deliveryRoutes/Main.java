package deliveryRoutes;


import EvolutionaryAlgorithm.Baseline_Algorithm;
import EvolutionaryAlgorithm.MDVRP_NSGA2_modified;
import EvolutionaryAlgorithm.MDVRP_NSGA3_modified;
import EvolutionaryAlgorithm.my_algorithm_final;

import java.io.File;

public class Main {

	public static String FILENAME = "Dataset"+"\\"+"iowa_1.csv";
	public static String OSMFILE_NAME = "iowa_export.pbf";
	public static boolean setupFlag = false;

	public static void main(String[] args)
	{
		//Create a new instance of Model
	/*	Model sim = new Model();

		//Create a new instance of the CSV Reader
		CSVReader read = new CSVReader();
		
		//WIP file reader
		File infile = new File(FILENAME);
		read.readFile(infile, sim);

		//Run the function to create a path between destinations
		sim.run("test"); */

		//System.out.println(java.time.LocalDateTime.now());

		Main.FILENAME = "Dataset"+"\\"+"iowa_1.csv";
		setupFlag = false;
		Baseline_Algorithm.main(new String[1]);

		Main.FILENAME = "Dataset"+"\\"+"iowa_2.csv";
		setupFlag = false;
		Baseline_Algorithm.main(new String[1]);

		Main.FILENAME = "Dataset"+"\\"+"iowa_3.csv";
		setupFlag = false;
		Baseline_Algorithm.main(new String[1]);

		Main.FILENAME = "Dataset"+"\\"+"edinburghTest.csv";
		setupFlag = false;
		Baseline_Algorithm.main(new String[1]);

	}
}
