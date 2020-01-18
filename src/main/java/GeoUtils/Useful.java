package GeoUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * A class of useful methods that can be reused in many situations
 * All created as static for ease of use
 */
public class Useful {
	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			System.out.println("Executing " + command);
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
	
	public static double[] getBounds(Location middle){
	     double lon_change = 0.01;// approx 1 sq mile
	     double lat_change = 0.004;
		 
	     double lat_min = middle.getLat() - lat_change;
	     double lon_min =  middle.getLon() + lon_change;
	     double lat_max =  middle.getLat() + lat_change;
	     double lon_max = middle.getLon() - lon_change;
	     double[] bounds = new double[4];
	     bounds[0] = lon_max;
	     bounds[1] = lat_min;
	     bounds[2] = lon_min;
	     bounds[3] = lat_max;
	     
	     
	     return bounds;
	}
	
	public static double haversine(Location start, Location end){
		//Adapted from https://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
		
		Double latDistance = toRad(end.getLat()-start.getLat());
        Double lonDistance = toRad(end.getLon()-start.getLon());
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(toRad(start.getLat())) * Math.cos(toRad(end.getLat())) *   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = 6371 * c;//6371 = Earth's radius in KMs
         
        return distance;
 
    }
     
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    
	}
}
