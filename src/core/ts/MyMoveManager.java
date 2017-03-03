package core.ts;

import java.util.ArrayList;
import java.util.Random;

import org.coinor.opents.*;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;


public class MyMoveManager implements MoveManager
{
    private VrpProblem vrp;
    private int speed = 1;
    public MyMoveManager(VrpProblem vrp){
    	this.vrp = vrp;
    }
    public Move[] getAllMoves( MySolution solution )
    {   
    	ArrayList<Move> listMove = doGetAllMoves((MySolution)solution);
    	Move[] moves = new Move[listMove.size()];
    	for(int i = 0; i < listMove.size(); i++)
    		moves[i] = (Move) listMove.get(i);
        return moves;
    }   // end getAllMoves

	private ArrayList<Move> doGetAllMoves(MySolution solution) {
		MySolution tmpSoln = new MySolution(solution);
		ArrayList<Move> listMove = new ArrayList<>();
		ArrayList<Route> listRoute = (ArrayList<Route>) tmpSoln.getRouteList().clone();
		
		for(int i = 0; i < listRoute.size(); i++){
			Random rd = new Random();
			Route rA = listRoute.get(i);
			ArrayList<Integer> listIndexB = new ArrayList<>();
			listIndexB.add(i);
			int j = 0;
			while(rA.getListOfDelivery().size() >= 2 && j < listRoute.size()-1){
				int indexB = rd.nextInt(listRoute.size());
				if(!listIndexB.contains(indexB)){
					listIndexB.add(indexB);
					Route rB = listRoute.get(indexB);
					
					ArrayList listCandidate = getListCandidate(rA,rB);
					for(int k = 0; k < listCandidate.size(); k++){
						ArrayList<?> candidate = (ArrayList) listCandidate.get(k);
						listMove.add(new MySwapMove((Route)candidate.get(0), (Route) candidate.get(1), i,
								indexB, (Route)candidate.get(2), (Route)candidate.get(3)));
					}
				}
				j++;
			}
			j = 0;
		}
		return listMove;
	}
	private ArrayList getListCandidate(Route rA, Route rB) {
		ArrayList listCandidate = new ArrayList<>();
		for(int i = 1; i < rA.getListOfDelivery().size()-1; i++){
			for(int j = 0; j < rB.getListOfStage().size(); j++){
				Route updateRouteA = rA.clone();
				Route updateRouteB = rB.clone();
				Stage tmpStage = rB.getListOfStage().get(j);
				ArrayList tmp = new ArrayList<>();
				if(Feasible(rA,rB,updateRouteA,updateRouteB,tmpStage,j,i)){
					tmp.add(rA);
					tmp.add(rB);
					tmp.add(updateRouteA);
					tmp.add(updateRouteB);
					listCandidate.add(tmp);
				}
			}
		}
		return listCandidate;
	}
	private boolean Feasible(Route rA, Route rB, Route updateRouteA, Route updateRouteB, Stage tmpStage,
			int positionOfStage,int positionOfRA) {
		Delivery del_u = rA.getListOfDelivery().get(positionOfRA);
		Delivery del_j = rB.getListOfDelivery().get(positionOfStage+1);
		Route saveRouteB = rB.clone();
		Route saveRouteA = rA.clone();
		
		if((rB.getTotalDemand() + del_u.getDemand()) > this.vrp.getCapacityOfVehicle())
			return false;
		//insert u into ij
		//check constraint time
		double dist_iu = norm(tmpStage.getDepartPoint(),this.vrp.getLocationOfCustomers().get(del_u.getId()));
		double trlTime_iu = calculateTrlTime(dist_iu, this.speed);
		double arrTime_iu = trlTime_iu + tmpStage.getStartingTime();
		if(arrTime_iu > del_u.getTimewindowTo())
			return false;
		double issuingTime_iu = 0;
		if(arrTime_iu <= del_u.getTimewindowFrom())
			issuingTime_iu = del_u.getTimewindowFrom();
		else
			issuingTime_iu = arrTime_iu;
		
		double dist_uj = norm(this.vrp.getLocationOfCustomers().get(del_u.getId()),tmpStage.getDestinationPoint());
		double trlTime_uj = calculateTrlTime(dist_uj, this.speed);
		double arrTime_uj = trlTime_uj + issuingTime_iu + del_u.getServiceTime();
		if(arrTime_uj > del_j.getTimewindowTo())
			return false;
		double issuingTime_uj = 0;
		if(arrTime_uj <= del_j.getTimewindowFrom())
			issuingTime_uj = del_j.getTimewindowFrom();
		else
			issuingTime_uj = arrTime_uj;
		
		Stage s_iu = new Stage(tmpStage.getDepartPoint(), this.vrp.getLocationOfCustomers().get(del_u.getId()), 
				tmpStage.getStartingTime(), arrTime_iu, issuingTime_iu, issuingTime_iu+del_u.getServiceTime(),
				dist_iu, trlTime_iu, tmpStage.getDistanceFromDepot() + dist_iu);
		Stage s_uj = new Stage(this.vrp.getLocationOfCustomers().get(del_u.getId()),tmpStage.getDestinationPoint() , 
				issuingTime_iu + del_u.getServiceTime(), arrTime_uj, issuingTime_uj, issuingTime_uj+del_j.getServiceTime(),
				dist_uj, trlTime_uj, tmpStage.getDistanceFromDepot() + dist_iu + dist_uj);
		//calculate time after insert u
		double currentTotalTrlTimeRB = issuingTime_uj + del_j.getServiceTime();
		for(int j = positionOfStage+1; j < rB.getListOfStage().size(); j++){
			Stage tmp1 = rB.getListOfStage().get(j);
			Delivery tmp2 = rB.getListOfDelivery().get(j+1);
			if((currentTotalTrlTimeRB + tmp1.getTravelTime() )> tmp2.getTimewindowTo()){
				rB = saveRouteB;
				return false;
			}
			Stage updateStageB = new Stage(tmp1.getDepartPoint(),tmp1.getDestinationPoint(),
					currentTotalTrlTimeRB,tmp2);
			updateRouteB.getListOfStage().remove(j);
			updateRouteB.getListOfStage().add(j,updateStageB);
			currentTotalTrlTimeRB = updateStageB.getEndTime();
		}
		updateRouteB.getListOfDelivery().add(positionOfStage+1,del_u);
		updateRouteB.setTotalDemand(updateRouteB.getTotalDemand() + del_u.getDemand());
		updateRouteB.setTotalDistance( updateRouteB.getTotalDistance() - tmpStage.getDistance() + dist_iu + dist_uj);
		updateRouteB.setTotalTravelTime(currentTotalTrlTimeRB);
		//remove stage ij and insert stage iu,uj
		updateRouteB.getListOfStage().remove(positionOfStage);
		updateRouteB.getListOfStage().add(positionOfStage,s_iu);
		updateRouteB.getListOfStage().add(positionOfStage+1,s_uj);
		
		//calculate rA
		//A-B-C remove B
		Delivery del_a = rA.getListOfDelivery().get(positionOfRA-1);
		Delivery del_c = rA.getListOfDelivery().get(positionOfRA+1);
		Delivery del_b = rA.getListOfDelivery().get(positionOfRA);
		double dist_ac = norm(del_a.getLocationOfCustomer(),del_c.getLocationOfCustomer());
		double dist_ab = norm(del_a.getLocationOfCustomer(),del_b.getLocationOfCustomer());
		double dist_bc = norm(del_b.getLocationOfCustomer(),del_c.getLocationOfCustomer());
		double trlTime_ac = calculateTrlTime(dist_ac, this.speed);
		double arrTime_ac = trlTime_ac + rA.getListOfStage().get(positionOfRA-1).getStartingTime();
		if(arrTime_ac > del_c.getTimewindowTo())
			return false;
		double issuingTime_ac = 0;
		if(arrTime_ac <= del_c.getTimewindowFrom())
			issuingTime_ac = del_c.getTimewindowFrom();
		else
			issuingTime_ac = arrTime_ac;
		Stage s_ac = new Stage(del_a.getLocationOfCustomer(),del_c.getLocationOfCustomer(),rA.getListOfStage().get(positionOfRA-1).getStartingTime(),
				arrTime_ac,issuingTime_ac,issuingTime_ac + del_c.getServiceTime(),dist_ac,trlTime_ac,rA.getListOfStage().get(positionOfRA-1).getDistanceFromDepot() - dist_ab + dist_ac);
		double currentTravelTimeRA = issuingTime_ac + del_c.getServiceTime();
		for(int k = positionOfRA+1; k < rA.getListOfStage().size(); k++){
			Stage st1 = rA.getListOfStage().get(k);
			Delivery del1 = rA.getListOfDelivery().get(k+1);
			if((currentTravelTimeRA + st1.getTravelTime()) > del1.getTimewindowTo()){
				rA = saveRouteA;
				return false;
			}
			Stage updateStageA = new Stage(st1.getDepartPoint(),st1.getDestinationPoint(),currentTravelTimeRA,del1);
			updateRouteA.getListOfStage().remove(k);
			updateRouteA.getListOfStage().add(k,updateStageA);
			currentTravelTimeRA = updateStageA.getEndTime();
		}
		updateRouteA.getListOfDelivery().remove(positionOfRA);
		updateRouteA.setTotalDemand(updateRouteA.getTotalDemand() - del_b.getDemand());
		updateRouteA.setTotalDistance(updateRouteA.getTotalDistance() - dist_ab - dist_bc + dist_ac);
		updateRouteA.setTotalTravelTime(currentTravelTimeRA);
		updateRouteA.getListOfStage().remove(positionOfRA);
		updateRouteA.getListOfStage().remove(positionOfRA-1);
		updateRouteA.getListOfStage().add(positionOfRA-1,s_ac);
		return true;
	}
	private double norm(Location a,Location b)
    {   
        double xDiff = a.getX() - b.getX();
        double yDiff = a.getY() - b.getY();
        return Math.sqrt( xDiff*xDiff + yDiff*yDiff );
    }
	private double calculateTrlTime(double dist, float speed) {
		return dist/speed;
	}
	public VrpProblem getVrp() {
		return vrp;
	}

	public void setVrp(VrpProblem vrp) {
		this.vrp = vrp;
	}
	@Override
	public Move[] getAllMoves(Solution arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    
}   // end class MyMoveManager
