package ChinaModel;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.field.network.*;

import java.io.*;

public class DataOutput implements Steppable{
	
	public Bag agents =  new Bag();
	
	public Bag firms = new Bag();
	
	BufferedWriter output;	
	BufferedWriter outputMovedByPro;	
	BufferedWriter outputClustering;	
	BufferedWriter outputDegreeDegree;	
	BufferedWriter outputMigrationMatrix;
	BufferedWriter outputNetworkStructure;
	
	//BufferedWriter outputAgents;
	BufferedWriter outputDegree;
	BufferedWriter outputParameterSpace;
	
	BufferedWriter outputPAA;
	BufferedWriter outputAgent;
	
	int filecount;
	
	DataOutput(CMModel model){
		super();
		try {
			filecount = 1;
			int i;
			String str = null;
			output = new BufferedWriter(new FileWriter("CMModelOutput"+str.valueOf(model.job())));
			outputMovedByPro = new BufferedWriter(new FileWriter("movedByPro"+str.valueOf(model.job())+".csv"));
			outputClustering = new BufferedWriter(new FileWriter("Clustering"+str.valueOf(model.job())+".csv"));
			outputDegreeDegree =  new BufferedWriter(new FileWriter("DegreeDegree"+str.valueOf(model.job())+".csv"));
			outputDegree = new BufferedWriter(new FileWriter("Degree"+str.valueOf(model.job())+".csv"));
			outputPAA = new BufferedWriter(new FileWriter("PAA"+str.valueOf(model.job())+".csv"));
			
			/*
			if (model.job() == 0)
				outputParameterSpace = new BufferedWriter(new FileWriter("ParameterSpace.csv"));
			else
				outputParameterSpace = new BufferedWriter(new FileWriter("ParameterSpace.csv", true));
			*/
			outputParameterSpace = new BufferedWriter(new FileWriter("ParameterSpace"+str.valueOf(model.job())+".csv"));
			/*
			outputAgents = new BufferedWriter(new FileWriter("Agents.csv"));
			for (i = 0 ; i < model.numAgents; i++){
				str = str.valueOf(model.agent[i].householdID);
				outputAgents.write(str);
				outputAgents.newLine();
				outputAgents.flush();
			}
			
			*/		
			str = "Step,TotalNumber,Age,Education,Province";
			output.write(str);
			output.newLine();
			output.flush();
			
			str = "movedByPro";
			outputMovedByPro.write(str+ ",");
			for (i = 0 ; i < model.numProvinces ; i++){
				str = str.valueOf(model.firm[i].spvID);
				outputMovedByPro.write(str + ",");
			}
			outputMovedByPro.newLine();
			outputMovedByPro.flush();
			
			str = "Clustering";
			outputClustering.write(str+ ",");
			for (i = 0 ; i < 999 ; i++){
				str = str.valueOf(i);
				outputClustering.write(str + ",");
			}
			outputClustering.newLine();
			outputClustering.flush();
			
			str = "DegreeDegree";
			outputDegreeDegree.write(str+ ",");
			for (i = 0 ; i < 999 ; i++){
				str = str.valueOf(i);
				outputDegreeDegree.write(str + ",");
			}
			outputDegreeDegree.newLine();
			outputDegreeDegree.flush();
			
			//outputParameterSpace.write("Job:");
			//outputParameterSpace.write(str.valueOf(model.job()));
			//outputParameterSpace.newLine();
			//outputParameterSpace.flush();
			str = str.valueOf(model.parameters.c2);
			str = str + "," + str.valueOf(model.parameters.c3);
			str = str + "," + str.valueOf(model.parameters.c4);
			str = str + "," + str.valueOf(model.parameters.Cpa);
			str = str + "," + str.valueOf(model.parameters.beta);
			str = str + "," + str.valueOf(model.parameters.theta);
			str = str + "," + str.valueOf(model.parameters.rou);
			str = str + "," + str.valueOf(model.parameters.omega);
			
			str = str + "," + str.valueOf(model.parameters.eta2);
			str = str + "," + str.valueOf(model.parameters.eta3);
			str = str + "," + str.valueOf(model.parameters.eta4);
			str = str + "," + str.valueOf(model.parameters.alpha);
			str = str + "," + str.valueOf(model.parameters.lamda);

			outputParameterSpace.write(str);
			outputParameterSpace.newLine();
			outputParameterSpace.flush();
			
			String strDD = null, strClustering = null, strD = null;
			int j;
			double[] c = model.getClustering();
			for (j = 0; j < 999; j++){
            	strClustering = strClustering.valueOf(c[j]);
            	strClustering = strClustering + ",";
            	
            	outputParameterSpace.write(strClustering);
			}

			
			outputParameterSpace.newLine();
			outputParameterSpace.flush();
			
			
			strDD = strDD.valueOf(model.schedule.getSteps());
    		strDD = strDD + ",";
    		
    		outputDegreeDegree.write(strDD);
    		double[] d = model.getDegreeDegree();
			for (j = 0; j < 999; j++){
            	strDD = strDD.valueOf(d[j]);
            	strDD = strDD + ",";
            	
            	outputDegreeDegree.write(strDD);
            	outputParameterSpace.write(strDD);
			}
			outputDegreeDegree.newLine(); 
			outputDegreeDegree.flush();
			outputParameterSpace.newLine();
			outputParameterSpace.flush();
			double[] d1 = new double[1000];
    		double[] d2 = new double[1000];
    		double[] d3 = new double[1000];
    		double[] d4 = new double[1000];
    		int index1 = 0, index2 = 0, index3 = 0, index4 = 0;
    		Bag edges;
    		Network network;
    		Edge edge;
    		
    		for (i = 0; i < model.numAgents; i++){
    			edges = model.network.getEdgesIn(model.agent[i]);
				for (j = 0; j < edges.numObjs; j++){
					if(edges.numObjs != 0){
						edge = (Edge)edges.get(j);
						if (edge.getWeight() < 1.1)
						{	
							index1++;
						}
						if (edge.getWeight() < 2.1)
						{	
							index2++;
						}
						if (edge.getWeight() < 3.1)
						{	
							index3++;
						}
						if (edge.getWeight() < 4.1){
							index4++;
						}
						}
					}
					d1[index1]++;
					d2[index2]++;
					d3[index3]++;
					d4[index4]++;
					index1 = 0;
					index2 = 0;
					index3 = 0;
					index4 = 0;
    		}
	
			
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d4[j]);
            	strD = strD + ",";
            	outputParameterSpace.write(strD);
			}
			outputParameterSpace.newLine();
			outputParameterSpace.flush();
			
			
			
			
			
		}
        catch(IOException e){
            System.out.println("Error"+e);
        }
		
	}
	
	public void step(SimState state){
		CMModel model = (CMModel) state;
		model.numMoved=model.getNumMoved();

        try{
        	//System.out.println(model.schedule.getSteps());
            agentData(model);
            firmData(model);
            
            int i, j, k;
            int age[] = new int[agents.numObjs]; 
            int edu[] = new int[agents.numObjs];
            int numgroup1 = 0,numgroup2 = 0,numgroup3=0;
            
            int sumOfEmployed = 0;      
            
        	Agent agent;

        	int numMovedByPro[] = new int[model.numProvinces];
        	
        	double agesum = 0.0;
        	double edusum = 0.0;
        	
            for(i = 0; i < agents.numObjs; i++)
            {
            	
            	agent = (Agent)agents.get(i);
            	
            	if (agent.employer != null && agent.age >= 16 && agent.age <= 44){
            		age[i] = agent.age;
            		edu[i] = agent.education;
            		sumOfEmployed++;
                	
                	agesum = agesum + age[i];
                	edusum = edusum + edu[i];
                	
                	numMovedByPro[agent.employer.firmID]++;
            	}

            	//System.out.println(agent.age);
            }
            
            double agemean = agesum / (double) sumOfEmployed;
            double edumean = edusum / (double) sumOfEmployed;
            
            Firm firm;
            int employees[] = new int[firms.numObjs];
            

            
            String strEmployed = null, s0 = null, s1 = null, s2 = null, s3 = null;
       
            s0 = s0.valueOf(model.schedule.getSteps()+1);
            s1 = s1.valueOf(sumOfEmployed);
            s2 = s2.valueOf(agemean);
            s3 = s3.valueOf(edumean);
            
            strEmployed = s0 + "," + s1 + "," + s2 + "," + s3 + ",";

            output.write(strEmployed);
            output.flush();
            output.newLine(); 
            output.flush();
 
     		
			
			String strMoved = null, strClustering = null, strDD = null;
	        			
			strMoved = strMoved.valueOf(model.numMoved);
    		strMoved = strMoved + ",";
    		
        	outputMovedByPro.write(strMoved);
    		
			for (j = 0; j < model.numProvinces; j++){
            	strMoved = strMoved.valueOf(numMovedByPro[j]);
            	strMoved = strMoved + ",";
            	
            	outputMovedByPro.write(strMoved);
			}
			outputMovedByPro.newLine(); 
			outputMovedByPro.flush();

			
			strClustering = strClustering.valueOf(model.schedule.getSteps());
    		strClustering = strClustering + ",";
    		
    		outputClustering.write(strClustering);
    		double[] c = model.getClustering();
			for (j = 0; j < 999; j++){
            	strClustering = strClustering.valueOf(c[j]);
            	strClustering = strClustering + ",";
            	
            	outputClustering.write(strClustering);
			}
			outputClustering.newLine(); 
			outputClustering.flush();
			
			
			strDD = strDD.valueOf(model.schedule.getSteps());
    		strDD = strDD + ",";
    		
    		outputDegreeDegree.write(strDD);
    		double[] d = model.getDegreeDegree();
			for (j = 0; j < 999; j++){
            	strDD = strDD.valueOf(d[j]);
            	strDD = strDD + ",";
            	
            	outputDegreeDegree.write(strDD);
			}
			outputDegreeDegree.newLine(); 
			outputDegreeDegree.flush();
			
			String strMMatrix = null, strMMatrix2;
			strMMatrix2 = "Total";
        	
			outputMigrationMatrix =  new BufferedWriter(new FileWriter("MigrationMatrix"+strMMatrix.valueOf(model.job())+".csv"));
			outputMigrationMatrix.write(strMMatrix2+ ",");
			for (i = 0 ; i < model.numProvinces; i++){
				strMMatrix2 = strMMatrix2.valueOf(model.firm[i].spvID);
				outputMigrationMatrix.write(strMMatrix2 + ",");
			}
			outputMigrationMatrix.newLine();
			outputMigrationMatrix.flush();
			
			String matrix[][] = new String[31][31]; 
			for (i = 0; i < model.numProvinces; i++){
				for(j = 0;j < model.numProvinces; j++){
					matrix[i][j] = strMMatrix.valueOf(model.firm[i].numEmployeesOrigins[j]);
					
				}
			}
			for (i = 0; i < model.numProvinces; i++){
				strMMatrix= strMMatrix.valueOf(model.firm[i].spvID);
				strMMatrix = strMMatrix + ",";
				outputMigrationMatrix.write(strMMatrix);
				for(j = 0;j < model.numProvinces; j++){
					strMMatrix = matrix[j][i];
					strMMatrix = strMMatrix + ",";
					outputMigrationMatrix.write(strMMatrix);		
				}
				outputMigrationMatrix.newLine(); 
				outputMigrationMatrix.flush();
			}
			
			
			Network network = model.network;
			Bag edges;
			Edge edge;
			Agent otherAgent;
			
			
			
			String strD = null;
			strD = strD.valueOf(model.schedule.getSteps())+ ",";
			

    		outputDegree.write(strD);
    		outputDegree.newLine();
    		
    		strD = "China";
			

    		outputDegree.write(strD);
    		outputDegree.newLine();
    		
    		for (i = 0 ; i < 999 ; i++){
				strD = strD.valueOf(i);
				outputDegree.write(strD + ",");
			}
			outputDegree.newLine();
			outputDegree.flush();
			
    		double[] d1 = new double[1000];
    		double[] d2 = new double[1000];
    		double[] d3 = new double[1000];
    		double[] d4 = new double[1000];
    		int index1 = 0, index2 = 0, index3 = 0, index4 = 0;

    		
    		for (i = 0; i < model.numAgents; i++){
    			edges = network.getEdgesIn(model.agent[i]);
				for (j = 0; j < edges.numObjs; j++){
					if(edges.numObjs != 0){
						edge = (Edge)edges.get(j);
						if (edge.getWeight() < 1.1)
						{	
							index1++;
						}
						if (edge.getWeight() < 2.1)
						{	
							index2++;
						}
						if (edge.getWeight() < 3.1)
						{	
							index3++;
						}
						if (edge.getWeight() < 4.1){
							index4++;
						}
						}
					}
					d1[index1]++;
					d2[index2]++;
					d3[index3]++;
					d4[index4]++;
					index1 = 0;
					index2 = 0;
					index3 = 0;
					index4 = 0;
    		}
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d1[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
            	
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d2[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d3[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d4[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			strD = "province";
			

    		outputDegree.write(strD);
    		outputDegree.newLine();
    		
    		for (i = 0 ; i < 999 ; i++){
				strD = strD.valueOf(i);
				outputDegree.write(strD + ",");
			}
			outputDegree.newLine();
			outputDegree.flush();
			
    		d1 = new double[1000];
    		d2 = new double[1000];
    		d3 = new double[1000];
    		d4 = new double[1000];
			index1 = 0;
			index2 = 0;
			index3 = 0;
			index4 = 0;
			agent = null;

    
			for (i = 0; i < model.firm[19].agents.numObjs; i++){
				agent = (Agent) model.firm[19].agents.get(i);
    			edges = network.getEdgesIn(agent);
				for (j = 0; j < edges.numObjs; j++){
					if(edges.numObjs != 0){
						edge = (Edge)edges.get(j);
						if ((Double)edge.getInfo()  < 1.1)
						{	
							index1++;
						}
						if ((Double)edge.getInfo()  < 2.1)
						{	
							index2++;
						}
						if ((Double)edge.getInfo()  < 3.1)
						{	
							index3++;
						}
						if ((Double)edge.getInfo() < 4.1){
							index4++;
						}
						}
					}
				d1[index1]++;
				d2[index2]++;
				d3[index3]++;
				d4[index4]++;
				index1 = 0;
				index2 = 0;
				index3 = 0;
				index4 = 0;
		}
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d1[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d2[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d3[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d4[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			
			strD = "village";
			

    		outputDegree.write(strD);
    		outputDegree.newLine();
    		
    		for (i = 0 ; i < 999 ; i++){
				strD = strD.valueOf(i);
				outputDegree.write(strD + ",");
			}
			outputDegree.newLine();
			outputDegree.flush();
			
			d1 = new double[1000];
    		d2 = new double[1000];
    		d3 = new double[1000];
    		d4 = new double[1000];
			index1 = 0;
			index2 = 0;
			index3 = 0;
			index4 = 0;
			agent = null;

    		Bag village = (Bag) model.firm[19].village.get(0);
    		for (i = 0; i < village.numObjs; i++){
    			Bag family = (Bag) village.get(i);
    			for (j = 0; j < family.numObjs; j++){
    			agent = (Agent) family.get(j);
    			edges = network.getEdgesIn(agent);
				for (k = 0; k < edges.numObjs; k++){
					if(edges.numObjs != 0){
						edge = (Edge)edges.get(k);
						if ((Double)edge.getInfo() < 1.1)
						{	
							index1++;
						}
						if ((Double)edge.getInfo() < 2.1)
						{	
							index2++;
						}
						if ((Double)edge.getInfo()  < 3.1)
						{	
							index3++;
						}
						if ((Double)edge.getInfo() < 4.1){
							index4++;
						}
						}
					}
				d1[index1]++;
				d2[index2]++;
				d3[index3]++;
				d4[index4]++;
				index1 = 0;
				index2 = 0;
				index3 = 0;
				index4 = 0;
		}}
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d1[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d2[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d3[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			for (j = 0; j < 999; j++){
            	strD = strD.valueOf(d4[j]);
            	strD = strD + ",";
            	outputDegree.write(strD);
			}
			outputDegree.newLine(); 
			outputDegree.flush();
			
			
			String strPAA;
			strPAA = "Province,m_Age,m_Education,m_Gender,m_Strong,m_Weak,Age,Education,Gender,Strong,Weak,migrants,total";
			
			double sumage1 = 0, sumage2 = 0, sumage3 = 0; //migrants, non-migrants, total
			double sumeducation1 = 0, sumeducation2 = 0, sumeducation3 = 0;
			double sumgender1=0 ,sumgender2 = 0, sumgender3 = 0;
			double strongtie, m_strongtie;
			double weaktie, m_weaktie;
			double totaldegree;
			
			
			if(model.schedule.getSteps() == 0 ||(model.schedule.getSteps()+1)%12 == 0){
			for (i = 0; i < model.numProvinces; i++){
				sumage1 = 0;
				sumage2 = 0;
				sumage3 = 0;
				sumeducation1 = 0;
				sumeducation2 = 0;
				sumeducation3 = 0;
				sumgender1 = 0;
				sumgender2 = 0;
				sumgender3 = 0;
				strongtie = 0;
				weaktie = 0;
				m_strongtie = 0;
				m_weaktie = 0;
				strPAA = null;
				for (j = 0;j < model.firm[i].agents.numObjs;j++){
					agent = (Agent)model.firm[i].agents.get(j);
					if(agent.age >= 16 && agent.age <= 44){
					sumage3 += agent.age;
					sumeducation3 += agent.education;
					sumgender3 += agent.male;
					edges = model.network.getEdgesIn(agent);
					for (k = 0;k<edges.numObjs;k++){
						edge = (Edge)edges.get(k);
						if (edge.getWeight() < 3.1)
							strongtie += 1;
						else
							weaktie += 1;
					}
					}
				}
				for (j = 0;j < model.firm[i].origins.numObjs;j++){
					agent = (Agent)model.firm[i].agents.get(j);
					if(agent.age >= 16 && agent.age <= 44){
					sumage1 += agent.age;
					sumeducation1 += agent.education;
					sumgender1 += agent.male;
					edges = model.network.getEdgesIn(agent);
					for (k = 0;k<edges.numObjs;k++){
						edge = (Edge)edges.get(k);
						if (edge.getWeight() < 3.1)
							m_strongtie += 1;
						else
							m_weaktie += 1;
					}
					}
				}
				strPAA = strPAA.valueOf(model.firm[i].spvID) + ',';
				strPAA = strPAA + strPAA.valueOf(sumage1/model.firm[i].origins.numObjs) + ',';
				strPAA = strPAA + strPAA.valueOf(sumeducation1/model.firm[i].origins.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(sumgender1/model.firm[i].origins.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(m_strongtie/model.firm[i].origins.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(m_weaktie/model.firm[i].origins.numObjs)+ ',';	
				
				strPAA = strPAA + strPAA.valueOf(sumage3/model.firm[i].agents.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(sumeducation3/model.firm[i].agents.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(sumgender3/model.firm[i].agents.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(strongtie/model.firm[i].agents.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(weaktie/model.firm[i].agents.numObjs)+ ',';
				
				strPAA = strPAA + strPAA.valueOf(model.firm[i].origins.numObjs)+ ',';
				strPAA = strPAA + strPAA.valueOf(model.firm[i].agents.numObjs)+ ',';
				outputPAA.write(strPAA);
				outputPAA.newLine(); 
				outputPAA.flush();
			}
			}
			
			
			if(model.schedule.getSteps() == 0 ||(model.schedule.getSteps()+1)%12 == 0){
				String str=null;
				//str = "ID, age, edu, male, group, hukou,householdID, employer,network 1,2,3,4";
				//System.out.println(filecount);
				
				outputAgent = new BufferedWriter(new FileWriter("Agents"+str.valueOf(model.job())+"time"+filecount+".csv"));
				for(i = 0; i < model.numAgents; i++){
					str = str.valueOf(model.agent[i].agentID+1)+ ',';
					str = str + str.valueOf(model.agent[i].age)+ ',';
					str = str + str.valueOf(model.agent[i].education)+ ',';
					str = str + str.valueOf(model.agent[i].male)+ ',';
					str = str + str.valueOf(model.agent[i].group)+ ',';
					str = str + str.valueOf(model.agent[i].hukou)+ ',';
					if (model.agent[i].moved == 1)
						str = str + str.valueOf(model.agent[i].employer.spvID)+ ',';
					else
						str = str + str.valueOf(0)+ ',';
					str = str + str.valueOf(model.agent[i].householdID)+ ',';
					
					edges = model.network.getEdgesIn(model.agent[i]);
					
					for (k = 0; k < edges.numObjs; k++){
						if(edges.numObjs != 0){
							edge = (Edge)edges.get(k);
			
							if ((Double)edge.getInfo() < 1.1)
							{	
								index1++;
							}
							else if ((Double)edge.getInfo()  < 2.1)
							{	
								index2++;
							}
							else if ((Double)edge.getInfo()  < 3.1)
							{	
								index3++;
							}
							else if ((Double)edge.getInfo()  < 4.1){
								index4++;
							}
							}
						}
					str = str + str.valueOf(index1)+ ',';
					str = str + str.valueOf(index2)+ ',';
					str = str + str.valueOf(index3)+ ',';
					str = str + str.valueOf(index4);
					index1 = 0;
					index2 = 0;
					index3 = 0;
					index4 = 0;
					
					outputAgent.write(str);
					outputAgent.newLine();
					outputAgent.flush();				
					
				}
				
				outputAgent.close();
				
				outputNetworkStructure = new BufferedWriter(new FileWriter("NetworkStucture"+str.valueOf(model.job())+"time"+filecount+".csv"));
				String strNetwork;
				
				for (i = 0; i < model.numAgents; i++){
					outputNetworkStructure.write(model.agent[i].agentID+1+",");
					edges = network.getEdgesIn(model.agent[i]);
					for (j=0; j < edges.numObjs; j++){
						edge = (Edge)edges.get(j);
						otherAgent = (Agent) edge.getOtherNode(model.agent[i]);
						outputNetworkStructure.write(otherAgent.agentID+1+",");
					}
					outputNetworkStructure.newLine();
					outputNetworkStructure.flush();
				}
				
				outputNetworkStructure.close();
				
				
				filecount = filecount + 1;
			}

        }
        catch(IOException e){
            System.out.println("Error"+e);
        }

		
	}
	
	public void agentData(CMModel model){
		agents.clear();
		agents.addAll(model.agent);
	}
	
	public void firmData(CMModel model){
		firms.clear();
		firms.addAll(model.firm);	
	}
	

}
