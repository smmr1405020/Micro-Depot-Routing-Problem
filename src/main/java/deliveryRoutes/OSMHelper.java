package deliveryRoutes;

import EvolutionaryAlgorithm.Scenario;
import GeoUtils.Journey;
import GeoUtils.Location;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;


public class OSMHelper {
	private static GraphHopperOSM hopper=null;
	private static HashMap<String, Journey> cache = new HashMap<String, Journey>();

	/* Used for demonstration/testing/debugging
	 *
	public static void main(String[] args) {
		System.out.println("Car ");
		PathWrapper pw = getDist("car",50.113918,8.680377,50.114737,8.685336);
		System.out.println(pw.getDistance());
		PointList pl = pw.getPoints();
		for (int c=0; c < pl.size(); c++){
			System.out.println(pl.getLatitude(c) +","+pl.getLongitude(c));
		}

		System.out.println("\n Bike ");
		pw = getDist("bike",50.113918,8.680377,50.114737,8.685336);
		System.out.println(pw.getDistance());
		pl = pw.getPoints();
		for (int c=0; c < pl.size(); c++){
			System.out.println(pl.getLatitude(c) +","+pl.getLongitude(c));
		}

		System.out.println("Foot ");
		pw = getDist("foot",50.113918,8.680377,50.114737,8.685336);
		System.out.println(pw.getDistance());
		pl = pw.getPoints();
		for (int c=0; c < pl.size(); c++){
			System.out.println(pl.getLatitude(c) +","+pl.getLongitude(c));
		}

	}*/


	public static Journey getJourney(Location last, Location next, String type){
		String key = last.getLat()+""+last.getLon() +":" + next.getLat() +""+ next.getLon() +":" + type;
		Journey res = cache.get(key);
		if (res != null)
			return res;


		res = findJourney(last, next, type);
		cache.put(key, res);
		return res;
	}

	private static Journey findJourney(Location start, Location end, String type) {

		//System.out.println("OSM call "+ start.toString() +":"+end.toString());
		if (hopper==null)
			init();

		GHRequest request = new GHRequest(start.getLat(),start.getLon(),end.getLat(),end.getLon()).setVehicle(type);
		GHResponse response = hopper.route(request);
		if (response.hasErrors()) {
			throw new IllegalStateException("S= " + start + "e= " + end +". GraphHopper gave " + response.getErrors().size()
					+ " errors. First error chained.",
					response.getErrors().get(0)
			);
		}

		PathWrapper pw = response.getBest();

		Journey res = new Journey(start,end,"hopper");
		res.setDistanceKM(pw.getDistance()/1000);//Check
		res.setTravelTimeMS(pw.getTime());//Check
		ArrayList<Location> path = new ArrayList<Location>();
		PointList pl = pw.getPoints();
		//path.add(start);
		for (int c=0; c < pl.size();c++){
			path.add(new Location(pl.getLatitude(c),pl.getLongitude(c)));
		}
		//path.add(end);
		res.putAttribute(Journey.PATH, path);

		/*
		 * Need to check if a car route cannot access the actual start or end. If this is the case, add a walk
		 */

		if (type.equals("car")){
			//Check start
			Location carStart = new Location(pw.getPoints().getLat(0),pw.getPoints().getLon(0));
			if ((start.getLat() != carStart.getLat())||(start.getLon() != carStart.getLon())){
				Journey walkA = getJourney(start ,carStart,"foot");
				if (walkA.getDistanceKM() > 0.01) {
					//Get walk path
					res.putAttribute("walkA",  walkA.getAttribute(Journey.PATH));
					res.setDistanceKM(res.getDistanceKM() + walkA.getDistanceKM());//Check
					res.setTravelTimeMS(res.getTravelTimeMS() + walkA.getTravelTimeMS());//Check
				}
			}
			//Add walk to result?

			//Check end
			Location carEnd = new Location(pw.getPoints().getLat(pw.getPoints().getSize()-1),pw.getPoints().getLon(pw.getPoints().getSize()-1));
			if ((end.getLat() != carEnd.getLat())||(end.getLon() != carEnd.getLon())){
				Journey walkB = getJourney(end ,carEnd,"foot");
				res.putAttribute("walkB",  walkB.getAttribute(Journey.PATH));
				res.setDistanceKM(res.getDistanceKM() + walkB.getDistanceKM());//Check
				res.setTravelTimeMS(res.getTravelTimeMS() + walkB.getTravelTimeMS());//Check

			}
			//Add walk to result?

		}

		return res;
	}

	private static GraphHopperOSM init() {
		String fileName = Scenario.getInstance().getOsmFile();
		hopper = new GraphHopperOSM();
		hopper.setOSMFile(fileName);

		hopper.setCHEnabled(false); // CH does not work with shortest weighting (at the moment)

		// where to store GH files?
		String store ="./"+ fileName.replace(".", "-");
		File dir = new File(store);
		if (!dir.exists()){
			try{
				Path path = Paths.get(store);
				Files.createDirectories(path);
				//dir.mkdir();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		hopper.setGraphHopperLocation(store );
		hopper.setEncodingManager(new EncodingManager("car,bike,foot"));

		// this may take a few minutes
		hopper.importOrLoad();
		return hopper;
	}

}
