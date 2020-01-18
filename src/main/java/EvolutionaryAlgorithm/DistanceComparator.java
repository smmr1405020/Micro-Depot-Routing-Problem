package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.util.Comparator;

public class DistanceComparator implements Comparator {

    @Override
    public int compare(Object  o, Object o1) {
        Results Res1 = (Results) o;
        Results Res2 = (Results) o1;
        if((Res1.getDistance() - Res2.getDistance()) > 0){
          return 1;
        }
        else if((Res1.getDistance() - Res2.getDistance()) < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
