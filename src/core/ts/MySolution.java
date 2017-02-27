package core.ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.coinor.opents.*;

import core.models.Delivery;
import core.models.Location;
import core.models.MyRoute;
import core.models.Stage;
import core.models.VrpProblem;
import core.models.myInsertionProfit;


public class MySolution extends SolutionAdapter 
{
    
    public List<List<Integer>> routes;
    public List<Double> totaldemandOfroute;
    
    public ArrayList<Route> RouteList;
    public double totalDistance = 0;
	public double totalTravelTime = 0;
	public List<Integer> exeptionList;
	public List<Integer> visited = new ArrayList<Integer>();
	float lamda;
	float u;
	float alpha1;
	float alpha2;
	float speed = 1;
    
    public MySolution(){} // Appease clone()
    
    public MySolution( VrpProblem vrp ,int sortparam,float u,float lamda,float alpha1,float alpha2)
    {
        // Crudely initialize solution
        this.seedInsertion(vrp, sortparam, lamda, u, alpha1, alpha2);
        calculateDistance();
    }   // end constructor
    
    public MySolution( VrpProblem vrp )
    {
        // Crudely initialize solution
        System.out.println("initialize solution:");
        this.routes = new ArrayList<List<Integer>>();
        this.totaldemandOfroute = new ArrayList<Double>();
        this.abc(vrp);
        for( int i = 0; i < this.routes.size(); i++ ){
        	//this.tour[i]=i;
        	System.out.print("Route " + i +" : ");
        	for(int j = 0; j < this.routes.get(i).size(); j++){
        		System.out.print(this.routes.get(i).get(j)+" - ");
        	}
        	System.out.print(" Total Demand: " + this.totaldemandOfroute.get(i));
        	System.out.println("");
        }
    }   // end constructor
    public MySolution(MySolution solution) {
		this.RouteList = (ArrayList<Route>) solution.RouteList.clone();
		this.totalDistance = solution.totalDistance;
		this.totalTravelTime = solution.totalTravelTime;
		this.exeptionList = solution.exeptionList;
	}

