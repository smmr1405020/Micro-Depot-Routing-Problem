package GeoUtils;

public class OSMFileUtils {
	private static String osmConv = "./osmconvert";
	private static String osmFilter = "./osmfilter";
	
	public static void setOsmConv(String osmConv) {
		OSMFileUtils.osmConv = osmConv;
	}


	public static void setOsmFilter(String osmFilter) {
		OSMFileUtils.osmFilter = osmFilter;
	}
	
	/*
	 * Method to extract a small .OSM file for querying from a much larger file
	 * 
	 * 
	 */
	public static void extractData(Location currentLocation, String osmFile, String newFileName, boolean removeWays) {
		//Create bounds
		double[] bounds = Useful.getBounds(currentLocation); //Extract approx. 1 square mile centered 
		String tmpFileName = System.currentTimeMillis() +".osm";
		
		if (removeWays){
			Useful.executeCommand(osmConv +" "+osmFile +" -b="+bounds[0]+","+bounds[1] +","+bounds[2] +","+ bounds[3]+ " -o="+tmpFileName);
			Useful.executeCommand(osmFilter +" ./"+tmpFileName+" --drop-ways --drop-relations --drop-author --drop-version -o="+newFileName);
		}else{
			Useful.executeCommand(osmConv +" "+osmFile +" -b="+bounds[0]+","+bounds[1] +","+bounds[2] +","+ bounds[3]+ " -o="+newFileName);
		}
	}
	
	
	
}
