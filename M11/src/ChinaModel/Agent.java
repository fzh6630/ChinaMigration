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
	
	
	public Agent() {
	
		super();
		
	}
	
	public Agent(Firm fir, int ag, int edu, int mal, int mov, int huk, int i, CMModel m) {
	 
		super();
		
		originalProvince = fir;
		age = ag;
		education = edu;
		male = mal;
		moved = mov;
		hukou = huk;
		agentID = i;
		model = m;
		
		//System.out.println(i);
		
		if (age < 25 && education >= 9 && male ==1){
			group = 1;//High quality
			moveProb = 0.5 * model.random.nextDouble(true,true) + 0.5;
			rational = model.random.nextBoolean(0.8);
		}
		else 
		{
			if (age > 25 && education < 9 && male == 0){
			group = 3;
			moveProb =  -2 + 3 * model.random.nextDouble(true,true);//Low quality
			if (moveProb < 0)
				moveProb = 0;
			rational = model.random.nextBoolean(0.2);
			}
			else{
			group = 2;
			moveProb = model.random.nextDouble(true,true);//Medium quality
			rational = model.random.nextBoolean(0.5);
			}
		}
		//moveProb = model.random.nextDouble(true,true);
				
		currentYear = m.date.getYear();
		currentMonth = m.date.getMonth();
		//System.out.println(currentMonth);
	
	}
	
	public void setAttributes(Firm fir, int ag, int edu, int mal, int mov, int huk, int i){
		originalProvince = fir;
		age = ag;
		education = edu;
		male = mal;
		moved = mov;
		hukou = huk;
		agentID = i;
	}

	
	public void step(SimState state) {
		model = (CMModel) state;
				
		marketMatch(model);
				
		updateAge(model);
	}
	

	
	public void marketMatch(CMModel model)
	{
		double threshold =  1 - 431.0 / (270744.0 + 431.0) * Math.exp(0.057 * model.schedule.getTime());
		
		if (threshold >= 1)
			threshold = 1;
		
		int i;
		insideEdges = model.insideNetwork.getEdgesIn(this);
		for (i =0 ; i < insideEdges.numObjs ; i++)
		{
			Edge edge;
			edge = (Edge) insideEdges.get(i);
			Agent otherAgent;
			otherAgent = (Agent) edge.getOtherNode(this);
			if (otherAgent.moved == 1)
			{
				moveProb = 0.5 * model.random.nextDouble(true,true) + 0.5;
			}
		}

		if ( moved < 1 && moveProb > threshold && isLegalAge()) {//test
			
		//	System.out.println(jobmarket.util.provinceID[0]);//test
			
			int randomDest = -1;
			
			double sumFDI = 0;
			double sumOri = 0;
			double rand;

			

			
		//	randomDest = model.random.nextInt(model.numProvinces);
			if (rational){
				for (i = 0; i < model.numProvinces ; i++){
					sumFDI += model.firm[i].currentFdi;
					sumOri += model.firm[i].numEmployeesOrigins[this.originalProvince.firmID];
				}
			//	System.out.println(sumOri);
				rand = model.random.nextDouble();

			//	System.out.println(rand);
				for (i = 0; i < model.numProvinces ; i++){
					rand -= (model.firm[i].currentFdi / sumFDI
							+ (model.firm[i].numEmployeesOrigins[this.originalProvince.firmID]+1) / (sumOri+1));
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
			
			for (i =0 ; i < insideEdges.numObjs ; i++)
			{
				Edge edge;
				edge = (Edge) insideEdges.get(i);
				Agent otherAgent;
				otherAgent = (Agent) edge.getOtherNode(this);
				if (otherAgent.moved == 1)
				{
					randomDest = otherAgent.employer.firmID;
				}
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
		
		model.yard.setObjectLocation(this, new Double2D(me.x + 5,
				yard.getHeight() * 0.1 + employer.firmID * 2 - 0.5));
		
		moved = 1;


		
	}
	
	public void updateAge(CMModel model){
		Double2D me = model.yard.getObjectLocation(this);
		
		if(age >= 16 && age <= 44){
			if (currentYear != model.date.getYear()){
				age++;
			}
			if (age == 45){
				if (moved == 0){
					model.yard.setObjectLocation(this, new Double2D(me.x - 3,
							me.y));}
				model.numAgentsOfLegalAge--;
			}
		}
		else if(age < 16){
			if (currentYear != model.date.getYear()){
				age++;
			}
			if (age == 16){
				if (moved == 0){
					model.yard.setObjectLocation(this, new Double2D(me.x + 3,
							me.y));}
				model.numAgentsOfLegalAge++;
			}
		}
		else{
			if (currentYear != model.date.getYear()){
				age++;
			}
		}
				
		currentYear = model.date.getYear();
		currentMonth = model.date.getMonth();
	}

	public boolean isEmployed() {
		return this.employer != null;
	}
	
	public boolean isLegalAge(){
		if(age >= 16 && age <= 44)
			return true;
		else
			return false;
	}
	

	
}
