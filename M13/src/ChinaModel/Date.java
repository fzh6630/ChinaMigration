package ChinaModel;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Date implements Steppable{
	public int month ;

	public int year;
	
	
	Date(){
		super();
		
		month = 12;
		year = 1995;
		System.out.println("month:"+month+" year:"+year);
		
	}

	public void step(SimState state) {
		CMModel model = (CMModel) state;
		
		month++;	
		
		if (month > 12){
			month = month - 12;
			year++;
		}		
		System.out.println("month:"+month+" year:"+year);
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void showCurrentDate(){
		System.out.println("month:"+month+" year:"+year);
	}
};