package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.util.Comparator;

public class EmissionComparator implements Comparator {

    @Override
    public int compare(Object  o, Object o1) {
        Results Res1 = (Results) o;
        Results Res2 = (Results) o1;
        if((Res1.getEmissions() - Res2.getEmissions()) > 0){
          return 1;
        }
        else if((Res1.getEmissions() - Res2.getEmissions()) < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
