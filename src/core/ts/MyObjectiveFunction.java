package core.ts;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import core.model.VrpProblem;


public class MyObjectiveFunction implements ObjectiveFunction
{
    
    public VrpProblem vrp;
    
    
    public MyObjectiveFunction(VrpProblem vrp){
    	this.vrp = vrp;
    }
    
    public double[] evaluate( MySolution solution, Move move )
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
        	
        	tmp.calculateDistanceAndTravelTime();
        	return new double[]{tmp.totalDistance};
        }   // end else: calculate incremental
    }   // end evaluate

	@Override
	public double[] evaluate(Solution arg0, Move arg1) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}   // end class MyObjectiveFunction
