package deliveryRoutes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
	
	void readFile(File file, Model model)
	{
		Model sim = model;
		//Try Catch to stop the program crashing if there is an error
		try {
			//Open a new BufferedReader to prepare reading the CSV file
			BufferedReader br = new BufferedReader(new FileReader(file));
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
			while ((readin = br.readLine()) != null)
			{
				
				//Split the read in line into an array on each comma
				String[] splitlines = readin.split(",");
				if(splitlines.length > 0)
				{
					//If the program reads the keyword "VEHICLE", create a new vehicle and get the properties for it
					if(splitlines[0].equals("VEHICLE"))
					{
						//Assign values from the CSV file to appropriate variables
						startX = Double.parseDouble(splitlines[1]);
						startY = Double.parseDouble(splitlines[2]);
						emissions = Double.parseDouble(splitlines[3]);
						speed = Integer.parseInt(splitlines[4]);
						capacity = Integer.parseInt(splitlines[5]);
						fixedCost = Double.parseDouble(splitlines[7]);
						runningCost = Double.parseDouble(splitlines[6]);
						description = splitlines[8];
						//Create a new vehicle if there is another vehicle in the CSV file
						if(firstVehicle == false)
						{
							myVan = new Vehicle("car");
						}
						//Add appropriate variables to the instance of the vehicle
						myVan.startLocation(startX, startY, 0.0);
						myVan.setEmissions(emissions);
						myVan.setSpeed(speed);
						myVan.setCapacity(capacity);
						myVan.setCostKM(runningCost);
						myVan.setFixedCost(fixedCost);
						myVan.setDescription(description);
						//Add the vehicle to the simulation
						sim.addVehicle(myVan);
					
						//If there is another vehicle the program should be told it needs to create a new instace of a vehicle
						firstVehicle = false;
						isDelivery = false;
					}
					else if(splitlines[0].equals("DELIVERY"))
					{
						destX = Double.parseDouble(splitlines[1]);
						destY = Double.parseDouble(splitlines[2]);
						weight = Integer.parseInt(splitlines[3]);
					
						Delivery myDelivery = new Delivery(destX,destY,0.0,"Delivery");
						myDelivery.setWeight(weight);
						isDelivery = true;
					
						if (rv != null) {
							rv.includeDelivery(myDelivery);
						}else
							myVan.addDelivery(myDelivery);
						
					}else if(splitlines[0].equals("RV")) {
						startX = Double.parseDouble(splitlines[1]);
						startY = Double.parseDouble(splitlines[2]);
						emissions = Double.parseDouble(splitlines[3]);
						speed = Integer.parseInt(splitlines[4]);
						capacity = Integer.parseInt(splitlines[5]);
						fixedCost = Double.parseDouble(splitlines[7]);
						runningCost = Double.parseDouble(splitlines[6]);
						description = splitlines[7];
						
						Vehicle rvVehicle  = new Vehicle("car");//car
						//Add appropriate variables to the instance of the vehicle
						rvVehicle.startLocation(startX, startY, 0.0);
						rvVehicle.setEmissions(emissions);
						rvVehicle.setSpeed(speed);
						rvVehicle.setCapacity(capacity);
						rvVehicle.setCostKM(runningCost);
						rvVehicle.setFixedCost(fixedCost);
						rvVehicle.setDescription(description);
						rv = new Rendezvous(startX,startY,0,rvVehicle);
						System.out.println("RV");
					
					}else if(splitlines[0].equals("ENDRV")) {
						System.out.println("ENDRV");
						myVan.addDelivery(rv);
						rv = null;
					}
				
					boolean isComment = false;
					if(splitlines.length > 1)
					{
						if(parsableCheck(splitlines[1]) == false)
						{
							isComment = true;
						}
					}
					
					if(isDelivery == true && splitlines[0].equals("") && isComment == false)
					{
						destX = Double.parseDouble(splitlines[1]);
						destY = Double.parseDouble(splitlines[2]);
						weight = Integer.parseInt(splitlines[3]);
					
						Delivery myDelivery = new Delivery(destX,destY,0.0,"Destination");
						myDelivery.setWeight(weight);
						if (rv != null) {
							rv.includeDelivery(myDelivery);
						}else
							myVan.addDelivery(myDelivery);
						//myVan.addDelivery(myDelivery);
					}
				}
			} 			
			
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Make sure all entries are valid in the CSV file");
		}
	}
	
	private boolean parsableCheck(String string)
	{
		try
		{
			double checkparse = Double.parseDouble(string);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
