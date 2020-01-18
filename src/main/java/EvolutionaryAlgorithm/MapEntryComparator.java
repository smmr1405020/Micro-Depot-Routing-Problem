package EvolutionaryAlgorithm;

import deliveryRoutes.Results;

import java.util.Comparator;

public class MapEntryComparator implements Comparator {

    @Override
    public int compare(Object  o, Object o1) {
        MAP_Entry Res1 = (MAP_Entry) o;
        MAP_Entry Res2 = (MAP_Entry) o1;
        if((Res1.count - Res2.count) > 0){
          return 1;
        }
        else if((Res1.count - Res2.count) < 0){
            return -1;
        }
        else{
            if(Res1.myResults.getMyAge() - Res2.myResults.getMyAge() > 0){
                return 1;
            }
            else if(Res1.myResults.getMyAge() - Res2.myResults.getMyAge() < 0){
                return -1;
            }
            return 0;
        }
    }
}
