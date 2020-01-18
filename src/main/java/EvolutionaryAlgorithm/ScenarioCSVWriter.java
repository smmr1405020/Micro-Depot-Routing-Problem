package EvolutionaryAlgorithm;

import GeoUtils.Location;
import deliveryRoutes.Delivery;
import deliveryRoutes.Rendezvous;
import deliveryRoutes.Vehicle;

import java.io.*;
import java.util.Random;


public class ScenarioCSVWriter {
	
	void writeFile(String file)
	{
		//Try Catch to stop the program crashing if there is an error
		try {
			//Open a new BufferedReader to prepare reading the CSV file
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br_maind = new BufferedReader(new FileReader("main_depots_iowa.txt")) ;
			BufferedReader br_md = new BufferedReader(new FileReader("micro_depots_iowa.txt"));
			BufferedReader br_dest = new BufferedReader(new FileReader("deliveries_iowa.txt"));


			BufferedWriter bw_newds = new BufferedWriter(new FileWriter("mdrds_5.csv"));

			//A variable to write each line to
			String readin = "";
			//Variables to write the CSV vehicle details to
			double startX = 0;
			double startY = 0;
			double emissions = 0;
			int speed = 0;
			int capacity = 0;
			double fixedCost=0;
			double runningCost=0;
			String description;
			String routeType;
			String sample_rvpoint ="";
			String sample_delivery="";
			
			//Variables to write the CSV delivery details to
			double destX = 0;
			double destY = 0;
			int weight = 0;
			//An initial instance of the vehicle
			Vehicle myVan = new Vehicle("car");//fix
			
			//Used to hold a pointer to an RV object
			Rendezvous rv = null;
			//A check for if the vehicle is the first vehicle in the CSV file
			boolean firstVehicle = true;
			//A check for if the details being read are deliveries
			boolean isDelivery = false;
			//Continue reading the file for as long as there is content in it

			Random rand = new Random();

			double [][] microdepot_loc = new double[1000][2];
			double [][] delivery_loc = new double[1000][2];

			double p1 = 0.68 + (double)(rand.nextInt(3))/ 10;
			double p2 = 0.38 + (double)(rand.nextInt(6)) / 10;

			int num_microdepots_desired = 0;
			int num_deliveries_desired = 0;

			while( (readin = br_md.readLine()) != null){
				double p3 = (double)(rand.nextInt(99)) / 100;
				if(p3 < p1){
					String [] splitlines = readin.split(",");
					microdepot_loc[num_microdepots_desired][0] = Double.parseDouble(splitlines[1]);
					microdepot_loc[num_microdepots_desired][1] = Double.parseDouble(splitlines[0]);
					num_microdepots_desired++;
				}
			}

			while( (readin = br_dest.readLine()) != null){
				double p3 = (double)(rand.nextInt(99))/ 100;
				if(p3 < p2){
					String [] splitlines = readin.split(",");
					delivery_loc[num_deliveries_desired][0] = Double.parseDouble(splitlines[1]);
					delivery_loc[num_deliveries_desired][1] = Double.parseDouble(splitlines[0]);
					num_deliveries_desired++;
				}
			}

			int p3 = rand.nextInt(2);
			int p3_temp = 0;
			double [] maindepot_loc = new double[2];

			while( (readin = br_maind.readLine()) != null){
				if(p3_temp == p3){
					String [] splitlines = readin.split(",");
					maindepot_loc[0] = Double.parseDouble(splitlines[1]);
					maindepot_loc[1] = Double.parseDouble(splitlines[0]);
					break;
				}
				p3_temp++;
			}


			int num_microdepots = 0;
			int num_deliveries = 0;

			while ((readin = br.readLine()) != null)
			{
				
				//Split the read in line into an array on each comma
				String nreadin = new String(readin);
				String[] splitlines = readin.split(",");
				if(splitlines.length > 0)
				{
					//If the program reads the keyword "VEHICLE", create a new vehicle and get the properties for it
					if((splitlines[0].equals("VEHICLE"))||(splitlines[0].equals("RVVEHICLE")))
					{
						//Assign values from the CSV file to appropriate variables
						startX = Double.parseDouble(splitlines[1]);
						startY = Double.parseDouble(splitlines[2]);

                        /*System.out.println(nreadin);
                        System.out.println(splitlines[1]);
                        System.out.println(String.valueOf(maindepot_loc[0]));*/
						nreadin = nreadin.replace(splitlines[1],String.format("%.4f",maindepot_loc[0]));
						nreadin = nreadin.replace(splitlines[2],String.format("%.4f",maindepot_loc[1]));

                        System.out.println(nreadin+"\n");

						bw_newds.append(nreadin+"\n");
					}
					else if(splitlines[0].equals("DELIVERY"))
					{
						if(sample_delivery.equals("")){
							sample_delivery= readin;

							for(int i = 0; i<num_deliveries_desired;i++){
								int w = 2 + rand.nextInt(18);
								String temp = "DELIVERY,"+String.format("%.4f",delivery_loc[i][0])+","+String.format("%.4f",delivery_loc[i][1])+","+w+",,,,,,";
								bw_newds.append(temp+"\n");
							}
						}


						
					}else if(splitlines[0].equals("RVPOINT"))
					{
						if(sample_rvpoint.equals("")){
							sample_rvpoint= readin;

							for(int i = 0; i<num_microdepots_desired;i++){
								String temp = "RVPOINT,"+String.format("%.4f",microdepot_loc[i][0])+","+String.format("%.4f",microdepot_loc[i][1])+","+"Micro Depot "+(i+1)+",,,,,,";
								bw_newds.append(temp+"\n");
							}
						}

						
					}else
					{
						bw_newds.append(readin+"\n");
						
					}

				}
				else{
					bw_newds.append(readin+"\n");
				}
			} 			
			
			br.close();

			bw_newds.flush();
			bw_newds.close();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Make sure all entries are valid in the CSV file");
		}
	}

	public static void main(String args []){

		ScenarioCSVWriter sw = new ScenarioCSVWriter();
		sw.writeFile("edinburghTest.csv");
	}

}
