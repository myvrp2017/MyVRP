package core.ts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;
import core.util.MyUtility;

public class TestMySolution extends JComponent{
	
	public static final int WIDTH = 956;
	public static final int HEIGHT = 956;
	
	private static class Line{
	    final int x1; 
	    final int y1;
	    final int x2;
	    final int y2;
	    final int index1;
	    final int index2;
		final Color color;
		public Line(int x1, int y1, int x2, int y2, int index1, int index2, Color color) {
			super();
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.index1 = index1;
			this.index2 = index2;
			this.color = color;
		}
	}
	private final LinkedList<Line> lines = new LinkedList<Line>();

	public void addLine(int x1, int x2, int x3, int x4,int index1, int index2, Color color) {
	    lines.add(new Line(x1,x2,x3,x4,index1,index2,color));        
	    repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    setBackground(Color.WHITE);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        g.drawString(line.index1+"",line.x1, line.y1);
	        g.drawString(line.index2+"",line.x2, line.y2);
	        g.drawLine(line.x1, line.y1, line.x2, line.y2);
	    }
	}
	public static void main (String args[]) throws FileNotFoundException 
    {
		long startTime = System.currentTimeMillis();
        // Initialize our objects
        VrpProblem vrp = new VrpProblem();//1 ignore service time;
        vrp = MyUtility.readFile("c101.txt",true);
        //Solution initialSolution  = new MySolution( vrp );
        MySolution initSolution  = new MySolution( vrp ,1,1,1,1,0);
        long endTime = System.currentTimeMillis();
        if(checkSolution(initSolution, vrp)){
	        double totalDistance = 0;
	        double totalTravelTime = 0;
	        System.out.println("initialize solution:");
	        for( int i = 0; i < initSolution.getRouteList().size(); i++ ){
	        	System.out.print("Route " + i +" : ");
	        	for(int j = 0; j < initSolution.getRouteList().get(i).getListOfDelivery().size(); j++){
	        		System.out.print(initSolution.getRouteList().get(i).getListOfDelivery().get(j).getId()+" - ");
	        	}
	        	System.out.print(" Total Demand: " + initSolution.getRouteList().get(i).getTotalDemand());
	        	System.out.print(" Total Distance: " + initSolution.getRouteList().get(i).getTotalDistance());
	        	System.out.print(" Total TravelTime: " + initSolution.getRouteList().get(i).getTotalTravelTime());
	        	totalDistance += initSolution.getRouteList().get(i).getTotalDistance();
	        	totalTravelTime += initSolution.getRouteList().get(i).getTotalTravelTime();
	        	System.out.println("");
	        }
	        System.out.println("Total Route :"+initSolution.getRouteList().size());
	        System.out.println("Total TravelTime: " + totalTravelTime);
	        System.out.println("Total Distance: " + totalDistance);
	        System.out.println("Execute Time: "+(endTime - startTime));
	        
	        JFrame testFrame = new JFrame();
	        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        final TestMySolution comp = new TestMySolution();
	        comp.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
	        
	        for(int i = 0; i < initSolution.getRouteList().size(); i++){
	        	Color randomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	        	for(int j = 0; j < initSolution.getRouteList().get(i).getListOfDelivery().size()-2; j++){
	        		Location cus1 = vrp.getLocationOfCustomers().get(initSolution.getRouteList().get(i).getListOfDelivery().get(j).getId());
	        		Location cus2 = vrp.getLocationOfCustomers().get(initSolution.getRouteList().get(i).getListOfDelivery().get(j+1).getId());
	                int x1 = (int) cus1.getX()*7 +100;
	                int x2 = (int) cus2.getX()*7 +100;
	                int y1 = (int) cus1.getY()*7 +100;
	                int y2 = (int) cus2.getY()*7 +100;
	                comp.addLine(x1, y1, x2, y2,initSolution.getRouteList().get(i).getListOfDelivery().get(j).getId(),initSolution.getRouteList().get(i).getListOfDelivery().get(j+1).getId(), randomColor);
	            }
	        }
	
	        testFrame.pack();
	        testFrame.setVisible(true);
        }
        
    }
	public static boolean checkSolution(MySolution soln, VrpProblem vrp){
		ArrayList<Route> routes = soln.getRouteList();
		for(int i = 0; i < routes.size(); i++){
			if(routes.get(i).getTotalDemand() > vrp.getCapacityOfVehicle()){
				System.out.println("Route "+ i +" out of capacity of vehicle");
				return false;
			}
			List<Stage> st = routes.get(i).getListOfStage();
			for(int j = 0; j < st.size(); j++){
				Delivery del = routes.get(i).getListOfDelivery().get(j+1);
				Stage s = st.get(j);
				if(s.getArrivingTime() > del.getTimewindowTo()){
					System.out.println("Stage from "+s.getDepartPoint() + " to "+s.getDestinationPoint()+" out of due time");
					return false;
				}
			}
		}
		return true;
	}
}
