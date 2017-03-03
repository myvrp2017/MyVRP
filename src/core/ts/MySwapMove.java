package core.ts;

import org.coinor.opents.Move;
import org.coinor.opents.Solution;

import core.model.Route;

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
	public void operateOn( Solution solution )
    {
        MySolution soln = (MySolution) solution;
        soln.getRouteList().remove(indexA);
        soln.getRouteList().add(indexA,updateRouteA);
        soln.getRouteList().remove(indexB);
        soln.getRouteList().add(indexB,updateRouteB);
        soln.calculateDistanceAndTravelTime();
    }   // end operateOn
    
    
    /** Identify a move for SimpleTabuList */
    public int hashCode()
    {   
        return customer;
    }   // end hashCode
    
}   // end class MySwapMove
