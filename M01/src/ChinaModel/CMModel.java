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
	
	public int numAgents = 313342;
	
	public int numAgentsOfLegalAge = 0;
	
	public int numProvinces = 31;
	
	public Network network = new Network(false);
	
	public Network networkByAge;
	
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
//		setNuclearFamilyNetwork();
//		setVillageNetwork();
//		setProvinceNetwork();
//		setMigrantNetwork();

	//	setNetworkByAge();
		
		//Set initial network influence
	//	IntialNetworkInfluence();
		
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
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/data/newdata/ProvinceWideData.csv"));
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
	                 
				actid = i + 1;
				spv = Integer.parseInt(item[0]);
				
				sr95 = Integer.parseInt(item[1]);
				sr96 = Integer.parseInt(item[2]);
				sr97 = Integer.parseInt(item[3]);
				sr98 = Integer.parseInt(item[4]);
				sr99 = Integer.parseInt(item[5]);
				sr00 = Integer.parseInt(item[6]);
				
				su95 = Integer.parseInt(item[7]);
				su96 = Integer.parseInt(item[8]);
				su97 = Integer.parseInt(item[9]);
				su98 = Integer.parseInt(item[10]);
				su99 = Integer.parseInt(item[11]);
				su00 = Integer.parseInt(item[12]);
				
				fdi95 =  1000;
				fdi96 =  1000;
				fdi97 =  1000;
				fdi98 =  1000;
				fdi99 =  1000;
				
				land95 = Double.parseDouble(item[18]);
				land96 = Double.parseDouble(item[19]);
				land97 = Double.parseDouble(item[20]);
				land98 = Double.parseDouble(item[21]);
				land99 = Double.parseDouble(item[22]);
				
				rincpc95 = Integer.parseInt(item[23]);				
				uincpc95 = Integer.parseInt(item[24]);
				
				//System.out.println(uincpc99);
		
				firm[i] = new Firm(sr95, sr96, sr97, sr98, sr99, sr00,
						su95, su96, su97, su98, su99, su00,
						fdi95, fdi96, fdi97, fdi98, fdi99, 
						land95, land96, land97, land98, land99,
						rincpc95, uincpc95, 
						actid, i, spv, this);
				//System.out.println(spv);
				
			//	yard.setObjectLocation(firm[i],
				//		new Double2D(yard.getWidth() * 0.3  + 10,
					//		yard.getHeight() * 0.1 + firm[i].firmID *2 - 0.5));
	
	             
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
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/newdata/InitialAgentDataAssigned.csv"));
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

				hid = Integer.parseInt(item[7]);
				pid = Integer.parseInt(item[0]);
				spv = Integer.parseInt(item[1]);
				spvhk = Integer.parseInt(item[2]);
				ag = Integer.parseInt(item[3]);
				edu = Integer.parseInt(item[5]);
				mal = Integer.parseInt(item[4]);
				mig = Integer.parseInt(item[6]);
				
				agent[i] = new Agent(firm[spvhk-1],ag,edu,mal,mig,spvhk,i,hid,this);
			
				this.firm[spvhk-1].agents.add(agent[i]);
				
				

				if (ag >= 16 && ag <= 44)
				{
					numAgentsOfLegalAge++;
					if (mig == 0){
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 8,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						this.firm[spvhk-1].numRuralAgents++;
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 3.5,
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
								new Double2D(yard.getWidth() * 0.3  - 8,
										yard.getHeight() * 0.1 + spvhk * 2 - 0.5));
						this.firm[spvhk-1].numRuralAgents++;
					}
					else{
						yard.setObjectLocation(agent[i],
								new Double2D(yard.getWidth() * 0.3  - 3.5,
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
		
	/*
		int i;
		
		for (i = 0; i < numAgents; i++){
			if (agent[i].householdID== 0){
			Agent agenthk;
			int rand;
			//System.out.println(i);
			rand = this.random.nextInt(firm[agent[i].hukou-1].agents.numObjs);
			agenthk = (Agent) this.firm[agent[i].hukou -1].agents.get(rand);
			//System.out.println(firm[agent[i].hukou -1].agents.numObjs);
			//System.out.println(agent[i].hukou -1);
			//System.out.println(this.random.nextInt(firm[agent[i].hukou-1].agents.numObjs));
			//System.out.println(agenthk.householdID);
			agent[i].householdID = agenthk.householdID;
			this.firm[agent[i].hukou-1].agents.add(agent[i]);
			}
		}
		*/
	}
	
	
	public void setNuclearFamilyNetwork(){
		
		
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
		Agent agent1, agent2, agenti;
		for (i = 0; i < numNuclearFamily; i++){
			agenti = (Agent) nuclearFamily[i].get(0);
			firm[agenti.hukou-1].nuclearFamily.add(nuclearFamily[i]);
			for (j = 0; j < nuclearFamily[i].numObjs; j++){
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
		Bag family1, family2, family3, family4;
		int i,j,k, countNuclearFamily = 0,m;
		
		countNuclearFamily = 1;
		int countAgent = 0;
		agent2 = agent[0];
		double prob;
		int villageSize;
		
		Bag villages[] = new Bag[2000];
		int countVillage = 0;
		int countV = 0;
		villages[0] = new Bag();
		
		family1 = (Bag)firm[0].nuclearFamily.get(0);
		agent1 = (Agent)family1.get(0);
		
		for (m = 0; m < numProvinces; m++){
			villageSize = readVillageSize(m);
			//System.out.println(firm[m].nuclearFamily.numObjs);
			for (i = 0; i < firm[m].nuclearFamily.numObjs; i++)
			{
				family2 = (Bag)firm[m].nuclearFamily.get(i);
				agent2 = (Agent)family2.get(0);
		//	System.out.println(villageSize);
				if (countAgent < villageSize && agent1.hukou == agent2.hukou){
					countAgent = countAgent + family2.numObjs;
					villages[countVillage].add(family2);
				}
				else{
					firm[agent2.hukou-1].village.add(villages[countVillage]);
					countVillage++;
					villages[countVillage] = new Bag();
					villageSize = readVillageSize(m);
					//System.out.println(countAgent);
					countAgent = 0;
					countV++;
				}
				family1 = family2;
				agent1 = agent2;
			}
		}
		System.out.println(countV);
		Bag village;
		Bag family;

		for (m = 0; m < numProvinces; m++){
			for (i = 0; i < firm[m].village.numObjs;i++){
				village = (Bag)firm[m].village.get(i);
				for (j = 0; j < village.numObjs; j++){
					for (k = 0; k < j; k++){
						prob = 5.0/village.numObjs;
						if( prob > 1)
							prob = 1;
						if (this.random.nextBoolean(prob)){
							//System.out.println(i);
							family1 = (Bag)village.get(j);
							agent1 = (Agent)family1.get(random.nextInt(family1.numObjs));
							family2 = (Bag)village.get(k);
							agent2 = (Agent)family2.get(random.nextInt(family2.numObjs));
							if (network.getEdge(agent1, agent2) == null)
								network.addEdge(agent1, agent2, new Double(2));
							if (this.random.nextBoolean(0.5))
							{
								agent3 = (Agent)family1.get(random.nextInt(family1.numObjs));
								agent4 = (Agent)family2.get(random.nextInt(family2.numObjs));
								if (network.getEdge(agent1, agent4) == null)
									network.addEdge(agent1, agent4, new Double(2));
								if (network.getEdge(agent2, agent3) == null)
									network.addEdge(agent2, agent3, new Double(2));
							}
						}
					}
				}	
			}
		}
		
			
		
	}
	
	public void setProvinceNetwork(){
		Agent agent1, agent2, agent3, agent4;
		Bag family1, family2, family3, family4;
		int i,j,k, countNuclearFamily = 0 , m,l;
		countNuclearFamily = 1;
		int countAgent = 1;
		agent2 = agent[0];
		double prob;
		Bag village1, village2;
		
		for (m = 0; m < numProvinces; m++){
			for (i = 0; i < firm[m].village.numObjs;i++){
				village1 = (Bag)firm[m].village.get(i);
				for (j = 0; j < i; j++){
					village2 = (Bag)firm[m].village.get(j);
					for (k = 0; k < village1.numObjs; k++)
						for(l = 0;l < village2.numObjs;l++){
							prob = 0.2/firm[m].nuclearFamily.numObjs;
							if( prob > 1)
								prob = 1;
							if (this.random.nextBoolean(prob)){
								//System.out.println(i);
								family1 = (Bag)village1.get(k);
								agent1 = (Agent)family1.get(random.nextInt(family1.numObjs));
								family2 = (Bag)village2.get(l);
								agent2 = (Agent)family2.get(random.nextInt(family2.numObjs));
								if (network.getEdge(agent1, agent2) == null)
									network.addEdge(agent1, agent2, new Double(3));
								if (this.random.nextBoolean(0.5))
								{
									agent3 = (Agent)family1.get(random.nextInt(family1.numObjs));
									agent4 = (Agent)family2.get(random.nextInt(family2.numObjs));
									if (network.getEdge(agent1, agent4) == null)
										network.addEdge(agent1, agent4, new Double(3));
									if (network.getEdge(agent2, agent3) == null)
										network.addEdge(agent2, agent3, new Double(3));
								}
							}
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
	
	public void setNetworkByAge(){
		networkByAge = new Network(network);
		int i;
		
		for (i = 0; i < numAgents; i++ ){
			if (agent[i].age < 16 || agent[i].age > 44)
				networkByAge.removeNode(agent[i]);
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
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/newdata/VillageSizeByProvince.csv"));
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
	
	public int[] getDegreeByAge() {
		int i, count, maxDegree = 0;
		Bag edges;
		int numAgents;
		numAgents = getNumAgents();
		int[] degree = new int[numAgents];
		for (i = 0; i < numAgents; i++)
		{
			edges = this.networkByAge.getEdgesIn(agent[i]);
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
	
	public int[] getDegreeofMovedByAge() {
		int i, count = 0, maxDegree = 0;
		Bag edges;
		
		int[] degree = new int[getNumMoved()];
		for (i = 0; i < numAgents; i++)
		{
			if (agent[i].moved == 1 && agent[i].age >= 16 && agent[i].age <= 44)
				{edges = this.networkByAge.getEdgesIn(agent[i]);
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
