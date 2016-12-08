package ChinaModel;

import java.util.Comparator;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class FirmList implements Steppable{ 
	public Bag firms = new Bag();
	
	FirmList(Firm[] f){
		firms.clear();
		firms.addAll(f);
		firms.sort(new FDIComparator());
	}
	
	public void step(SimState state) {
		CMModel model = (CMModel) state;
		firms.sort(new FDIComparator());	
		//Firm f1 = (Firm)firms.get(0);
		//System.out.println(f1.currentFdi);
		
	}
	
	
	public class FDIComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			Firm a1 = (Firm) o1;
			Firm a2 = (Firm) o2;
			
			//System.out.println("Compare"+a1.acturalID+"with"+a2.acturalID);

			double ret = -a1.currentFdi + a2.currentFdi;

			if (ret > 0) {
				return 1;
			} else if (ret < 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	

}
