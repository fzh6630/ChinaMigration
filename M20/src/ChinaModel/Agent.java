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
	
	public boolean landbased;
	
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
		
		rational = model.random.nextBoolean(model.parameters.rou);
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
		double threshold = 1 - (444.0  - 74.829 * (model.schedule.getSteps()) + 9.779 * (model.schedule.getSteps()) * (model.schedule.getSteps()))/313355.0 + 444.0/(313355.0);
		
		if (threshold >= 1)
			threshold = 1;
		
		int i;
		
		/*for (i =0 ; i < insideEdges.numObjs ; i++)
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
		
*/

		if (moved < 1  && moveProb > 0.8){
		Edge edge;
		Agent agentL1;
		Agent agentL2;
		Agent agentL3;
		Bag edgesL1 = new Bag();
		Bag edgesL2 = new Bag();
		Bag edgesL3 = new Bag();
		Bag agentsL1 = new Bag();
		Bag agentsL2 = new Bag();
		Bag agentsL3 = new Bag();
		int j,k,m;
		
		edgesL1 = model.network.getEdgesIn(this);
		for (i = 0 ; i < edgesL1.numObjs; i ++){
			edge = (Edge) edgesL1.get(i);
			agentL1 = (Agent) edge.getOtherNode(this);
			agentsL1.add(agentL1);
		}
		
		for (i = 0 ; i < agentsL1.numObjs; i ++){
			agentL1 = (Agent) agentsL1.get(i);
			edgesL2 = model.network.getEdgesIn(agentL1);
			for (j = 0; j < edgesL2.numObjs; j++){
				edge = (Edge) edgesL2.get(j);
				agentL2 = (Agent) edge.getOtherNode(agentL1);
				if (!agentsL1.contains(agentL2) && agentL2.agentID!=this.agentID)
					agentsL2.add(agentL2);
			}
		}
		
		for (i = 0 ; i < agentsL2.numObjs; i ++){
			agentL2 = (Agent) agentsL2.get(i);
			edgesL3 = model.network.getEdgesIn(agentL2);
			for (j = 0; j < edgesL3.numObjs; j++){
				edge = (Edge) edgesL3.get(j);
				agentL3 = (Agent) edge.getOtherNode(agentL2);
				if (!agentsL2.contains(agentL3) && !agentsL1.contains(agentL3) && agentL3.agentID!=this.agentID)
					agentsL3.add(agentL3);
			}
		}
		double[] urbanInfluence = new double[model.firm.length];
		double ruralInfluence = 0.0;
 		for (i = 0; i < agentsL1.numObjs; i++){
			agentL1 = (Agent) agentsL1.get(i);
			if (agentL1.moved == 1)
				urbanInfluence[agentL1.employer.firmID] += 1;
			else
				ruralInfluence += 1;
		}
		for (i = 0; i < agentsL2.numObjs; i++){
			agentL2 = (Agent) agentsL2.get(i);
			if (agentL2.moved == 1)
				urbanInfluence[agentL2.employer.firmID] += 0.5;
			else
				ruralInfluence += 0.5;
		}
		for (i = 0; i < agentsL3.numObjs; i++){
			agentL3 = (Agent) agentsL3.get(i);
			if (agentL3.moved == 1)
				urbanInfluence[agentL3.employer.firmID] += 0.25;
			else
				ruralInfluence += 0.25;
		}
		double maxInfluence = 0;
		int maxInfluenceIndex = -1;
		for (i = 0; i < model.numProvinces; i++)
		{
			if (urbanInfluence[i] > maxInfluence){
				maxInfluence = urbanInfluence[i] * model.parameters.beta - ruralInfluence * model.parameters.alpha;
				maxInfluenceIndex = i;}
		}
		
//		if (maxInfluence >0)
//			moveProb = 1;
		
		if ( moved < 1 && ((moveProb ) > 1- (1- threshold)*(1+maxInfluence)*(2-Math.sqrt(model.firm[this.hukou-1].currentLand)/47.1) ) && isLegalAge()) {//test
			
		//	System.out.println(jobmarket.util.provinceID[0]);//test
			
			int randomDest = -1;
			
			double sumFDI = 0;
			double sumOri = 0;
			
			double rand;

			

			
		//	randomDest = model.random.nextInt(model.numProvinces);
			
			
			if (rational){		
				
				double sum2 = 0.0;
				double sum =0.0;
				for (i = 0; i < model.numProvinces ; i++){
					
					sum2 += model.firm[i].employees.numObjs;
				}
				
				for (i = 0; i < model.numProvinces ; i++){
					sum += model.firm[i].currentFdi * (1 + model.parameters.omega*(double) model.firm[i].employees.numObjs/(double)sum2);
				
				}
				
				for (i = 0; i < model.numProvinces ; i++){
					sumFDI += model.firm[i].currentFdi;
					sumOri += urbanInfluence[i];				
				}
			
				
			//	System.out.println(sumOri);
				rand = 1 * model.random.nextDouble();

			//	System.out.println(rand);
				for (i = 0; i < model.numProvinces ; i++){
					 
					rand -= model.firm[i].currentFdi * (1 + model.parameters.omega*(double) model.firm[i].employees.numObjs/(double)sum2) /sum;
					//System.out.println(rand);
					if (rand <= 0){
						randomDest = i;				
						break;
					}
				}
			}
			
			else{				
				double sum2 = 0.0;
				double sum =0.0;
				for (i = 0; i < model.numProvinces ; i++){
					
					sum2 += model.firm[i].employees.numObjs;
				}
				
				for (i = 0; i < model.numProvinces ; i++){
					sum += 1000 * (1 + model.parameters.omega*(double) model.firm[i].employees.numObjs/(double)sum2);
				
				}
				
				for (i = 0; i < model.numProvinces ; i++){
					sumFDI += 1000;
					sumOri += urbanInfluence[i];				
				}
			
				
			//	System.out.println(sumOri);
				rand = 1 * model.random.nextDouble();

			//	System.out.println(rand);
				for (i = 0; i < model.numProvinces ; i++){
					 
					rand -= 1000 * (1 + model.parameters.omega*(double) model.firm[i].employees.numObjs/(double)sum2) /sum;
					//System.out.println(rand);
					if (rand <= 0){
						randomDest = i;				
						break;
					}
				}
			}
			
			
			
			
			
			model.firm[this.originalProvince.firmID].numRuralAgents--;
			//System.out.println(randomDest);
			employer = model.firm[randomDest];
			model.firm[randomDest].employees.add(this);
			model.firm[this.originalProvince.firmID].origins.add(this);
			model.firm[randomDest].numEmployeesOrigins[this.originalProvince.firmID]++;
			
			double prob;
			
			//preferential attachment
			for (i = 0; i < model.firm[randomDest].employees.numObjs; i++){
				prob = model.parameters.c4/Math.pow(model.firm[randomDest].employees.numObjs, model.parameters.eta4);
				if( prob > 1)
					prob = 1;
			if(model.random.nextBoolean(prob)){
				Agent friend;
				int friendIndex;
				friendIndex = model.random.nextInt(model.firm[randomDest].employees.numObjs);
				friend = (Agent) model.firm[randomDest].employees.get(friendIndex);
			
				if (model.network.getEdge(this, friend) == null){
					model.network.addEdge(this, friend, new Double(4));
					if(model.random.nextBoolean(model.parameters.theta)){
						Bag fedges;
						fedges = model.network.getEdgesIn(friend);
						int f;
						f = model.random.nextInt(model.network.getEdgesIn(friend).numObjs);
						Edge fedge;
						fedge = (Edge)model.network.getEdgesIn(friend).get(f);
						if((Double)fedge.info < 3.5 & (Double)fedge.info > 1.5)
							model.network.removeEdge(fedge);
					}
				}
				if (model.random.nextBoolean(model.parameters.Cpa ))
				{
					int f1,f2;
					Agent agent3, agent4;
					Edge edge3, edge4;
					f1 = model.random.nextInt(model.network.getEdgesIn(this).numObjs);
					f2 = model.random.nextInt(model.network.getEdgesIn(friend).numObjs);

					edge3 = (Edge)model.network.getEdgesIn(this).get(f1);
					agent3 = (Agent) edge3.getOtherNode(this);
					edge4 = (Edge)model.network.getEdgesIn(friend).get(f2);
					agent4 = (Agent) edge4.getOtherNode(this);
					
					if (model.network.getEdge(this, agent4) == null)
						model.network.addEdge(this, agent4, new Double(4));
					
					if (model.network.getEdge(friend, agent3) == null)
						model.network.addEdge(friend, agent3, new Double(4));
				}
				
			}
			}
			moveToDest(model);
		}
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
		
		if(age >= 16 && age <= 44){
			if (currentYear != model.date.getYear()){
				age++;
			}
			if (age == 45){
			
				model.numAgentsOfLegalAge--;
			}
		}
		else if(age < 16){
			if (currentYear != model.date.getYear()){
				age++;
			}
			if (age == 16){
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
	
	public void setMoveProbability(CMModel model){
		double tau;
		
		tau =  1 - 31160.0 / 313355.0;
		
		if (age <= 30 && education > 8 && male ==1){
			group = 1;//High quality
			moveProb = ((1 - tau) * 55405.0 / 9076.0)* model.random.nextDouble(true,true)
					+1 - ((1 - tau) * 55405.0 / 9076.0);
		}
		else 
		{
			if (age > 30 && education <= 8){
			group = 3;
			moveProb = ((1 - tau) * 58348.0 / 3108.0)* model.random.nextDouble(true,true)
					+1 - ((1 - tau) * 58348.0 / 3108.0);
			}
			else{
			group = 2;
			moveProb = ((1 - tau) *  159159.0 /17983.0 )* model.random.nextDouble(true,true)
					+1 - ((1 - tau) *  159159.0 /17983.0 );
			}
		}
	}
	
	
	

	
}