	public Object clone()
    {   
        MySolution copy = (MySolution)super.clone();
        copy.RouteList = (ArrayList<Route>) this.RouteList.clone();
        return copy;
    }   // end clone
    
    
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        
        s.append( "Solution value: " + getObjectiveValue()[0] );
        for( int i = 0; i < this.RouteList.size(); i++ ){
        	s.append("Route " + i +" : [");
        	for(int j = 0; j < this.RouteList.get(i).getListOfDelivery().size(); j++){
        		s.append(this.RouteList.get(i).getListOfDelivery().get(j).getIndex()+" , ");
        	}
        	s.append( " ]" );
        	s.append(" Total Demand: " + this.RouteList.get(i).getTotalDemand());
        	s.append(" Total Distance: " + this.RouteList.get(i).getTotalDistance());
        	s.append(" Total TravelTime: " + this.RouteList.get(i).getTotalTravelTime());
        	this.totalDistance += this.RouteList.get(i).getTotalDistance();
        	this.totalTravelTime += this.RouteList.get(i).getTotalTravelTime();
        }
        s.append("Total Route :"+this.RouteList.size());
        s.append("Total TravelTime: " + this.totalTravelTime);
        s.append("Total Distance: " + this.totalDistance);
        return s.toString();
    }   // end toString

    public void abc(VrpProblem vrp){
    	boolean[] unvisited = new boolean[vrp.getLocationCustomer().length];
    	unvisited[0] = true;
    	List<Integer> currentRoute = new ArrayList<Integer>();
    	currentRoute.add(0);
    	currentRoute.add(0);
    	this.routes.add(currentRoute);
    	this.totaldemandOfroute.add(0.0);
    	double totalCapacity = vrp.getCapacityOfVehicle();
    	//int currentCustomerNum = -1;
    	for(int i = 0; i < vrp.getLocationCustomer().length; i++){
    		double maxDist = Double.MAX_VALUE;
    		int self=0,neighbour=-1;
    		for(int j=0;j<vrp.getDistanceMatrix()[1].length;j++){
    			if(!unvisited[j]){
    				if(totalCapacity < vrp.getDeliveryList()[j].getDemand()){
						continue;
					}
    				double minDist = Double.MAX_VALUE;
    				int tmpNeighbour=-1;
    				for(int k=0;k<currentRoute.size()-1;k++){
    					if(vrp.getDistanceMatrix()[currentRoute.get(k)][j]>0 
    							&& vrp.getDistanceMatrix()[currentRoute.get(k)][j]<minDist ){
    						minDist = vrp.getDistanceMatrix()[currentRoute.get(k)][j];
    						tmpNeighbour = j;
    					}
    				}
    				if(minDist<maxDist){
    					maxDist=minDist;
    					neighbour=tmpNeighbour;
    				}
    			}
    		}
    		if(neighbour != -1){
	    		double newDist,newMinDist = Double.MAX_VALUE;
	    		for(int a = 0; a<currentRoute.size()-1;a++){
	    			newDist = vrp.getDistanceMatrix()[currentRoute.get(a)][neighbour]+vrp.getDistanceMatrix()[neighbour][currentRoute.get(a+1)]-vrp.getDistanceMatrix()[currentRoute.get(a)][currentRoute.get(a+1)];
	    			if(newDist>0&&newDist<newMinDist){
	    				newMinDist = newDist;
	    				self = a+1;
	    			}
	    		}
	    		currentRoute.add(self, neighbour);
	    		totalCapacity -= vrp.getDeliveryList()[neighbour].getDemand();
	    		double tt = this.totaldemandOfroute.get(this.routes.size()-1)+vrp.getDeliveryList()[neighbour].getDemand();
	    		this.totaldemandOfroute.remove(this.routes.size()-1);
	    		this.totaldemandOfroute.add(tt);
	    		unvisited[neighbour] = true;
    		}else{
    			currentRoute = new ArrayList<Integer>();
    			currentRoute.add(0);
    	    	currentRoute.add(0);
    			this.routes.add(currentRoute);
    			this.totaldemandOfroute.add(0.0);
    			totalCapacity = vrp.getCapacityOfVehicle();
    		}
    	}
    	
    }
    public void seedInsertion(VrpProblem vrp,int sortparam,float lamda,float u,float alpha1,float alpha2){
    	
    	this.alpha1 = alpha1;
    	this.alpha2 = alpha2;
    	this.u = u;
    	this.lamda = lamda;
    	this.exeptionList = checkInput(vrp);
    	if(!this.exeptionList.isEmpty()){
    		for(int e = 0; e < this.exeptionList.size(); e++){
    			System.out.println("Can not scheduled : " + this.exeptionList.get(e));
    		}
    	}
    	int n_unvisited = vrp.getLocationCustomer().length - this.exeptionList.size();
    	boolean[] unvisited = new boolean[vrp.getLocationCustomer().length];
    	unvisited[0] = true;
    	
    	ArrayList<Route> R = new ArrayList<Route>();
    	
    	Delivery[] sortD = vrp.getDeliveryList().clone();
    	List<Delivery> listD = sortDelivery(sortD,sortparam);
    	
    	while(n_unvisited != 0){
    		
    		Route rseed = new Route();
    		rseed = initSeedRoute(vrp,listD,unvisited);
    		
    		n_unvisited = updateN(unvisited,rseed);
    		boolean noMoreFeasibleCustomer = false;
    		while(!noMoreFeasibleCustomer){
    			int uStar = -1;
    			
    			uStar = findCustomerAndUpdateSeed(unvisited,rseed,vrp);//k construction may be replaced
    			
    			if(uStar == -1)
    				noMoreFeasibleCustomer = true;
    			else
    				unvisited[uStar] = true;
    		}
    		n_unvisited = updateN(unvisited,rseed);
    		R.add(rseed);
    	}
    	this.RouteList = R;
    }
    
    private int findCustomerAndUpdateSeed(boolean[] unvisited, Route rseed, VrpProblem vrp) {
    	int uStar = -1;
    	ArrayList<myInsertionProfit> p = new ArrayList<myInsertionProfit>();
    	for(int i = 1; i < unvisited.length; i++){
    		if(!unvisited[i] && !this.exeptionList.contains(i)){
    			for(int j = 0; j < rseed.getListOfStage().size(); j++){
    				Route tmpRoute = new Route(rseed);
    				Stage tmpStage = rseed.getListOfStage().get(j);
    				
    				double c1 = calculeteProfit(tmpStage,rseed,i,vrp,1);
    				double c2 = calculeteProfit(tmpStage,rseed,i,vrp,2);
    				if(Feasible(tmpRoute,tmpStage,i,vrp,j,rseed)){
    					uStar = i;
    					p.add(new myInsertionProfit(c1,c2,uStar,tmpRoute));
    				}
    			}
    		}
    	}
    	myInsertionProfit tmp = null;
    	if(uStar != -1){
    		Comparator c = myInsertionProfit.getComparator(myInsertionProfit.SortParameter.C2_DES, myInsertionProfit.SortParameter.C1_ASC);
            Collections.sort(p, c);
            tmp = p.get(0);
            uStar = tmp.getuStar();
            rseed.setAttribute(tmp.getUpdateRoute());
    	}
		return uStar;
	}
    
    

	private boolean Feasible(Route tmpRoute, Stage tmpStage, int positionOfCus, VrpProblem vrp, int positionInStage, Route currentRoute) {
		Delivery del_u = vrp.getDeliveryList()[positionOfCus];
		Delivery del_j = currentRoute.getListOfDelivery().get(positionInStage+1);
		double currentCapacity = currentRoute.getTotalDemand();
		Route saveRoute = new Route(currentRoute);
		
		if(currentCapacity + del_u.getDemand() > vrp.getCapacityOfVehicle())
			return false;
		//insert u into ij
		double dist_iu = norm(tmpStage.getDepartPoint(),vrp.getLocationCustomer()[positionOfCus]);
		double trlTime_iu = calculateTrlTime(dist_iu, this.speed);
		double arrTime_iu = trlTime_iu + tmpStage.getStartingTime();
		if(arrTime_iu > del_u.getTimewindowTo())
			return false;
		double issuingTime_iu = 0;
		if(arrTime_iu <= del_u.getTimewindowFrom())
			issuingTime_iu = del_u.getTimewindowFrom();
		else
			issuingTime_iu = arrTime_iu;
		
		double dist_uj = norm(vrp.getLocationCustomer()[positionOfCus],tmpStage.getDestinationPoint());
		double trlTime_uj = calculateTrlTime(dist_uj, this.speed);
		double arrTime_uj = trlTime_uj + issuingTime_iu + del_u.getServiceTime();
		if(arrTime_uj > del_j.getTimewindowTo())
			return false;
		double issuingTime_uj = 0;
		if(arrTime_uj <= del_j.getTimewindowFrom())
			issuingTime_uj = del_j.getTimewindowFrom();
		else
			issuingTime_uj = arrTime_uj;
		
		Stage s_iu = new Stage(tmpStage.getDepartPoint(), vrp.getLocationCustomer()[positionOfCus], 
				tmpStage.getStartingTime(), arrTime_iu, issuingTime_iu, issuingTime_iu+del_u.getServiceTime(),
				dist_iu, trlTime_iu, tmpStage.getDistanceFromDepot() + dist_iu);
		Stage s_uj = new Stage(vrp.getLocationCustomer()[positionOfCus],tmpStage.getDestinationPoint() , 
				issuingTime_iu + del_u.getServiceTime(), arrTime_uj, issuingTime_uj, issuingTime_uj+del_j.getServiceTime(),
				dist_uj, trlTime_uj, tmpStage.getDistanceFromDepot() + dist_iu + dist_uj);
		//calculate time after insert u
		double currentTotalTrlTime = issuingTime_uj+del_j.getServiceTime();
		for(int j = positionInStage+1; j < currentRoute.getListOfStage().size(); j++){
			Stage tmp1 = currentRoute.getListOfStage().get(j);
			Delivery tmp2 = currentRoute.getListOfDelivery().get(j+1);
			if((currentTotalTrlTime + tmp1.getTravelTime() )> tmp2.getTimewindowTo()){
				currentRoute = saveRoute;
				return false;
			}
			Stage updateStage = new Stage(tmp1.getDepartPoint(),tmp1.getDestinationPoint(),
					currentTotalTrlTime,tmp2);
			tmpRoute.getListOfStage().remove(j);
			tmpRoute.getListOfStage().add(j,updateStage);
			currentTotalTrlTime = updateStage.getEndTime();
		}
		tmpRoute.getListOfDelivery().add(positionInStage+1,del_u);
		tmpRoute.setTotalDemand(tmpRoute.getTotalDemand() + del_u.getDemand());
		tmpRoute.setTotalDistance( tmpRoute.getTotalDistance() - tmpStage.getDistance() + dist_iu + dist_uj);
		tmpRoute.setTotalTravelTime(currentTotalTrlTime);
		tmpRoute.getListOfStage().remove(positionInStage);
		tmpRoute.getListOfStage().add(positionInStage,s_iu);
		tmpRoute.getListOfStage().add(positionInStage+1,s_uj);
		return true;
	}

	private double calculeteProfit(Stage tmpStage, Route rseed, int positionOfCus, VrpProblem vrp, int optionProfit) {
		
		double d_iu = norm(tmpStage.getDepartPoint(), vrp.getLocationCustomer()[positionOfCus]);
		double d_uj = norm(vrp.getLocationCustomer()[positionOfCus], tmpStage.getDestinationPoint());
		double d_ij = tmpStage.getDistance();
		
		double t_iu =calculateTrlTime(d_iu, this.speed);
		double t_uj =calculateTrlTime(d_uj, this.speed);
		double t_ij =calculateTrlTime(d_ij, this.speed);
		
		double c11 = d_iu + d_uj - this.u*d_ij;
		
		double issuingTimeAtPosition;
		if(tmpStage.getStartingTime() + t_iu <= vrp.getDeliveryList()[positionOfCus].getTimewindowFrom())
			issuingTimeAtPosition = vrp.getDeliveryList()[positionOfCus].getTimewindowFrom();
		else
			issuingTimeAtPosition = tmpStage.getStartingTime() + t_iu;
		double arrTimeFromJtoU = issuingTimeAtPosition + vrp.getDeliveryList()[positionOfCus].getServiceTime()+t_uj;
		
		double c12 = arrTimeFromJtoU - tmpStage.getIssuingTime();
		
		double c1 = this.alpha1*c11 + this.alpha2*c12;
		
		double d_OU = vrp.getDistanceMatrix()[0][positionOfCus];
		double c2 = this.lamda*d_OU - c1;
		
		if(optionProfit == 1){
			return c1;
		}else{
			return c2;
		}
	}

	private double norm(Location a,Location b)
    {   
        double xDiff = a.getX() - b.getX();
        double yDiff = a.getY() - b.getY();
        return Math.sqrt( xDiff*xDiff + yDiff*yDiff );
    }
	
	private int updateN(boolean[] unvisited, Route rseed) {
		for(int i = 0; i < rseed.getListOfDelivery().size(); i++){
			if(!visited.contains(rseed.getListOfDelivery().get(i).getIndex()))
				visited.add(rseed.getListOfDelivery().get(i).getIndex());
			unvisited[rseed.getListOfDelivery().get(i).getIndex()] = true;
		}
		return unvisited.length - this.visited.size() - this.exeptionList.size();
	}

	private Route initSeedRoute(VrpProblem vrp, List<Delivery> listD, boolean[] unvisited) {
		int i;
    	double trlTime = 0;
		double arrTime = 0;
		double issuingTime = 0;
        double endTime = 0;
        double returnDepotTime = 0;
        double [][] distanceMatrix = vrp.getDistanceMatrix();
    	for(i = 0; i < listD.size(); i++){
    		if(this.exeptionList.contains(listD.get(i).getIndex()))
    			continue;
    		if(listD.get(i).getIndex() == 0)
    			continue;
    		if(unvisited[listD.get(i).getIndex()])
    			continue;
    		trlTime = calculateTrlTime(distanceMatrix[0][listD.get(i).getIndex()],this.speed);
    		arrTime = vrp.getDeliveryList()[0].getTimewindowFrom() + trlTime;
    		if(arrTime > listD.get(i).getTimewindowTo())
    			continue;
    		if(arrTime < listD.get(i).getTimewindowFrom())
    			issuingTime = listD.get(i).getTimewindowFrom();
    		else
    			issuingTime = arrTime;
    		endTime = issuingTime + listD.get(i).getServiceTime();
    		returnDepotTime = endTime + trlTime;
    		if(returnDepotTime > vrp.getDeliveryList()[0].getTimewindowTo())
    			continue;
    		break;
    	}
    	// depot -> A -> depot
    	Stage s_OA = new Stage(vrp.getDepot(), listD.get(i).getLocationOfCus(), 0, arrTime, issuingTime, endTime
    			, distanceMatrix[0][listD.get(i).getIndex()], trlTime, distanceMatrix[0][listD.get(i).getIndex()]);
    	Stage s_AO = new Stage(listD.get(i).getLocationOfCus(), vrp.getDepot(), endTime, returnDepotTime,
    			returnDepotTime, returnDepotTime, distanceMatrix[0][listD.get(i).getIndex()], 
    			trlTime, 2*distanceMatrix[0][listD.get(i).getIndex()]);
    	
    	ArrayList<Delivery> list = new ArrayList<Delivery>();
    	list.add(vrp.getDeliveryList()[0]);
    	list.add(listD.get(i));
    	list.add(vrp.getDeliveryList()[0]);
    	
    	ArrayList<Stage> listOfStage = new ArrayList<Stage>();
    	listOfStage.add(s_OA);
    	listOfStage.add(s_AO);
    	
		return new Route(list,listD.get(i).getDemand(),2*vrp.getDistanceMatrix()[0][listD.get(i).getIndex()],returnDepotTime,listOfStage);
	}

	private double calculateTrlTime(double dist, float speed) {
		return dist/speed;
	}

	private List<Delivery> sortDelivery(Delivery[] sortD, int sortparam) {
		List<Delivery> result = Arrays.asList(sortD);
		
		if(sortparam == 1){
			Comparator c = Delivery.getComparator(Delivery.SortParameter.DEMAND_DESC,Delivery.SortParameter.EARLIEST_DEALINE_ASC);
			Collections.sort(result,c);
		}
		if(sortparam == 2){
			Comparator c = Delivery.getComparator(Delivery.SortParameter.DISTANCE_DESC,Delivery.SortParameter.DEMAND_DESC);
			Collections.sort(result,c);
		}
		if(sortparam == 3){
			Comparator c = Delivery.getComparator(Delivery.SortParameter.DEMAND_DESC,Delivery.SortParameter.DISTANCE_DESC);
			Collections.sort(result,c);
		}
		return result;
	}

	private List<Integer> checkInput(VrpProblem vrp){
    	List<Integer> exeption = new ArrayList<Integer>();
    	Delivery[] deliveryList = vrp.getDeliveryList();
    	double [][] distanceMatrix = vrp.getDistanceMatrix();
    	for(int i = 1; i < deliveryList.length; i++){
    		if(deliveryList[i].getDemand() > vrp.getCapacityOfVehicle()){
    			exeption.add(i);
    			continue;
    		}
    		double trlTime = calculateTrlTime(distanceMatrix[0][i],this.speed);
    		if(trlTime > deliveryList[i].getTimewindowTo()){
    			exeption.add(i);
    			continue;
    		}
    	}
    	return exeption;
    }
	public void calculateDistance(){
		double totalDistance = 0;
		double totalTime = 0;
		for( int i = 0; i < this.RouteList.size(); i++ ){
        	totalDistance += this.RouteList.get(i).getTotalDistance();
        	totalTravelTime += this.RouteList.get(i).getTotalTravelTime();
        }
		this.totalDistance = totalDistance;
		this.totalTravelTime = totalTime;
	}
}   // end class MySolution
