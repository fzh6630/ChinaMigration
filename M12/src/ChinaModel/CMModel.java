package ChinaModel;


import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.util.ArrayList;  




public class CMModel extends SimState{
	
	public Agent[] agent;
	
	public Firm[] firm;
	
	public Date date;
	
	public FirmList firmList;
	
	public DataOutput dataOutput;
	
	public Continuous2D yard = new Continuous2D(1.0,100,100);
	
	public int numAgents = 31335;
	
	public int numAgentsOfLegalAge = 0;
	
	public int numProvinces = 31;
	
	public Network network = new Network(false);
	
	public Bag[] nuclearFamily;
	
	public int sizeOfNuclearFamily;
	
		
	public CMModel(long seed) {		
		super(seed);
	}
	
	
	public int getNumMoved(){
		int num = 0, i;
		for (i = 0; i < numAgents; i++){
			if (agent[i].moved == 1)
				num++;		
		}
		return num;
	}
	

	
	public void start() {		
		super.start();
		
		this.setSeed(10000);
		
		random.setSeed(10000);
		
		date = new Date();
		
		schedule.scheduleRepeating(date,1,1);

		yard.clear();			
		
		readProvinceData();
		
		readIndivialData();
		
		setNuclearFamilyNetwork();
		
		setVillageNetwork();

		setProvinceNetwork();
/*		
		Agent agent,agent2;		
		Bag bag;
		int i;
		bag = network.getEdgesIn(this.agent[399]);
		Edge edge;
		for (i = 0; i < bag.numObjs; i++){
			edge = (Edge) bag.get(i);
			agent2 = (Agent)edge.getOtherNode(this.agent[399]);
			System.out.println(agent2.agentID);
		}

*/		
		IntialNetworkInfluence();

//		Agent exp = (Agent)nuclearFamily[100].get(0);
//		System.out.println(exp.agentID);


		
		dataOutput = new DataOutput(this);
		schedule.scheduleRepeating(dataOutput,8,1);
	}
	
	public void readProvinceData(){

		try { 			
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/provinceData_6.csv"));
			reader.readLine();
			String line = null; 
			
			int i = 0;
			int actid;
			int spv;
			
			int sr95;
			int sr96;
			int sr97;
			int sr98;
			int sr99;
			int sr00;
			
			int su95;
			int su96;
			int su97;
			int su98;
			int su99;
			int su00;
			
			double fdi95;
			double fdi96;
			double fdi97;
			double fdi98;
			double fdi99;
			
			double land95;
			double land96;
			double land97;
			double land98;
			double land99;
			
			int rincpc95;
			int rincpc96;
			int rincpc97;
			int rincpc98;
			int rincpc99;
			
			int uincpc95;
			int uincpc96;
			int uincpc97;
			int uincpc98;
			int uincpc99;

			
			firm = new Firm[numProvinces];
	            
			while(i < numProvinces && (line=reader.readLine())!=null){ 						
				String item[] = line.split(",");
	                 
				actid = Integer.parseInt(item[0]);
				spv = Integer.parseInt(item[1]);
				
				sr95 = Integer.parseInt(item[2]);
				sr96 = Integer.parseInt(item[3]);
				sr97 = Integer.parseInt(item[4]);
				sr98 = Integer.parseInt(item[5]);
				sr99 = Integer.parseInt(item[6]);
				sr00 = Integer.parseInt(item[7]);
				
				su95 = Integer.parseInt(item[8]);
				su96 = Integer.parseInt(item[9]);
				su97 = Integer.parseInt(item[10]);
				su98 = Integer.parseInt(item[11]);
				su99 = Integer.parseInt(item[12]);
				su00 = Integer.parseInt(item[13]);
				
				fdi95 =  Double.parseDouble(item[14]);
				fdi96 =  Double.parseDouble(item[15]);
				fdi97 =  Double.parseDouble(item[16]);
				fdi98 =  Double.parseDouble(item[17]);
				fdi99 =  Double.parseDouble(item[18]);
				
				land95 = Double.parseDouble(item[19]);
				land96 = Double.parseDouble(item[20]);
				land97 = Double.parseDouble(item[21]);
				land98 = Double.parseDouble(item[22]);
				land99 = Double.parseDouble(item[23]);
				
				rincpc95 = Integer.parseInt(item[24]);
				rincpc96 = Integer.parseInt(item[25]);
				rincpc97 = Integer.parseInt(item[26]);
				rincpc98 = Integer.parseInt(item[27]);
				rincpc99 = Integer.parseInt(item[28]);
				
				uincpc95 = Integer.parseInt(item[29]);
				uincpc96 = Integer.parseInt(item[30]);
				uincpc97 = Integer.parseInt(item[31]);
				uincpc98 = Integer.parseInt(item[32]);
				uincpc99 = Integer.parseInt(item[33]);
				
				//System.out.println(uincpc99);
		
				firm[i] = new Firm(sr95, sr96, sr97, sr98, sr99, sr00,
						su95, su96, su97, su98, su99, su00,
						fdi95, fdi96, fdi97, fdi98, fdi99, 
						land95, land96, land97, land98, land99,
						rincpc95, rincpc96, rincpc97, rincpc98, rincpc99,
						uincpc95, uincpc96, uincpc97, uincpc98, uincpc99,
						actid, i, spv, this);
				
				yard.setObjectLocation(firm[i],
						new Double2D(yard.getWidth() * 0.3  + 10,
							yard.getHeight() * 0.1 + firm[i].firmID *2 - 0.5));
	
	             
				//System.out.println(Integer.parseInt(item[0]));//test
				
				schedule.scheduleRepeating(firm[i],2,1);
				
				i++;
			} 
		}

		catch (Exception e) { 
	        	e.printStackTrace(); 
		}	
		
	}
	
