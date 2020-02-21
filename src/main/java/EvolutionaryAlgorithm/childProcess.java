package EvolutionaryAlgorithm;

import java.io.*;
import java.util.ArrayList;

public class childProcess implements Runnable{

    Thread t1;


    public childProcess(String str){
        t1 = new Thread(this,str);
        t1.start();
    }

    public void run() {
        String [] str = new String[5];
        Baseline_Algorithm.main(str);
    }

}
