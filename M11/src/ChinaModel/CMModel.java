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
	
	public int numAgents = 3133;
	
	public int numAgentsOfLegalAge = 0;
	
	public int numProvinces = 31;
	
	public Network insideNetwork = new Network(false);
	
		
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
		
		double test = this.random.nextDouble(true,true);
		
		try { 			
			BufferedReader reader = new BufferedReader(new FileReader("/Users/fzh/Desktop/CM/Data/provinceData_6.csv"));
			reader.readLine();
			String line = null; 
			
			int i = 0;
			int actid;
			int id;
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
		
		
		firmList = new FirmList(firm);
		schedule.scheduleRepeating(firmList,3,1);
		
				
		
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
				insideNetwork.addNode(agent[i]);
				i++;
			} 
		}	
		catch (Exception e) { 
	        	e.printStackTrace(); 
		}
		
		int i = 0 ;
		int j = 1 ;
		int k = 0;
				
		
		for (i = 0; i < numAgents; i = i + j)
		{
			if (i + j < numAgents){
				for (j = 1; j < 4 ; j++){
					if (agent[i+j].originalProvince.firmID == agent[i].originalProvince.firmID){
						for (k = 0; k < j ; k++){						
							insideNetwork.addEdge(agent[i+k], agent[i+j], new Double(-1));
						}
					}
				}
			}
		}
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

		
		dataOutput = new DataOutput(this);
		schedule.scheduleRepeating(dataOutput,8,1);
	}
	
	public static void main(String[] args)
	{
		doLoop(CMModel.class, args);
		System.exit(0);
	}


}
