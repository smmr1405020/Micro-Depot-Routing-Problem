package EvolutionaryAlgorithm;

public class MarkDownHelper {

	private String name;
	private String vehicles = "";


	
	public void addVehicle(String id, String desc, String dels, String time, String emissions, String dist){
		vehicles += "## Delivery details for vehicle ID: "+id+" " +desc + " \n"  + dels  + "\n";
		vehicles += "### Total distance travelled: "+dist+" km \n" +
		"### Total time taken: "+time+ " minutes \n" +
		"### Total emissions:"+ emissions+" g of CO2\n" ;
		
	}
	public void setName(String aName){
		name = aName;
	}
	
	public String getMarkDown(){
	return "# Plan : "+name+"\n" +vehicles ;
	}
}
