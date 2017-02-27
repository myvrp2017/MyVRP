package core.ts;

import org.coinor.opents.*;

import core.models.MyRoute;

public class MySwapMove implements Move 
{
    public int customer;
    public int movement;
    
    Route routeA;
    Route routeB;
    int indexA;
    int indexB;
    Route updateRouteA;
    Route updateRouteB;
    
    public MySwapMove( int customer, int movement )
    {   
        this.customer = customer;
        this.movement = movement;
    }   // end constructor
    
    public MySwapMove(Route routeA, Route routeB, int indexA, int indexB, Route updateRouteA,
			Route updateRouteB) {
		this.routeA = routeA;
		this.routeB = routeB;
		this.indexA = indexA;
		this.indexB = indexB;
		this.updateRouteA = updateRouteA;
		this.updateRouteB = updateRouteB;
	}
	public void operateOn( SolutionTwoopt solution )
    {
        MySolution soln = (MySolution) solution;
        soln.RouteList.remove(indexA);
        soln.RouteList.add(indexA,updateRouteA);
        soln.RouteList.remove(indexB);
        soln.RouteList.add(indexB,updateRouteB);
        soln.calculateDistance();
    }   // end operateOn
    
    
    /** Identify a move for SimpleTabuList */
    public int hashCode()
    {   
        return customer;
    }   // end hashCode
    
}   // end class MySwapMove
