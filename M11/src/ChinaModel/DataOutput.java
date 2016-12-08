package ChinaModel;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

import java.io.*;

public class DataOutput implements Steppable{
	
	public Bag agents =  new Bag();
	
	public Bag firms = new Bag();
	
	BufferedWriter output;
	
	BufferedWriter outputDesGD;
	
	BufferedWriter outputDesJS;
	
	BufferedWriter outputOriHN;
	
	BufferedWriter outputOriSC;
	
	DataOutput(CMModel model){
		super();
		try {
			int i;
			output = new BufferedWriter(new FileWriter("CMModelOutput"));
			outputDesGD = new BufferedWriter(new FileWriter("m0_d_gd.csv"));
			outputDesJS = new BufferedWriter(new FileWriter("m0_d_js.csv"));
			outputOriHN = new BufferedWriter(new FileWriter("m0_o_hn.csv"));
			outputOriSC = new BufferedWriter(new FileWriter("m0_o_sc.csv"));
			
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
			
		}
        catch(IOException e){
            System.out.println("Error"+e);
        }
		
	}
	
	public void step(SimState state){
		CMModel model = (CMModel) state;

        try{

            
            agentData(model);
            firmData(model);
            
            int i;
            int j;
            int move[] = new int[agents.numObjs];
            int age[] = new int[agents.numObjs]; 
            int edu[] = new int[agents.numObjs];
            
            int sumOfEmployed = 0;
            
            int employer[] = new int[agents.numObjs];       
            
        	Agent agent;

        	int numFromOriSC[] = new int[model.numProvinces];
        	int numFromOriHN[] = new int[model.numProvinces];
        	
        	int numToDesGD[] = new int[model.numProvinces];
        	int numToDesJS[] = new int[model.numProvinces];

        	
        	
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
            	}

            	//System.out.println(agent.age);
            }
            
            double agemean = agesum / (double) sumOfEmployed;
            double edumean = edusum / (double) sumOfEmployed;
            
            Firm firm;
            int employees[] = new int[firms.numObjs];
            

            
            String s = null;
            String s0 = null;
            String s1 = null;
            String s2 = null;
            String s3 = null;
            String s4 = null;
            String s5 = null;
            String s6 = null;
            String s7 = null;
        	
        	s0 = s0.valueOf(model.schedule.getTime());
        	s1 = s1.valueOf(sumOfEmployed);
            s2 = s2.valueOf(agemean);
            s3 = s3.valueOf(edumean);
            
            s = s0 + "," + s1 + "," + s2 + "," + s3 + ",";
            output.write(s);
            output.flush();
            output.newLine(); 
            output.flush();
            /*
            for(i = 0; i < firms.numObjs; i++)
            {
            	firm = model.firm[i];
            	employees[i] = firm.employees.numObjs;

            	
            	s4 = s4.valueOf(employees[i]);
            	s4 = s4 + ",";
            	
            	
            	output.write(s4);
            	output.flush();
            	
            }
*/
     		for (i = 0; i < model.firm[5].employees.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[5].employees.get(i);
    			numToDesGD[agent.originalProvince.firmID]++;

    		}
    		s4 = s4.valueOf(model.firm[5].employees.numObjs);
    		s4 = s4 + ",";
    		
        	outputDesGD.write(s4);
    		
			for (j = 0; j < model.numProvinces; j++){
            	s4 = s4.valueOf(numToDesGD[j]);
            	s4 = s4 + ",";
            	
            	outputDesGD.write(s4);
			}
			outputDesGD.newLine(); 
			outputDesGD.flush();
			
            
            
    		for (i = 0; i < model.firm[14].employees.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[14].employees.get(i);
    			numToDesJS[agent.originalProvince.firmID]++;

    		}
    		s5 = s5.valueOf(model.firm[14].employees.numObjs);
    		s5 = s5 + ",";
    		
        	outputDesJS.write(s5);
    		
			for (j = 0; j < model.numProvinces; j++){
            	s5 = s5.valueOf(numToDesJS[j]);
            	s5 = s5 + ",";
            	
            	outputDesJS.write(s5);
			}
			outputDesJS.newLine(); 
			outputDesJS.flush();
			
			
			for (i = 0; i < model.firm[11].origins.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[11].origins.get(i);
    			numFromOriHN[agent.employer.firmID]++;

    		}
    		s6 = s6.valueOf(model.firm[11].origins.numObjs);
    		s6 = s6 + ",";
    		
        	outputOriHN.write(s6);
    		
			for (j = 0; j < model.numProvinces; j++){
            	s6 = s6.valueOf(numFromOriHN[j]);
            	s6 = s6 + ",";
            	
            	outputOriHN.write(s6);
			}
			outputOriHN.newLine(); 
			outputOriHN.flush();
			
            
       		for (i = 0; i < model.firm[25].origins.numObjs; i ++)
    		{
    			agent = (Agent)model.firm[25].origins.get(i);
    			numFromOriSC[agent.employer.firmID]++;

    		}
       		
    		s7 = s7.valueOf(model.firm[25].origins.numObjs);
    		s7 = s7 + ",";
    		
        	outputOriSC.write(s7);
    		
			for (j = 0; j < model.numProvinces; j++){
            	s7 = s7.valueOf(numFromOriSC[j]);
            	s7 = s7 + ",";
            	
            	outputOriSC.write(s7);
			}
			outputOriSC.newLine(); 
			outputOriSC.flush();


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
