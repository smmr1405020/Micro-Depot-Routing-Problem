package deliveryRoutes;


import java.io.File;

public class Main {

	public static String FILENAME = "Dataset"+"\\"+"ny_1.csv";
	public static String OSMFILE_NAME = "ny_export.pbf";

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

		System.out.println(java.time.LocalDateTime.now());
	}
}
