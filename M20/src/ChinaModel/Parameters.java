package ChinaModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Parameters {
	CMModel model;
	Parameters(long job){
		/*int i, j, k ,m;
		int count = 0;
		for(i = 0;i < 10; i++)
			for(j = 0;j < 10; j++)
				for(k = 0;k < 10; k++)
					for(m = 0;m < 10; m++){
						count++;
						if (count == job){
							c2 = 1.0 + i * 0.5;
							c3 = 0.1 + j * 0.1;
							c4 = 0.5 + k * 0.1;
							pa = 0.1 + 0.05 * m;
						}
					}

		Random random = new Random(job*3);
		c2 = 2 + 3 * random.nextDouble();
		epsilon = 0.5 * random.nextDouble();
		eta2 = 0.9 + 0.1 *  random.nextDouble();
		c3 = 2 * random.nextDouble();
		eta3 = 0.9 + 0.1 * random.nextDouble();
		c4 = 3 * random.nextDouble();
		eta4 = 0.9 + 0.1 *  random.nextDouble();
		pa = 0.5 * random.nextDouble();
		phi = 0.5 * random.nextDouble();
*/		
		int i = 0;
		
		try { 			
			BufferedReader reader = new BufferedReader(new FileReader("parametersLHS_modified2"));
			String line = null; 
			
	            
			while(i <  13 && (line=reader.readLine())!=null){ 						
				i++;
			} 
			
			line=reader.readLine();
			String item[] = line.split(",");
			c2 = Double.parseDouble(item[0]);
			c3 = Double.parseDouble(item[1]);
			c4 = Double.parseDouble(item[2]);
			Cpa = Double.parseDouble(item[3]);
			beta = Double.parseDouble(item[4]);
			theta = Double.parseDouble(item[5]);
			rou = Double.parseDouble(item[6]);
			omega = Double.parseDouble(item[7]);
			
			eta2 = Double.parseDouble(item[8]);
			eta3 = Double.parseDouble(item[9]);
			eta4 = Double.parseDouble(item[10]);
			alpha = Double.parseDouble(item[11]);
			lamda = Double.parseDouble(item[12]);


		}

		catch (Exception e) { 
	        	e.printStackTrace(); 
		}	
		System.out.println(c2);

	}
	Parameters(){	
		
	}

	public double c2 = 5.0;
	public double c3 = 2.0;
	public double c4 = 3.0;
	public double Cpa = 1.0;
	public double beta = 0.02;
	public double theta = 0.5;
	public double rou = 0.8;
	public double omega = 0.8;

	public double eta2 = 1.0;
	public double eta3 = 1.0;
	public double eta4 = 1.0;
	public double alpha = 0.0005;
	public double lamda = 0.5;


}