	public void readIndivialData(){
		try { 
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/indiv_1_sortbyprovince.csv"));
			reader.readLine();
			String line = null; 
			int i = 0;
			int ori = 0;
			int ag = 0;
			int edu = 0;
			int mal = 0;
			int mig = 0;
			int huk = 0;
			
			int oriindex;
			
			agent = new Agent[numAgents];
			
			while(i < numAgents && (line=reader.readLine())!=null){ 
				String item[] = line.split(",");               
		
				ori = Integer.parseInt(item[0]);
				ag  = Integer.parseInt(item[2]);
				edu = Integer.parseInt(item[3]);
				mal = Integer.parseInt(item[4]);
				mig = Integer.parseInt(item[5]);
				huk = Integer.parseInt(item[6]);
				if (huk > 21)
					huk--;
				huk--;
				
				oriindex = ori - 1 ;
				if (ori > 21)
					oriindex--;
			
				agent[i] = new Agent(firm[oriindex],ag,edu,mal,mig,huk,i,this);
				

				if (ag >= 16 && ag <= 44)
				{
					numAgentsOfLegalAge++;
					if (mig == 0){
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 0.5,
										yard.getHeight() * 0.1 + oriindex * 2 - 0.5));
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  + 4.5,
										yard.getHeight() * 0.1 + oriindex * 2 - 0.5));
						agent[i].employer = firm[huk];
						this.firm[huk].origins.add(agent[i]);
						this.firm[oriindex].employees.add(agent[i]);
						this.firm[oriindex].numEmployeesOrigins[huk]++;
					}
				}
				else{
					if (mig == 0){
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 3.5,
										yard.getHeight() * 0.1 + oriindex * 2 - 0.5));
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  + 4.5,
										yard.getHeight() * 0.1 + oriindex * 2 - 0.5));
						agent[i].employer = firm[huk];
						this.firm[huk].origins.add(agent[i]);
						this.firm[oriindex].employees.add(agent[i]);
						this.firm[oriindex].numEmployeesOrigins[huk]++;
					}
				}
			//	System.out.println(Integer.parseInt(item[1]));//test
			
	            
				schedule.scheduleRepeating(agent[i],4,1);
				network.addNode(agent[i]);
				i++;
			} 
		}	
		catch (Exception e) { 
	        	e.printStackTrace(); 
		}
	}
	
	
	public void setNuclearFamilyNetwork(){
		int i;
		int j;
		int k = 0;
		
		nuclearFamily = new Bag[numAgents/4+100];
		for (i = 0; i < numAgents-3; i++)
		{
			j = agent[i].originalProvince.firmID ;
			this.firm[j].sizeOfNuclearFamily++;

			nuclearFamily[k] = new Bag();
			if (agent[i+3].originalProvince.firmID == j)
			{
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				nuclearFamily[k].add(agent[i+3]);
				network.addEdge(agent[i], agent[i+1],new Double(-1));
				network.addEdge(agent[i], agent[i+2],new Double(-1));
				network.addEdge(agent[i], agent[i+3],new Double(-1));
				network.addEdge(agent[i+1], agent[i+2],new Double(-1));
				network.addEdge(agent[i+1], agent[i+3],new Double(-1));
				network.addEdge(agent[i+2], agent[i+3],new Double(-1));
				
				i = i + 3;
				k++;
			}
			else if (agent[i+2].originalProvince.firmID == j)
			{
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				network.addEdge(agent[i], agent[i+1],new Double(-1));
				network.addEdge(agent[i], agent[i+2],new Double(-1));
				network.addEdge(agent[i+1], agent[i+2],new Double(-1));
				i = i + 2;
				k++;
			}
			else if (agent[i+1].originalProvince.firmID == j)
			{
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				network.addEdge(agent[i], agent[i+1],new Double(-1));
				i = i + 1;
				k++;
			}	
			else
			{
				nuclearFamily[k].add(agent[i]);
				k++;
			}
		}
		
		sizeOfNuclearFamily = k;
		

		/*
		i = 0 ;
		j = 1 ;
		k = 0;
		
		
		for (i = 0; i < numAgents; i = i + j)
		{
			if (i + j < numAgents){
				for (j = 1; j < 4 ; j++){
					if (agent[i+j].originalProvince.firmID == agent[i].originalProvince.firmID){
						for (k = 0; k < j ; k++){						
							network.addEdge(agent[i+k], agent[i+j], new Double(-1));
						}
					}
				}
			}
		}
		
		*/
		/*		
		Agent ag;
		Agent otherAgent;
		Bag insideEdges;
		Edge edge;
		
		ag = agent[21004];
		
		
		insideEdges = this.insideNetwork.getEdgesIn(ag);
		for (i = 0 ; i < insideEdges.numObjs ; i ++){
			edge = (Edge) insideEdges.get(i);
			otherAgent = (Agent) edge.getOtherNode(ag);
			System.out.println(otherAgent.agentID);//test
		}
		
		*/
	}
	
	public void setVillageNetwork(){

		Agent agent1, agent2;
		int i,j,k, countNF = 0;
	/*	
		for (i = 0; i < this.numProvinces; i++)
		{
			for (j = countNF;j < countNF + this.firm[i].sizeOfNuclearFamily; j++)
			{
				agent1 = (Agent)nuclearFamily[j].get(random.nextInt(nuclearFamily[j].numObjs));
				if (j != this.firm[i].sizeOfNuclearFamily){
					agent2 = (Agent)nuclearFamily[j+1].get(random.nextInt(nuclearFamily[j+1].numObjs));
					network.addEdge(agent1, agent2, new Double(-1));
				}
				else{
					agent2 = (Agent)nuclearFamily[0].get(random.nextInt(nuclearFamily[0].numObjs));
					countNF = countNF + this.firm[i].sizeOfNuclearFamily;
					network.addEdge(agent1, agent2, new Double(-1));
					break;
				}				
			}
		}
	*/
		
		countNF = 1;
		agent2 = agent[0];
		for (i = 1; i < sizeOfNuclearFamily; i++)
		{
			agent1 = (Agent)nuclearFamily[i].get(random.nextInt(nuclearFamily[i].numObjs));
			
			if (i%100 != 0 && agent1.originalProvince.firmID == agent2.originalProvince.firmID){
				agent2 = (Agent)nuclearFamily[i-1].get(random.nextInt(nuclearFamily[i-1].numObjs));
				network.addEdge(agent1, agent2, new Double(-1));
				countNF++;
			}
			else {
				agent1 = (Agent)nuclearFamily[i-1].get(random.nextInt(nuclearFamily[i-1].numObjs));
				agent2 = (Agent)nuclearFamily[i-countNF].get(random.nextInt(nuclearFamily[i-countNF].numObjs));
				network.addEdge(agent1, agent2, new Double(-1));
				for (j = i-countNF; j < i  ; j ++)
					for (k = i-countNF; k < j; k++)
				{
					if (this.random.nextBoolean(4.0/countNF)){
						agent1 = (Agent)nuclearFamily[j].get(random.nextInt(nuclearFamily[j].numObjs));
						agent2 = (Agent)nuclearFamily[k].get(random.nextInt(nuclearFamily[k].numObjs));
						network.addEdge(agent1, agent2, new Double(-1));
					}
				}
				countNF = 1;
			}
			
		}
		
		
	}
	
	public void setProvinceNetwork(){
		int i,j,k;
		Agent agent1, agent2;
		
		for (i = 0; i < this.numProvinces; i++)
		{
			for (j = 0; j < this.firm[i].sizeOfNuclearFamily; j++)
			{
				for (k = 0; k < j ; k++)
				{
					if (this.random.nextBoolean(0.05/this.firm[i].sizeOfNuclearFamily)){
						agent1 = (Agent)nuclearFamily[j].get(random.nextInt(nuclearFamily[j].numObjs));
						agent2 = (Agent)nuclearFamily[k].get(random.nextInt(nuclearFamily[k].numObjs));
						network.addEdge(agent1, agent2, new Double(-1));
					}
				}
			}
		}
	}
	
	public void IntialNetworkInfluence(){
	
		Edge edge;
		Agent agentL1;
		Agent agentL2;
		Agent agentL3;
		Bag edgesL1;
		Bag edgesL2;
		Bag edgesL3;
		int i,j,k,m;
		
		for (m = 0; m < agent.length; m++) {
			if(agent[m].moved == 1){
				agent[m].pathLength = 0;
				edgesL1 = network.getEdgesIn(agent[m]);
				for (i =0 ; i < edgesL1.numObjs ; i++)
				{
					edge = (Edge) edgesL1.get(i);
					agentL1 = (Agent) edge.getOtherNode(agent[i]);
						if (agentL1.pathLength > 1 ){
							agentL1.pathLength = 1;
			//				System.out.println(agentL1.agentID);
							edgesL2 = network.getEdgesIn(agentL1);
							
							for (j =0 ; j < edgesL2.numObjs ; j++)
							{
								edge = (Edge) edgesL2.get(j);
								agentL2 = (Agent) edge.getOtherNode(agentL1);
								if (agentL2.pathLength > 2){
									agentL2.pathLength = 2;	
									edgesL3 = network.getEdgesIn(agentL2);
									
									for (k = 0 ; k < edgesL3.numObjs; k++)
									{
										edge = (Edge) edgesL3.get(k);
										agentL3 = (Agent) edge.getOtherNode(agentL2);
										if (agentL3.pathLength > 3)
											agentL3.pathLength = 3;										
									}				
									
								}
									
							}
						}																	
				}
				
			}
			
		}
		
	}
	
	
	
	
	public static void main(String[] args)
	{
		doLoop(CMModel.class, args);
		System.exit(0);
	}


}
