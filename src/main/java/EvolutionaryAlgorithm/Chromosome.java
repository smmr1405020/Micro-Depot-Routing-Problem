package EvolutionaryAlgorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Chromosome extends ArrayList<Gene> implements Cloneable,Serializable{
	private Random rnd = new Random();
	
	public Chromosome(int len){
		super();
		
		if(len > Scenario.getInstance().getMAX_VEHICLES()){
			len = Scenario.getInstance().getMAX_VEHICLES();
			}
	
		for (int c=0;c< len; c++){
			super.add(new Gene());
		}
	}
	
	public Chromosome(Chromosome p1, Chromosome p2){
		for (int x=0; x < 10; x++) {
			if (rnd.nextBoolean()){
				if (x < p1.size()) {

					Gene g = p1.get(x);
					this.add((Gene)g.clone());
					if (this.size()> Scenario.getInstance().getMAX_VEHICLES())
						return;
				}
			}else {
					if (x < p2.size()) {

						Gene g = p2.get(x);
						this.add((Gene)g.clone());
						if (this.size()> Scenario.getInstance().getMAX_VEHICLES())
							return;
					}	
			}
		}
		
		
		/*for (Gene g : p1){
			if (rnd.nextBoolean()){
				this.add((Gene)g.clone());
				if (this.size()>Scenario.getInstance().getMAX_VEHICLES())
					return;
			}
		}
		for (Gene g : p2){
			if (rnd.nextBoolean()){
				this.add((Gene)g.clone());
				if (this.size()>Scenario.getInstance().getMAX_VEHICLES())
					return;
			}
		}*/
	}
	
	public Object clone() {
		Chromosome res = (Chromosome) super.clone();
		return res;
	}
	
	public void mutate(){
		if (super.size()==0){//Can only add new
			super.add(new Gene());
			return;
		}
		double ch = rnd.nextInt(4);
		if (ch ==0){// mutate a random gene
			Gene g = super.get(rnd.nextInt(super.size()));
			g.mutate();

		}else if (ch==1){//Change the gene order
			if (super.size()==1) return;
			Gene g = super.get(rnd.nextInt(super.size()));
			super.remove(g);
			int insert = rnd.nextInt(super.size());
			super.add(insert,g);
		}
		else if (ch==2){//Add new
			if (super.size()< Scenario.getInstance().getMAX_VEHICLES()){
				Gene g = new Gene();
				super.add(g);
			}
		}
		else{//Remove gene
			if (super.size() >1){
				Gene g = super.get(rnd.nextInt(super.size()));
				super.remove(g);
			}
		}
	}

    public void mutate( int no_of_mutations){
        for(int i=0; i<no_of_mutations; i++){
            if (super.size()==0){//Can only add new
                super.add(new Gene());
                continue;
            }
            double ch = rnd.nextInt(4);
            if (ch ==0){// mutate a random gene
                Gene g = super.get(rnd.nextInt(super.size()));
                g.mutate();

            }else if (ch==1){//Change the gene order
                if (super.size()==1) return;
                Gene g = super.get(rnd.nextInt(super.size()));
                super.remove(g);
                int insert = rnd.nextInt(super.size());
                super.add(insert,g);
            }
            else if (ch==2){//Add new
                if (super.size()< Scenario.getInstance().getMAX_VEHICLES()){
                    Gene g = new Gene();
                    super.add(g);
                }
            }
            else{//Remove gene
                if (super.size() >1){
                    Gene g = super.get(rnd.nextInt(super.size()));
                    super.remove(g);
                }
            }
        }
    }

	public void mutate(double mutation_rate){
		Random rand = new Random();
		double d = 0;

		while(d<mutation_rate){
			d = rand.nextDouble();
			if (super.size()==0){//Can only add new
				super.add(new Gene());
				continue;
			}
			double ch = rnd.nextInt(4);
			if (ch ==0){// mutate a random gene
				Gene g = super.get(rnd.nextInt(super.size()));
				g.mutate();

			}else if (ch==1){//Change the gene order
				if (super.size()==1) return;
				Gene g = super.get(rnd.nextInt(super.size()));
				super.remove(g);
				int insert = rnd.nextInt(super.size());
				super.add(insert,g);
			}
			else if (ch==2){//Add new
				if (super.size()< Scenario.getInstance().getMAX_VEHICLES()){
					Gene g = new Gene();
					super.add(g);
				}
			}
			else{//Remove gene
				if (super.size() >1){
					Gene g = super.get(rnd.nextInt(super.size()));
					super.remove(g);
				}
			}
		}
	}



}
