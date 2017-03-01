package execute;

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

import org.coinor.opents.BestEverAspirationCriteria;
import org.coinor.opents.MoveManager;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.SimpleTabuList;
import org.coinor.opents.SingleThreadedTabuSearch;
import org.coinor.opents.Solution;
import org.coinor.opents.TabuList;
import org.coinor.opents.TabuSearch;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;
import core.ts.MyMoveManager;
import core.ts.MyObjectiveFunction;
import core.ts.MySolution;
import core.ts.TestMySolution;
import core.util.MyUtility;


public class Main extends JComponent{
	private static final long serialVersionUID = 1L;
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
	public static void main (String args[]) throws FileNotFoundException 
	{
		long startTime = System.currentTimeMillis();
	    // Initialize our objects
	    VrpProblem vrp = new VrpProblem();
	    vrp = MyUtility.readFile("c102.txt", false);//true ignore service time;
	    
	    ObjectiveFunction objFunc = new MyObjectiveFunction( vrp );
	    Solution initialSolution  = new MySolution( vrp ,1,1,1,1,0);
	    MoveManager   moveManager = new MyMoveManager(vrp);
	    TabuList         tabuList = new SimpleTabuList( 7 ); // In OpenTS package
	    
	    // Create Tabu Search object
	    TabuSearch tabuSearch = new SingleThreadedTabuSearch(
	            initialSolution,
	            moveManager,
	            objFunc,
	            tabuList,
	            new BestEverAspirationCriteria(), // In OpenTS package
	            false ); // maximizing = yes/no; false means minimizing
	    
	    // Start solving
	    tabuSearch.setIterationsToGo( 10000 );
	    tabuSearch.startSolving();
	    long endTime = System.currentTimeMillis();
	    // Show solution
	    MySolution best = (MySolution)tabuSearch.getBestSolution();
	    
	    if(checkSolution(best, vrp)){
	        double totalDistance = 0;
	        double totalTravelTime = 0;
	        System.out.println("Best solution:");
	        for( int i = 0; i < best.RouteList.size(); i++ ){
	        	System.out.print("Route " + i +" : ");
	        	for(int j = 0; j < best.RouteList.get(i).getListOfDelivery().size(); j++){
	        		System.out.print(best.RouteList.get(i).getListOfDelivery().get(j).getId()+" - ");
	        	}
	        	System.out.print(" Total Demand: " + best.RouteList.get(i).getTotalDemand());
	        	System.out.print(" Total Distance: " + best.RouteList.get(i).getTotalDistance());
	        	System.out.print(" Total TravelTime: " + best.RouteList.get(i).getTotalTravelTime());
	        	totalDistance += best.RouteList.get(i).getTotalDistance();
	        	totalTravelTime += best.RouteList.get(i).getTotalTravelTime();
	        	System.out.println("");
	        }
	        System.out.println(best.RouteList.size());
	        System.out.println(totalTravelTime);
	        System.out.println(totalDistance);
	        System.out.println((endTime - startTime));
	        
	        JFrame testFrame = new JFrame();
	        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        final TestMySolution comp = new TestMySolution();
	        comp.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
	        
	        for(int i = 0; i < best.RouteList.size(); i++){
	        	Color randomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	        	for(int j = 0; j < best.RouteList.get(i).getListOfDelivery().size()-2; j++){
	        		Location cus1 = vrp.getLocationOfCustomers().get(best.RouteList.get(i).getListOfDelivery().get(j).getId());
	        		Location cus2 = vrp.getLocationOfCustomers().get(best.RouteList.get(i).getListOfDelivery().get(j+1).getId());
	                int x1 = (int) cus1.getX()*7 +100;
	                int x2 = (int) cus2.getX()*7 +100;
	                int y1 = (int) cus1.getY()*7 +100;
	                int y2 = (int) cus2.getY()*7 +100;
	                comp.addLine(x1, y1, x2, y2,best.RouteList.get(i).getListOfDelivery().get(j).getId(),best.RouteList.get(i).getListOfDelivery().get(j+1).getId(), randomColor);
	            }
	        }
	
	        testFrame.pack();
	        testFrame.setVisible(true);
	    }
	    System.out.println("stage of r0");
	    System.out.println(best.RouteList.get(0).getListOfStage());
	    best.RouteList.get(0).calculateStage();
	    
	    System.out.println("calculate stage of r0");
	    System.out.println(best.RouteList.get(0).getListOfStage());
	    
	}// end main
	private final LinkedList<Line> lines = new LinkedList<Line>();

	public static boolean checkSolution(MySolution soln, VrpProblem vrp){
		ArrayList<Route> routes = soln.RouteList;
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
}   // end class Main
