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
	
//	public FirmList firmList;
	
	public DataOutput dataOutput;
	
	public Continuous2D yard = new Continuous2D(1.0,100,100);
	
	public int numAgents = 313355;
	
	public int numAgentsOfLegalAge = 0;
	
	public int numProvinces = 31;
	
	public Network network = new Network(false);
	
	public Bag[] nuclearFamily;
	
	public int numNuclearFamily = 0;
	
	public int numMoved = 0;
	
	public void setNumAgents(int val) { if (val > 0) numAgents = val; }
		
	public CMModel(long seed) {		
		super(seed);
	}
	
	public void start() {		
		super.start();
		
		//Set random seed
		this.setSeed(10000);	
		random.setSeed(10000);
		
		//Set date
		date = new Date();
		schedule.scheduleRepeating(date,1,1);

		//Clear the display yeard
		yard.clear();			

		//Read data
		readProvinceData();		
		readIndividualData();
		
		//Setup Network
		setNuclearFamilyNetwork();
		setVillageNetwork();
		setProvinceNetwork();
		setMigrantNetwork();
		
		//Set initial network influence
		IntialNetworkInfluence();
		
		/*
		Test the village number
		int totalVillage = 0;
		for (int i = 0;i <31; i++)
			totalVillage+= firm[i].numVillage;
		System.out.println(totalVillage);
		*/

		//Setup the output
		dataOutput = new DataOutput(this);
		schedule.scheduleRepeating(dataOutput,8,1);
	}
	
	public void readProvinceData(){

		try { 			
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/provinceData_7.csv"));
			reader.readLine();
			String line = null; 
			
			int i = 0, actid, spv;
			int sr95, sr96, sr97, sr98, sr99, sr00;
			int su95, su96, su97, su98, su99, su00;	
			double fdi95, fdi96, fdi97, fdi98, fdi99;
			double land95, land96, land97, land98, land99;
			int rincpc95, rincpc96,  rincpc97, rincpc98, rincpc99;
			int uincpc95, uincpc96, uincpc97, uincpc98, uincpc99;
			
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
				//System.out.println(spv);
				
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
	
	public void readIndividualData(){
		try { 
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/InitialAgentData2.csv"));
			reader.readLine();
			String line = null; 
			int i = 0;
			int hid = 0;
			int pid = 0;
			int spv = 0;
			int spvhk = 0;
			int ag = 0;
			int edu = 0;
			int mal = 0;
			int mig = 0;
			
			agent = new Agent[numAgents];
			
			while(i < numAgents && (line=reader.readLine())!=null){ 
				String item[] = line.split(",");               
		
				hid = Integer.parseInt(item[0]);
				pid = Integer.parseInt(item[1]);
				spv = Integer.parseInt(item[2]);
				spvhk = Integer.parseInt(item[3]);
				ag = Integer.parseInt(item[4]);
				edu = Integer.parseInt(item[5]);
				mal = Integer.parseInt(item[6]);
				mig = Integer.parseInt(item[7]);
				
				agent[i] = new Agent(firm[spvhk-1],ag,edu,mal,mig,spvhk,i,hid,this);
				

				if (ag >= 16 && ag <= 44)
				{
					numAgentsOfLegalAge++;
					if (mig == 0){
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 0.5,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						this.firm[spvhk-1].numRuralAgents++;
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 8,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						agent[i].employer = firm[spv-1];
						this.firm[spvhk-1].origins.add(agent[i]);
						this.firm[spv-1].employees.add(agent[i]);
						this.firm[spv-1].numEmployeesOrigins[spvhk-1]++;
					}
				}
				else{
					if (mig == 0){
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 3.5,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						this.firm[spvhk-1].numRuralAgents++;
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 8,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						agent[i].employer = firm[spv-1];
						this.firm[spvhk-1].origins.add(agent[i]);
						this.firm[spv-1].employees.add(agent[i]);
						this.firm[spv-1].numEmployeesOrigins[spvhk-1]++;
					}
				}
			//	System.out.println(Integer.parseInt(item[1]));//test
	            
				schedule.scheduleRepeating(agent[i],4,1);
				
				i++;
			} 
		}
		catch (Exception e) { 
	        	e.printStackTrace(); 
		}
	}
	
	
	public void setNuclearFamilyNetwork(){
		
		/*
		nuclearFamily = new Bag[numAgents];
		int i;
		int j;
		int k = 0;
		int m, n;
		int rand;
		int familySize;
		
		int sum = 66482 + 74091 + 18071 + 6561 + 1817 + 1134 ;
			
		for (i = 0; i < numAgents; i++)
		{
			j = agent[i].originalProvince.firmID ;
			this.firm[j].numNuclearFamily++;

			nuclearFamily[k] = new Bag();
			rand = this.random.nextInt(sum);
			
			if (rand - 1134 < 0  && i + 5 < numAgents && agent[i+5].originalProvince.firmID == j)
				{
				familySize = 6;
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				nuclearFamily[k].add(agent[i+3]);
				nuclearFamily[k].add(agent[i+4]);
				nuclearFamily[k].add(agent[i+5]);
				network.addEdge(agent[i], agent[i+1],new Double(1));
				network.addEdge(agent[i], agent[i+2],new Double(1));
				network.addEdge(agent[i], agent[i+3],new Double(1));
				network.addEdge(agent[i], agent[i+4],new Double(1));
				network.addEdge(agent[i], agent[i+5],new Double(1));
				network.addEdge(agent[i+1], agent[i+2],new Double(1));
				network.addEdge(agent[i+1], agent[i+3],new Double(1));
				network.addEdge(agent[i+1], agent[i+4],new Double(1));
				network.addEdge(agent[i+1], agent[i+5],new Double(1));
				network.addEdge(agent[i+2], agent[i+3],new Double(1));
				network.addEdge(agent[i+2], agent[i+4],new Double(1));
				network.addEdge(agent[i+2], agent[i+5],new Double(1));
				network.addEdge(agent[i+3], agent[i+4],new Double(1));
				network.addEdge(agent[i+3], agent[i+5],new Double(1));
				network.addEdge(agent[i+4], agent[i+5],new Double(1));
				
				i = i + familySize - 1;
				k++;
				}
			else if (rand - 1134 -1817 < 0 && i + 4 < numAgents && agent[i+4].originalProvince.firmID == j)
				{
				familySize = 5;
				familySize = 6;
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				nuclearFamily[k].add(agent[i+3]);
				nuclearFamily[k].add(agent[i+4]);
				network.addEdge(agent[i], agent[i+1],new Double(1));
				network.addEdge(agent[i], agent[i+2],new Double(1));
				network.addEdge(agent[i], agent[i+3],new Double(1));
				network.addEdge(agent[i], agent[i+4],new Double(1));
				network.addEdge(agent[i+1], agent[i+2],new Double(1));
				network.addEdge(agent[i+1], agent[i+3],new Double(1));
				network.addEdge(agent[i+1], agent[i+4],new Double(1));
				network.addEdge(agent[i+2], agent[i+3],new Double(1));
				network.addEdge(agent[i+2], agent[i+4],new Double(1));
				network.addEdge(agent[i+3], agent[i+4],new Double(1));
				
				i = i + familySize - 1;
				k++;
				}
			else if (rand - 1134 -1817 -6561< 0 && i + 3 < numAgents && agent[i+3].originalProvince.firmID == j)
				{
				familySize = 4;
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				nuclearFamily[k].add(agent[i+3]);
				network.addEdge(agent[i], agent[i+1],new Double(1));
				network.addEdge(agent[i], agent[i+2],new Double(1));
				network.addEdge(agent[i], agent[i+3],new Double(1));
				network.addEdge(agent[i+1], agent[i+2],new Double(1));
				network.addEdge(agent[i+1], agent[i+3],new Double(1));
				network.addEdge(agent[i+2], agent[i+3],new Double(1));
				
				i = i + familySize -1 ;
				k++;
				}
			else if (rand - 1134 -1817 -6561 -18071 < 0 && i + 2 < numAgents && agent[i+2].originalProvince.firmID == j)
				{
				familySize = 3;
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				nuclearFamily[k].add(agent[i+2]);
				network.addEdge(agent[i], agent[i+1],new Double(1));
				network.addEdge(agent[i], agent[i+2],new Double(1));
				network.addEdge(agent[i+1], agent[i+2],new Double(1));
				i = i + familySize -1;
				k++;
				}
			else if (rand - 1134 -1817 -6561 -18071 -74091 < 0 && i + 1 < numAgents && agent[i+1].originalProvince.firmID == j)
				{
				familySize = 2;
				nuclearFamily[k].add(agent[i]);
				nuclearFamily[k].add(agent[i+1]);
				network.addEdge(agent[i], agent[i+1],new Double(1));
				i = i + familySize -1;
				k++;
				}
			else 
				{
				familySize = 1;
				nuclearFamily[k].add(agent[i]);
				
				k++;
				}
		}
		
		numNuclearFamily = k;
		*/
		
		nuclearFamily = new Bag[numAgents];
		int i;
		int j;
		int k = 0;
		int m, n;

		int familySize = 0;
		int hidindex = agent[0].householdID;
		nuclearFamily[k] = new Bag();	
		
		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].householdID == hidindex)
			{
				nuclearFamily[k].add(agent[i]);
			}
			else
			{
				k++;
				nuclearFamily[k] = new Bag();	
				nuclearFamily[k].add(agent[i]);
				hidindex= agent[i].householdID;
			}	
		}
		numNuclearFamily = k;
		Agent agent1, agent2;
		for (i = 0; i < numNuclearFamily; i++){
			for (j = 0; j < nuclearFamily[i].numObjs;j++){
				for(k = 0;k < j; k++){
					agent1 = (Agent) nuclearFamily[i].get(j);
					agent2 = (Agent) nuclearFamily[i].get(k);
					if (network.getEdge(agent1, agent2) == null)
						network.addEdge(agent1, agent2, new Double(1));
				}
			}
		}
		

	}
	
	public void setVillageNetwork(){

		Agent agent1, agent2, agent3, agent4;
		Edge edge3,edge4;
		int i,j,k, countNuclearFamily = 0;
		int m,n;
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
		
		countNuclearFamily = 1;
		int countAgent = 1;
		agent2 = agent[0];
		double prob;
		int villageSize;
		for (i = 1; i < numNuclearFamily; i++)
		{
			agent1 = (Agent)nuclearFamily[i].get(random.nextInt(nuclearFamily[i].numObjs));
			agent2 = (Agent)nuclearFamily[i-1].get(random.nextInt(nuclearFamily[i-1].numObjs));
			villageSize = readVillageSize(agent2.originalProvince.firmID);
			
		//	System.out.println(villageSize);
			
			if (countAgent < villageSize && agent1.originalProvince.firmID == agent2.originalProvince.firmID){
				//agent2 = (Agent)nuclearFamily[i-1].get(random.nextInt(nuclearFamily[i-1].numObjs));
				//network.addEdge(agent1, agent2, new Double(-1));
				countAgent = countAgent + 1;
				countNuclearFamily++;
			}
			else {
				//agent1 = (Agent)nuclearFamily[i-1].get(random.nextInt(nuclearFamily[i-1].numObjs));
				//agent2 = (Agent)nuclearFamily[i-countNF].get(random.nextInt(nuclearFamily[i-countNF].numObjs));
				//network.addEdge(agent1, agent2, new Double(-1));
				firm[agent2.originalProvince.firmID].numVillage++;
				
				for (j = i-countNuclearFamily; j < i  ; j ++)
					for (k = i-countNuclearFamily; k < j; k++)
				{
					prob = 6.0/countNuclearFamily;
					if( prob > 1)
						prob = 1;
					
					if (this.random.nextBoolean(prob)){
						agent1 = (Agent)nuclearFamily[j].get(random.nextInt(nuclearFamily[j].numObjs));
						agent2 = (Agent)nuclearFamily[k].get(random.nextInt(nuclearFamily[k].numObjs));
						if (network.getEdge(agent1, agent2) == null)
							network.addEdge(agent1, agent2, new Double(2));
						for (m = 0;m < nuclearFamily[j].numObjs; m++){
							for(n = 0;n < nuclearFamily[k].numObjs; n++){
						if (this.random.nextBoolean(0.99))
						{
							agent3 = (Agent)nuclearFamily[j].get(m);
							agent4 = (Agent)nuclearFamily[k].get(n);
							if (network.getEdge(agent1, agent4) == null)
								network.addEdge(agent1, agent4, new Double(2));
							if (network.getEdge(agent2, agent3) == null)
								network.addEdge(agent2, agent3, new Double(2));
						}
						}}
					}
				}
				
				countNuclearFamily = 1;
				countAgent = 1;
			}
			
		}
		
		
	}
	
	public void setProvinceNetwork(){
		int i,j,k,m,n;
		Agent agent1, agent2, agent3, agent4;
		
		for (i = 0; i < this.numProvinces; i++)
		{
			for (j = 0; j < this.firm[i].numNuclearFamily; j++)
			{
				for (k = 0; k < j ; k++)
				{
					
					if (this.random.nextBoolean(0.3/this.firm[i].numNuclearFamily) ){
						agent1 = (Agent)nuclearFamily[j].get(random.nextInt(nuclearFamily[j].numObjs));
						agent2 = (Agent)nuclearFamily[k].get(random.nextInt(nuclearFamily[k].numObjs));
						if (network.getEdge(agent1, agent2) == null)
							network.addEdge(agent1, agent2, new Double(3));
						for (m = 0;m < nuclearFamily[j].numObjs; m++){
							for(n = 0;n < nuclearFamily[k].numObjs; n++){
						if (this.random.nextBoolean(0.99))
						{
							agent3 = (Agent)nuclearFamily[j].get(m);
							agent4 = (Agent)nuclearFamily[k].get(n);
							if (network.getEdge(agent1, agent4) == null)
								network.addEdge(agent1, agent4, new Double(3));
							if (network.getEdge(agent2, agent3) == null)
								network.addEdge(agent2, agent3, new Double(3));
						}
						}}
					}
				}
			}
		}
	}
	
	public void setMigrantNetwork(){
		int i,j,k;
		Agent agent1, agent2, agent3, agent4;
		
		for (i = 0; i < this.numProvinces; i++)
		{
			for (j = 0; j < this.firm[i].employees.numObjs; j++)
			{
				for (k = 0; k < j ; k++){
					if(random.nextBoolean(0.01)){
						agent1 = (Agent) this.firm[i].employees.get(j);
						agent2 = (Agent) this.firm[i].employees.get(k);
						if (network.getEdge(agent1, agent2) == null)
							network.addEdge(agent1, agent2, new Double(4));
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
	
	public int readVillageSize(int provinceIndex){
		double villageSize = 0 ,temp ;
		double i = 1.0;
		try { 
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/vsize.csv"));
			reader.readLine();
			String line = null; 
	            
			int spv = this.firm[provinceIndex].spvID;
			int spvID;
			
			while((line=reader.readLine())!=null){ 						
				String item[] = line.split(",");	                 
				spvID = Integer.parseInt(item[0]);
				temp = Integer.parseInt(item[1]);
				if (spvID == spv && this.random.nextBoolean(1/i))
				{
					villageSize = temp;
					i = i+1;
				}				
			}
			
			reader.close();
		
	}	
	catch (Exception e) { 
        	e.printStackTrace(); 
	}
		
		villageSize = villageSize;
		
		return (int)villageSize;
	}
	
	//Functions get**: inspectors in UI
	public int getNumAgents() { return numAgents; }
	
	public int getNumProvinces() { return numProvinces; }
	
	public int[] getFamilySize(){
		int[] familySize = new int[numNuclearFamily];
		int i;
		for (i = 0; i < numNuclearFamily; i++){
			familySize[i]=nuclearFamily[i].numObjs;
		}
		return familySize;
	}
	
	public int[] getDegree() {
		int i, count, maxDegree = 0;
		Bag edges;
/*		for (i = 0; i < getNumAgents(); i++)
		{
			edges = this.network.getEdgesIn(agent[i]);
			if (maxDegree < edges.numObjs)
				maxDegree = edges.numObjs;
		}
		System.out.println(maxDegree);
		int[] degree = new int[maxDegree+1];
		for (i = 0; i <= maxDegree; i++)
			degree[i] = 0;
		
		for (i = 0; i < getNumAgents(); i++)
		{
			edges = this.network.getEdgesIn(agent[i]);
			degree[edges.numObjs] = degree[edges.numObjs] + 1;
		}
*/
		int numAgents;
		numAgents = getNumAgents();
		int[] degree = new int[numAgents];
		for (i = 0; i < numAgents; i++)
		{
			edges = this.network.getEdgesIn(agent[i]);
			degree[i] = edges.numObjs;
			
		}
		    return degree;
	}
	
	public double getMeanDegree() {
		int i, count, maxDegree = 0;
		Bag edges;
		
		double mean,sum = 0;
		
		int numAgents;
		numAgents = getNumAgents();
		int[] degree = new int[numAgents];
		for (i = 0; i < numAgents; i++)
		{
			edges = this.network.getEdgesIn(agent[i]);
			degree[i] = edges.numObjs;
			sum = sum + degree[i];
			
		}
		mean = sum/numAgents;
		    return mean;
	}
	
	public double getMeanDegreeOfMig() {
		int i;
		Bag edges;
		
		double mean,sum = 0;
	
		int count = 0;
		
		int[] degree = new int[getNumMoved()];
		for (i = 0; i < numAgents; i++)
		{
			if(agent[i].moved == 1 && agent[i].age >= 16 && agent[i].age <= 44){
				edges = this.network.getEdgesIn(agent[i]);
				degree[count] = edges.numObjs;
				sum = sum + degree[count];
			}		
		}
		mean = sum/getNumMoved();
		    return mean;
	}
	
	public int[] getDegreeofMoved() {
		int i, count = 0, maxDegree = 0;
		Bag edges;
		
		int[] degree = new int[getNumMoved()];
		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].moved == 1 && agent[i].age >= 16 && agent[i].age <= 44)
				{edges = this.network.getEdgesIn(agent[i]);
				degree[count] = edges.numObjs;
				count++;}
			
		}
		    return degree;
	}
	
	public int[] getPath() {
		int i, count = 0, maxPath = 0, num = 0;
		Bag edges;
		

		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].pathLength < 3 && agent[i].pathLength >0)
				num++;			
		}
		int[] path = new int[num];
		
		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].pathLength < 3 && agent[i].pathLength >0)
			{
				path[count] = agent[i].pathLength;
				count++;
			}
		}
	
		    return path;
	}
	
	public int getNumMoved(){
		int num = 0, i;
		for (i = 0; i < numAgents; i++){
			if (agent[i].moved == 1 && agent[i].age >= 16 && agent[i].age <= 44)
				num++;		
		}
		return num;
	}
	
	public int[] getProvinceTotalAgents(){
		int numAgents, i;
		numAgents = getNumAgents();
		int[] originProvinces = new int[numAgents];
		for (i = 0; i < numAgents; i++)
		{
			originProvinces[i] = agent[i].originalProvince.spvID;		
		}
		    return originProvinces;
	}
	public int[] getProvinceMovedAgents(){
		int numMovedAgents, i, j = 0;
		numMovedAgents = getNumMoved();
		int[] toProvinces = new int[numMovedAgents];
		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].moved == 1 && agent[i].employer != null && agent[i].age >= 16 && agent[i].age <= 44){
				toProvinces[j] = agent[i].employer.spvID;
				j++;
			}
				
		}
	
		    return toProvinces;
	}
	
	public int[] getToGD(){
		int i;
		int[] employeeOriGD = new int[firm[5].employees.numObjs];
		Agent agent;
		for (i = 0; i < firm[5].employees.numObjs; i ++)
		{
			agent = (Agent)firm[5].employees.get(i);
			employeeOriGD[i] = agent.originalProvince.spvID;
		}		
		return employeeOriGD;
	}

	public double[] getFDI(){
		double fdi[];
		int i;
		fdi = new double[32];
		for (i = 0; i < 31; i++){
			fdi[firm[i].spvID] = firm[i].currentFdi;
		}
		
		return fdi;
	}
	
	public int[] getNumVillage(){
		int numVillage[];
		int i;
		numVillage= new int[32];
		for (i = 0; i < 31; i++){
			numVillage[firm[i].spvID] = firm[i].numVillage;
		}
		
		return numVillage;
	}
	
	public double[] getClustering(){
		int num = 0, i,j,k,m;
		Agent n1,n2;
		Bag edges;
		Edge edge1,edge2;
		double[] c = new double[50];
		int[] E = new int[50];
		int[] count = new int[50];
		for (i = 0; i < numAgents; i++){
			edges = network.getEdgesIn( agent[i]);
			for (m=2;m<c.length;m++){
			if (edges.numObjs == m){
				count[m]++;
				for (j=0;j<edges.numObjs;j++)
					for(k=0;k<j;k++){
						edge1 = (Edge) edges.get(j);
						n1=(Agent)edge1.getOtherNode(agent[i]);
						edge2 = (Edge) edges.get(k);
						n2=(Agent)edge2.getOtherNode(agent[i]);
						if (network.getEdge(n1, n2)!=null)
							E[m]++;
					}
				
				
			}
			}
			
		}
		for (m=2;m<c.length;m++)
			c[m] = 2.0* E[m]/(double)m/((double)m+1.0) / (double)count[m];
		
		return c;
	}
	
	public double[] getDegreeDegree(){
		int num = 0, i,j;
		int m;
		double[] c = new double[50];
		int[] deg = new int[50];
		int[] count = new int[50];
		Bag edges;
		Edge edge;
		Agent agent;
		for (i = 0; i < numAgents; i++){
			edges = network.getEdgesIn(this.agent[i]);
			for (m = 0; m < c.length; m++){
				if (edges.numObjs == m){
					count[m] += m;
					for(j=0;j<edges.numObjs;j++){
						edge = (Edge) edges.get(j);
						agent = (Agent) edge.getOtherNode(this.agent[i]);		
						deg[m] += network.getEdgesIn(agent).numObjs;
					}
				}	
			}
		}
		for (m=0;m<c.length;m++)
			c[m] = (double)deg[m] / (double)count[m];
		return c;
	}
	
	public static void main(String[] args)
	{
		doLoop(CMModel.class, args);
		System.exit(0);
	}


}
