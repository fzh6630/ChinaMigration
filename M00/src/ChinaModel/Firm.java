package ChinaModel;

import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

public class Firm implements Steppable{
	
	Firm(int srr95, int srr96, int srr97, int srr98, int srr99, int srr00,
			int suu95, int suu96, int suu97, int suu98, int suu99, int suu00, 
			double fdi95, double fdi96, double fdi97, double fdi98, double fdi99,
			double land95, double land96, double land97, double land98, double land99,
			int ruralincpc95, int urbanincpc95, 
			int actid, int id, int spv, CMModel m){
		
		super();
		
		srr1995 = srr95;
		srr1996 = srr96;
		srr1996 = srr97;
		srr1998 = srr98;
		srr1999 = srr99;
		srr2000 = srr00;
		srr = new int[]{srr1995, srr1996, srr1997, srr1998, srr1999, srr2000};
		currentSrr = srr95;
		
		suu1995 = suu95;
		suu1996 = suu96;
		suu1997 = suu97;
		suu1998 = suu98;
		suu1999 = suu99;
		suu2000 = suu00;
		suu = new int[]{suu1995, suu1996, suu1997, suu1998, suu1999, suu2000};
		currentSuu = suu95;
		
		fdi1995 = fdi95;
		fdi1996 = fdi96;
		fdi1997 = fdi97;
		fdi1998 = fdi98;
		fdi1999 = fdi99;
		fdi = new double[]{fdi1995, fdi1996, fdi1997, fdi1998, fdi1999};
		currentFdi = fdi95;
		
		land1995 = land95;
		land1996 = land96;
		land1997 = land97;
		land1998 = land98;
		land1999 = land99;
		land = new double[]{land1995, land1996, land1997, land1998, land1999};
		currentLand = land99;
		
		rural_incpc1995 = ruralincpc95;
		rural_incpc1996 = ruralincpc95;
		rural_incpc1997 = ruralincpc95;
		rural_incpc1998 = ruralincpc95;
		rural_incpc1999 = ruralincpc95;
		rural_incpc = new int[]{rural_incpc1995, rural_incpc1996, rural_incpc1997, rural_incpc1998, rural_incpc1999};
		currentRural_incpc = ruralincpc95;
		
		urban_incpc1995 = urbanincpc95;
		urban_incpc1996 = urbanincpc95;
		urban_incpc1997 = urbanincpc95;
		urban_incpc1998 = urbanincpc95;
		urban_incpc1999 = urbanincpc95;
		urban_incpc = new int[]{urban_incpc1995, urban_incpc1996, urban_incpc1997, urban_incpc1998, urban_incpc1999};
		currentUrban_incpc = urbanincpc95;
		
		model = m;
		currentYear = m.date.getYear();
		currentMonth = m.date.getMonth();	
		
		acturalID = actid;
		spvID = spv;
		firmID = id;
		
		numEmployeesOrigins = new int[model.numProvinces];
		
	}
	
	public void step(SimState state) {
		CMModel model = (CMModel) state;
	
		//System.out.println("id" + ProvinceID + "step");

		
		numOutstandingOffers = 0;
		outstandingOfferAgents.clear();
		candidates.clear();
		
		updateData(model);

	}
	
	public int firmID;
	
	public int acturalID; //ID in the Database	
	
	public int spvID;
	
	public double capitalInput;
	
	public double factorProductivity;
	
	public double alpha;
	
	public double beta;
	
	public int openPosition;
	
	public int numOutstandingOffers = 0;
	
	public int numNuclearFamily = 0;
	
	public int numVillage = 0;
	
	public Bag outstandingOfferAgents = new Bag();

	public Bag candidates = new Bag();

	public Bag employees = new Bag();
	
	public Bag origins = new Bag();
	
	public Bag agents = new Bag();
	
	public Bag nuclearFamily = new Bag();
	
	public Bag village = new Bag();
	
	public int[] numEmployeesOrigins;
	
	public CMModel model;
	
	public int srr1995 = 0;
	public int srr1996 = 0;
	public int srr1997 = 0;
	public int srr1998 = 0;
	public int srr1999 = 0;
	public int srr2000 = 0;
	public int[] srr;
	public int currentSrr;
	
	public int suu1995 = 0;
	public int suu1996 = 0;
	public int suu1997 = 0;
	public int suu1998 = 0;
	public int suu1999 = 0;
	public int suu2000 = 0;
	public int[] suu;
	public int currentSuu;

	public double fdi1995 = 0;
	public double fdi1996 = 0;
	public double fdi1997 = 0;
	public double fdi1998 = 0;
	public double fdi1999 = 0;
	public double[] fdi;
	public double currentFdi;
	
	public double land1995 = 0;
	public double land1996 = 0;
	public double land1997 = 0;
	public double land1998 = 0;
	public double land1999 = 0;
	public double[] land;
	public double currentLand;
	
	public int rural_incpc1995 = 0;
	public int rural_incpc1996 = 0;
	public int rural_incpc1997 = 0;
	public int rural_incpc1998 = 0;
	public int rural_incpc1999 = 0;
	public int[] rural_incpc;
	public int currentRural_incpc;

	public int urban_incpc1995 = 0;
	public int urban_incpc1996 = 0;
	public int urban_incpc1997 = 0;
	public int urban_incpc1998 = 0;
	public int urban_incpc1999 = 0;
	public int[] urban_incpc;
	public int currentUrban_incpc;
	
	public int numberOfOffersToMake = 3;
	
	public int currentYear;
	
	public int currentMonth;
	
	public int numRuralAgents = 0;
	
	

	
	public void updateData(CMModel model){
		if (currentYear != model.date.getYear()){
			currentFdi = fdi[(model.date.getYear() - 1996)];
		}
		
	/*	if(firmID == 1){
			System.out.println(currentFdi);
			}
	*/	
		currentYear = model.date.getYear();
		currentMonth = model.date.getMonth();
	}
	

}
