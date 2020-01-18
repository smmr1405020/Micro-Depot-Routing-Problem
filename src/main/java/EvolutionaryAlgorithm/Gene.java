package EvolutionaryAlgorithm;

import deliveryRoutes.Vehicle;

import java.io.Serializable;
import java.util.Random;

public class Gene implements Cloneable,Serializable{
	
	private Random rnd = new Random();
	//private Scenario myScenario = Scenario.getInstance();
	Vehicle rvVehicle;
	int insertPoint;
	int delCount;
	int location;
	
	public Gene(){
		this.randomize();
	}
	
	public Object clone() {
		  try {
			  Gene res = (Gene) super.clone();
		   return res;
		 }
		  catch (CloneNotSupportedException e) {
		   System.out.println("CloneNotSupportedException comes out : "+e.getMessage());
		   return null;
		  }
		 }
	public void randomize(){
		rvVehicle  = Scenario.getInstance().getRVVehicle(Scenario.getInstance().getRndRVVehicleIndx());
		delCount = rvVehicle.getCapacity();
		location = Scenario.getInstance().getRndRVLocatioIndx();
		insertPoint = Scenario.getInstance().getRndInsertPointIndx();
		
	}
	
	public void mutate(){
		
		int choice = rnd.nextInt(3);
		if (choice==0){
			if (rnd.nextBoolean()){
				delCount++;
				if (delCount>rvVehicle.getCapacity())delCount=rvVehicle.getCapacity();
			}else{
				delCount--;
				if (delCount <1) delCount=1;
			}
		}
		else if(choice ==1)
			location = Scenario.getInstance().getRndRVLocatioIndx();
		
		else if(choice ==2)
			rvVehicle = Scenario.getInstance().getRVVehicle(Scenario.getInstance().getRndRVVehicleIndx());
		//else if(choice ==2)
		//	insertPoint = myScenario.getRndInsertPointIndx();
	}
}
