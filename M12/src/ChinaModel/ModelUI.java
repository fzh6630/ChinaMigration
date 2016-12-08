package ChinaModel;



import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;

import javax.swing.*;

import java.awt.*;


public class ModelUI extends GUIState
{
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
	NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();

	
	public static void main(String[] args)
	{
		ModelUI vid = new ModelUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}



	public ModelUI() { super(new CMModel(System.currentTimeMillis())); }
	public ModelUI(SimState state) { super(state); }
	public static String getName() { return "Toy Model"; }

	public Object getSimulationInspectedObject() { return state; }
	public Inspector getInspector()
	{
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}
	
	
	public void start()
	{
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals()
	{
		CMModel model = (CMModel) state;

		yardPortrayal.setField( model.yard );
		yardPortrayal.setPortrayalForAll(new OvalPortrayal2D());
		
	// reschedule the displayer
		

	    buddiesPortrayal.setField( new SpatialNetwork2D( model.yard, model.network ) );
	    buddiesPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
	    
		display.reset();
		display.setBackdrop(Color.white);
	// redraw the display
		display.repaint();
	}
	public void init(Controller c)
	{
		super.init(c);
		display = new Display2D(600,600,this);
		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("Toy Model");
		c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		displayFrame.setVisible(true);
		display.attach( buddiesPortrayal, "InsideNetwork" );
		display.attach( yardPortrayal, "Yard" );
	}
	
	public void quit()
	{
		super.quit();
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}
}
