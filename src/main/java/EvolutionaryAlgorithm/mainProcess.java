package EvolutionaryAlgorithm;

public class mainProcess {
    public static void main(String args[]) throws InterruptedException {
        childProcess cp1 = new childProcess("iowa_1.csv,iowa_export_1.pbf");
        childProcess cp2 = new childProcess("iowa_2.csv,iowa_export_2.pbf");
        childProcess cp3 = new childProcess("iowa_3.csv,iowa_export_3.pbf");

        cp1.t1.join();
        cp2.t1.join();
        cp3.t1.join();


    }
}
