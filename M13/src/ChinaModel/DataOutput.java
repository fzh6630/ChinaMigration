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
	BufferedWriter outputDesGD;	
	BufferedWriter outputDesJS;	
	BufferedWriter outputOriHN;	
	BufferedWriter outputOriSC;	
	BufferedWriter outputMovedByPro;	
	BufferedWriter outputClustering;	
	BufferedWriter outputDegreeDegree;	
	BufferedWriter outputMigrationMatrix;
	BufferedWriter outputNetworkStructure;
	BufferedWriter outputDegree;
	
	DataOutput(CMModel model){
		super();
		try {
			int i;
			output = new BufferedWriter(new FileWriter("CMModelOutput"));
			outputDesGD = new BufferedWriter(new FileWriter("m0_d_gd.csv"));
			outputDesJS = new BufferedWriter(new FileWriter("m0_d_js.csv"));
			outputOriHN = new BufferedWriter(new FileWriter("m0_o_hn.csv"));
			outputOriSC = new BufferedWriter(new FileWriter("m0_o_sc.csv"));
			outputMovedByPro = new BufferedWriter(new FileWriter("movedByPro.csv"));
			outputClustering = new BufferedWriter(new FileWriter("Clustering.csv"));
			outputDegreeDegree =  new BufferedWriter(new FileWriter("DegreeDegree.csv"));
			outputDegree =  new BufferedWriter(new FileWriter("Degree.csv"));
			
			String str;
			str = "Step,TotalNumber,Age,Education,Province";
			output.write(str);
			output.newLine();
			output.flush();
			
			str = "outputDesGD";
			outputDesGD.write(str + ",");
			for (i = 0 ; i < model.numProvinces ; i++){
				str = str.valueOf(model.firm[i].spvID);
				outputDesGD.write(str + ",");
			}
			outputDesGD.newLine();
			outputDesGD.flush();
			
			str = "outputDesJS";
			outputDesJS.write(str + ",");
			for (i = 0 ; i < model.numProvinces ; i++){
				str = str.valueOf(model.firm[i].spvID);
				outputDesJS.write(str + ",");
			}
			outputDesJS.newLine();
			outputDesJS.flush();
			
			str = "outputOriHN";
			outputOriHN.write(str+ ",");
			for (i = 0 ; i < model.numProvinces ; i++){
				str = str.valueOf(model.firm[i].spvID);
				outputOriHN.write(str + ",");
			}
			outputOriHN.newLine();
			outputOriHN.flush();
			
			str = "outputOriSC";
			outputOriSC.write(str+ ",");
			for (i = 0 ; i < model.numProvinces ; i++){
				str = str.valueOf(model.firm[i].spvID);
				outputOriSC.write(str + ",");
			}
			outputOriSC.newLine();
			outputOriSC.flush();
			
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
			for (i = 0 ; i < 50 ; i++){
				str = str.valueOf(i);
				outputClustering.write(str + ",");
			}
			outputClustering.newLine();
			outputClustering.flush();
			
			str = "DegreeDegree";
			outputDegreeDegree.write(str+ ",");
			for (i = 0 ; i < 50 ; i++){
				str = str.valueOf(i);
				outputDegreeDegree.write(str + ",");
			}
			outputDegreeDegree.newLine();
			outputDegreeDegree.flush();
			
			str = "Degree";
			outputDegree.write(str+ ",");
			for (i = 0 ; i < 50 ; i++){
				str = str.valueOf(i);
				outputDegree.write(str + ",");
			}
			outputDegree.newLine();
			outputDegree.flush();
			
			
			
			
		}
        catch(IOException e){
            System.out.println("Error"+e);
        }
		
	}
	
	public void step(SimState state){
		CMModel model = (CMModel) state;
		
		model.numMoved=model.getNumMoved();

        try{

            
            agentData(model);
            firmData(model);
            
            int i, j;
            int age[] = new int[agents.numObjs]; 
            int edu[] = new int[agents.numObjs];
            
            int sumOfEmployed = 0;      
            
        	Agent agent;

        	int numFromOriSC[] = new int[model.numProvinces];
        	int numFromOriHN[] = new int[model.numProvinces];   	
        	int numToDesGD[] = new int[model.numProvinces];
        	int numToDesJS[] = new int[model.numProvinces];
        	
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
            String strGD = null, strJS = null, strHN = null, strSC = null;
       
            s0 = s0.valueOf(model.schedule.getSteps()+1);
            s1 = s1.valueOf(sumOfEmployed);
            s2 = s2.valueOf(agemean);
            s3 = s3.valueOf(edumean);
            
            strEmployed = s0 + "," + s1 + "," + s2 + "," + s3 + ",";
            output.write(strEmployed);
            output.flush();
            output.newLine(); 
            output.flush();
 
     		for (i = 0; i < model.firm[5].employees.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[5].employees.get(i);
    			numToDesGD[agent.originalProvince.firmID]++;

    		}
    		strGD = strGD.valueOf(model.firm[5].employees.numObjs);
    		strGD = strGD + ",";
    		
        	outputDesGD.write(strGD);
    		
			for (j = 0; j < model.numProvinces; j++){
            	strGD = strGD.valueOf(numToDesGD[j]);
            	strGD = strGD + ",";
            	
            	outputDesGD.write(strGD);
			}
			outputDesGD.newLine(); 
			outputDesGD.flush();
			
            
            
    		for (i = 0; i < model.firm[14].employees.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[14].employees.get(i);
    			numToDesJS[agent.originalProvince.firmID]++;

    		}
    		strJS = strJS.valueOf(model.firm[14].employees.numObjs);
    		strJS = strJS + ",";
    		
        	outputDesJS.write(strJS);
    		
			for (j = 0; j < model.numProvinces; j++){
            	strJS = strJS.valueOf(numToDesJS[j]);
            	strJS = strJS + ",";
            	
            	outputDesJS.write(strJS);
			}
			outputDesJS.newLine(); 
			outputDesJS.flush();
			
			
			for (i = 0; i < model.firm[11].origins.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[11].origins.get(i);
    			numFromOriHN[agent.employer.firmID]++;

    		}
    		strHN = strHN.valueOf(model.firm[11].origins.numObjs);
    		strHN = strHN + ",";
    		
        	outputOriHN.write(strHN);
    		
			for (j = 0; j < model.numProvinces; j++){
            	strHN = strHN.valueOf(numFromOriHN[j]);
            	strHN = strHN + ",";
            	
            	outputOriHN.write(strHN);
			}
			outputOriHN.newLine(); 
			outputOriHN.flush();
			
            
       		for (i = 0; i < model.firm[25].origins.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[25].origins.get(i);
    			numFromOriSC[agent.employer.firmID]++;

    		}
       		
    		strSC = strSC.valueOf(model.firm[25].origins.numObjs);
    		strSC = strSC + ",";
    		
        	outputOriSC.write(strSC);
    		
			for (j = 0; j < model.numProvinces; j++){
            	strSC = strSC.valueOf(numFromOriSC[j]);
            	strSC = strSC + ",";
            	
            	outputOriSC.write(strSC);
			}
			outputOriSC.newLine(); 
			outputOriSC.flush();
			
		    
			
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
			for (j = 0; j < 50; j++){
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
			for (j = 0; j < 50; j++){
            	strDD = strDD.valueOf(d[j]);
            	strDD = strDD + ",";
            	
            	outputDegreeDegree.write(strDD);
			}
			outputDegreeDegree.newLine(); 
			outputDegreeDegree.flush();
			
        	String strMMatrx = null, strMMatrix2;
			strMMatrix2 = "Total";
        	
			outputMigrationMatrix =  new BufferedWriter(new FileWriter("MigrationMatrix.csv"));
			outputMigrationMatrix.write(strMMatrix2+ ",");
			for (i = 0 ; i < model.numProvinces; i++){
				strMMatrix2 = strMMatrix2.valueOf(model.firm[i].spvID);
				outputMigrationMatrix.write(strMMatrix2 + ",");
			}
			outputMigrationMatrix.newLine();
			outputMigrationMatrix.flush();
			
			for (i = 0; i < model.numProvinces; i++){
				strMMatrx= strMMatrx.valueOf(numMovedByPro[i]);
				strMMatrx = strMMatrx + ",";
				outputMigrationMatrix.write(strMMatrx);
				for(j = 0;j < model.numProvinces; j++){
					strMMatrx= strMMatrx.valueOf(model.firm[i].numEmployeesOrigins[j]);
					strMMatrx = strMMatrx + ",";
					outputMigrationMatrix.write(strMMatrx);
				}
				outputMigrationMatrix.newLine(); 
				outputMigrationMatrix.flush();
			}
			
			outputNetworkStructure = new BufferedWriter(new FileWriter("NetworkStucture.csv"));
			String strNetwork;
			outputNetworkStructure.write("NetworkStructure"+ ","+"Neighbour");
			outputNetworkStructure.newLine();
			outputNetworkStructure.flush();
			
			
			Network network = model.network;
			Bag edges;
			Edge edge;
			Agent otherAgent;
			
			for (i = 0;i < model.numAgents; i++){
				outputNetworkStructure.write(model.agent[i].agentID+",");
				edges = network.getEdgesIn(model.agent[i]);
				for (j=0; j < edges.numObjs; j++){
					edge = (Edge)edges.get(j);
					otherAgent = (Agent) edge.getOtherNode(model.agent[i]);
					outputNetworkStructure.write(otherAgent.agentID+",");
				}
				outputNetworkStructure.newLine();
				outputNetworkStructure.flush();
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
