package ChinaModel;



import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import sim.portrayal.FieldPortrayal2D;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.Portrayal;
import sim.portrayal.LocationWrapper;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;
import java.awt.Graphics2D;
import sim.util.Bag;
import java.awt.geom.Point2D;
import sim.portrayal.simple.OvalPortrayal2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.*;

import java.awt.*;
import sim.util.media.chart.BarChartGenerator;
import sim.util.media.chart.TimeSeriesChartGenerator;




public class ModelUI extends GUIState
{
	public Display2D display;	
	public JFrame displayFrame;
	ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
	NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();
//	SimpleInspector inspectorFDI;
	
	public BarChartGenerator barChart;	
	public JFrame barChartFrame;
	public TimeSeriesChartGenerator timeSeriesChart;
	public JFrame timeSeriesChartFrame;
	
	public Display2D display2;
	public JFrame displayFrame2;


	SimplePortrayal2D defaultPortrayal = new OvalPortrayal2D();
    public Portrayal getDefaultPortrayal()
    	{return defaultPortrayal; }
    

    
    public Double2D getScale(DrawInfo2D fieldPortrayalInfo)
    {
    	double boundsx = 20.0; // our pretend "width"
    	double boundsy = 20.0; // our pretend "height"
    	double xScale = fieldPortrayalInfo.draw.width / boundsx; 
    	double yScale = fieldPortrayalInfo.draw.height / boundsy; 
    	return new Double2D(xScale, yScale);
    }
	
	
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
	    
	//    inspectorFDI = new SimpleInspector(model.numAgents,this);
	    
	    
		display.reset();
		display.setBackdrop(Color.white);
	// redraw the display
		display.repaint();
	    
	//	display2.reset();
	//	display2.setBackdrop(Color.black);
	// redraw the display
	//	display2.repaint();
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
	//	display.attach( inspectorFDI, "FDI");
		
	/*	
		display2 = new Display2D(600,600,this);
		display2.setClipping(false);
		displayFrame2 = display2.createFrame();
		displayFrame2.setTitle("Test");
		c.registerFrame(displayFrame2); // so the frame appears in the "Display" list
		displayFrame2.setVisible(true);*/
	}
	
	
	
	
	public void quit()
	{
		super.quit();
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}
}
