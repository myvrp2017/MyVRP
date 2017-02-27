package core.ts;

import org.coinor.opents.*;

import core.models.VrpProblem;


public class MyObjectiveFunction implements ObjectiveFunction
{
    
    public VrpProblem vrp;
    
    
    public MyObjectiveFunction(VrpProblem vrp){
    	this.vrp = vrp;
    }
    
    public double[] evaluate( SolutionTwoopt solution, Move move )
    {
        MySolution soln = (MySolution) solution;
        
        // If move is null, calculate distance from scratch
        if( move == null )
        {
            return new double[]{ soln.totalDistance };
        }   // end if: move == null

        // Else calculate incrementally
        else
        {
        	MySwapMove mv = (MySwapMove)move;
        	MySolution tmp = new MySolution(soln);
        	
        	tmp.RouteList.remove(mv.indexA);
        	tmp.RouteList.add(mv.indexA,mv.updateRouteA);
        	tmp.RouteList.remove(mv.indexB);
        	tmp.RouteList.add(mv.indexB,mv.updateRouteB);
        	
        	tmp.calculateDistance();
        	return new double[]{tmp.totalDistance};
        }   // end else: calculate incremental
    }   // end evaluate
    
    
}   // end class MyObjectiveFunction
