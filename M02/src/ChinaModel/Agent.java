package ChinaModel;



import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.field.network.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;



public class Agent implements Steppable{
	
	public int agentID ;
	
	public int age ;
	
	public int education ;
	
	public int male ;
	
	public Firm originalProvince ;
	
	public int moved = 0;
	
	public int hukou ;
	
	public int householdID;
	
	public Firm deferredAcceptanceFirm = null;

	public double deferredAcceptanceWage;

//	public int employerID;
	public Firm employer = null;

	public Firm startingEmployer = null;
	
	public double wage = 100;
	
	public CMModel model;
	
	public static int[] rank = new int[100];
	
	public int currentYear;
	
	public int currentMonth;
	
	public int group;
	
	public double moveProb;
	
	public boolean rational;
	
	public Bag insideEdges;
	
	public int pathLength;
	
	public int sizeOfNetworkL1;
	
	public int sizeOfNetworkL2;
	
	public int sizeOfNetworkL3;
	
	

	
	
	public Agent() {
	
		super();
		
	}
	
	public Agent(Firm fir, int ag, int edu, int mal, int mov, int huk, int i,int hid, CMModel m) {
		 
		super();
		
		originalProvince = fir;
		age = ag;
		education = edu;
		male = mal;
		moved = mov;
		hukou = huk;
		agentID = i;
		householdID = hid;
		model = m;
		
		//System.out.println(i);
		
		setMoveProbability(m);
		
		rational = model.random.nextBoolean(0.803);
		//moveProb = model.random.nextDouble(true,true);
				
		currentYear = m.date.getYear();
		currentMonth = m.date.getMonth();
		//System.out.println(currentMonth);
		
		pathLength = 9;
		
	
	}
	
	public void setAttributes(Firm fir, int ag, int edu, int mal, int mov, int huk, int i,int hid){
		originalProvince = fir;
		age = ag;
		education = edu;
		male = mal;
		moved = mov;
		hukou = huk;
		agentID = i;
		householdID = hid;
	}

	
	public void step(SimState state) {
		model = (CMModel) state;
				
		marketMatch(model);
				
		updateAge(model);
	}
	

	
	public void marketMatch(CMModel model)
	{
		double threshold = 1 - (444.0  - 74.829 * (model.schedule.getSteps()+1) + 9.779 * (model.schedule.getSteps()+1) * (model.schedule.getSteps()+1))/313355.0 + 444.0/(313355.0);
		
		if (threshold >= 1)
			threshold = 1;

		if ( moved < 1 && moveProb > threshold && isLegalAge()) {//test
			
		//	System.out.println(jobmarket.util.provinceID[0]);//test
			
			int randomDest = -1;
			
			int i;
			double sum = 0;
			double rand;

			

			
		//	randomDest = model.random.nextInt(model.numProvinces);
			if (rational){
				for (i = 0; i < model.numProvinces ; i++){
					sum += model.firm[i].currentFdi;
				}
			//	System.out.println(sum);
				rand = sum * model.random.nextDouble();

			//	System.out.println(rand);
				for (i = 0; i < model.numProvinces ; i++){
					rand -= model.firm[i].currentFdi;
					if (rand <= 0){
						randomDest = i;
						break;
					}
				}
			//	System.out.println(randomDest);
			}
			
			else{				
				randomDest = model.random.nextInt(model.numProvinces);
			}
			employer = model.firm[randomDest];
			model.firm[randomDest].employees.add(this);
			model.firm[this.originalProvince.firmID].origins.add(this);
			model.firm[randomDest].numEmployeesOrigins[this.originalProvince.firmID]++;
			
			moveToDest(model);
		}
	}
	
	public void moveToDest(CMModel model){
		
		Continuous2D yard = model.yard;
		
		Double2D me = model.yard.getObjectLocation(this);	
		
		model.yard.setObjectLocation(this, new Double2D(me.x + 4.5,
				me.y));
		
		moved = 1;


		
	}
	
	public void updateAge(CMModel model){
		Double2D me = model.yard.getObjectLocation(this);
		

		if (currentYear != model.date.getYear())
			age++;
		if (age == 16)
			model.numAgentsOfLegalAge++;
			
		currentYear = model.date.getYear();
		currentMonth = model.date.getMonth();
	}

	public boolean isEmployed() {
		return this.employer != null;
	}
	
	public boolean isLegalAge(){
		if(age >= 16 )
			return true;
		else
			return false;
	}
	
	public void setMoveProbability(CMModel model){
		double tau;
		
		tau =  1 - 31160.0 / 313355.0;
		
		if (age < 25 && education >= 9 && male ==1){
			group = 1;//High quality
			moveProb = ((1 - tau) * 55405.0 / 9076.0)* model.random.nextDouble(true,true)
					+1 - ((1 - tau) * 55405.0 / 9076.0);
		}
		else 
		{
			if (age > 25 && education < 9 && male == 0){
			group = 3;
			moveProb = ((1 - tau) * 43982.0 / 1563.0)* model.random.nextDouble(true,true)
					+1 - ((1 - tau) * 43982.0 / 1563.0);
			}
			else{
			group = 2;
			moveProb = ((1 - tau) * 213968.0 /20521.0 )* model.random.nextDouble(true,true)
					+1 - ((1 - tau) * 213968.0 /20521.0 );
			}
		}
	}
	
	
	

	
}
