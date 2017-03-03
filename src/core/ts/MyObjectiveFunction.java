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
            return new double[]{ soln.getTotalDistance() };
        }   // end if: move == null

        // Else calculate incrementally
        else
        {
        	MySwapMove mv = (MySwapMove)move;
        	MySolution tmp = new MySolution(soln);
        	
        	tmp.getRouteList().remove(mv.indexA);
        	tmp.getRouteList().add(mv.indexA,mv.updateRouteA);
        	tmp.getRouteList().remove(mv.indexB);
        	tmp.getRouteList().add(mv.indexB,mv.updateRouteB);
        	
        	tmp.calculateDistanceAndTravelTime();
        	return new double[]{tmp.getTotalDistance()};
        }   // end else: calculate incremental
    }   // end evaluate

	@Override
	public double[] evaluate(Solution arg0, Move arg1) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}   // end class MyObjectiveFunction
