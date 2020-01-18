package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class Reader {

	public static void main(String[] args){
		
		HashMap<String, MAP_Entry> map = new HashMap<String, MAP_Entry>();
		try
        {   
            // Reading the object from a file
            FileInputStream file = new FileInputStream(args[1]);
            ObjectInputStream in = new ObjectInputStream(file);
             
            // Method for deserialization of object
            map = (HashMap<String, MAP_Entry>)in.readObject();
             
            in.close();
            file.close();

        }
         
        catch(IOException ex)
        {
        	ex.printStackTrace();
            System.out.println("IOException is caught");
        }
         
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
		
		int[][] stats= new int [5][30];
	
		
		for(String k : map.keySet()){
			System.out.println(k);
			String[] data = k.split(":");
			for (int c=0; c < data.length;c++){
				int v = Integer.parseInt(data[c]);
				
				stats[c][v]++;
				}
			}
		System.out.println("\tEm \t\tF Cst \t\tR Cst \t\tTime\t\tVeh");
		for (int c=0; c < 30; c++){
			System.out.print(c + ".\t" );
			for (int x=0; x < 5; x++){
				System.out.print(""+stats[x][c]+"\t\t");
			}
			System.out.println();
		}
		
		String key = args[2];
		File scenarioFile = new File(args[0]);
		Scenario myScenario = Scenario.getInstance();
		myScenario.setup(scenarioFile);

		MAP_Entry me =map.get(key);
		
		Results firstres= myScenario.evaluate(me.myChromo,false,"");




	}
}
