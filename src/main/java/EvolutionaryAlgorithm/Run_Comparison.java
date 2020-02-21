package EvolutionaryAlgorithm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Run_Comparison {

    /*
        Baseline Algorithm: "original_mapelites.csv"
        My Final Algorithm: "myalgo_final.csv"
        My Final Algorithm without mutation rate: "myalgo_womutation.csv"
        My Final Algorithm without taboo list: "myalgo_wotaboolist.csv"

        NSGA2: "myalgo_NSGA2_modified.csv"
        NSGA3: "myalgo_NSGA3_modified.csv"

     */

    public static String FILE1 = "original_mapelites.csv";
    public static String FILE2 = "myalgo_final.csv";


    public static void main(String args[]){
        HashMap<String,Double> original = new HashMap<>();
        HashMap<String,Double> myalgo = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader( new FileReader(FILE1));
            while(true){
                String s = br.readLine();
                if(s==null){
                    break;
                }
                String[] s1 = s.split(",");
                original.put(s1[0],Double.parseDouble(s1[1]));
            }
            br.close();

            br = new BufferedReader( new FileReader(FILE2));
            while(true){
                String s = br.readLine();
                if(s==null){
                    break;
                }
                String[] s1 = s.split(",");
                myalgo.put(s1[0],Double.parseDouble(s1[1]));
            }
            br.close();

            int new_in_org = 0;
            int new_in_myalg = 0;
            int org_better = 0;
            int myalg_better = 0;
            int equal = 0;

            double original_best = Double.MAX_VALUE;
            for(String key : original.keySet()){
                if(original_best>original.get(key)){
                    original_best = original.get(key);
                }
            }

            double myalgo_best = Double.MAX_VALUE;
            for(String key : myalgo.keySet()){
                if(myalgo_best>myalgo.get(key)){
                    myalgo_best = myalgo.get(key);
                }
            }

            for( String key: myalgo.keySet()){
                double val_my = myalgo.get(key);
                if(original.containsKey(key)){
                    double val_org = original.get(key);
                    if(val_my<val_org){
                        myalg_better++;
                    }
                    else if(val_my>val_org) {
                        org_better++;
                    }
                    else {
                        equal++;
                    }
                }
                else {
                    new_in_myalg++;
                }
            }





            new_in_org = original.size() - (org_better+myalg_better+equal);

            System.out.println("FILE1 found but FILE2 didn't: "+new_in_org);
            System.out.println("FILE2 found but FILE1 didn't: "+new_in_myalg);
            System.out.println("FILE1 found better than FILE2 found: "+org_better);
            System.out.println("FILE2 found better than FILE1 found: "+myalg_better);
            System.out.println("Equal: "+equal);
            System.out.println("\n\n");
            System.out.println("FILE1 Better: "+(new_in_org+org_better));
            System.out.println("FILE2 Better: "+(new_in_myalg+myalg_better));
            System.out.println("Original best cost: "+original_best);
            System.out.println("MyAlgo best cost: "+myalgo_best);


           /* System.out.println(new_in_org+" "+new_in_myalg+" "+org_better+" "+myalg_better+" "+equal);
            System.out.println(myalg_better);*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
