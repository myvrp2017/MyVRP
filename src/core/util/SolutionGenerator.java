package core.util;
import java.util.ArrayList;

import model.Customer;
import model.FeasibleSolution;
import model.Route;
import model.VrpProblem;

public class SolutionGenerator {
	
	public static FeasibleSolution genereateSolution(VrpProblem vrpProblem,ArrayList<Customer> customers) {
		FeasibleSolution solution = new FeasibleSolution();

		
		while(customers.size() -1 != 0){
			
			
			Route route = new Route();
			double timeFromLastNodeToHome = 0.00;
						
			ArrayList<Customer> usedCustomers = new ArrayList<>();
			
		
			
			for(Customer customer: customers){
				
				if(customer.getId() == 0){
					route.getCustomers().add(customer);
					continue;
				}
				
				double travelTimeBetweenNodes = calculateTravelTime(route, customer);
				double nodeTime = travelTimeBetweenNodes + customer.getServiceTime();
				double arrivalTime = route.getTotalTime() + travelTimeBetweenNodes;
				double timeFromNodeToHome = calculateTimeHome(route, customer);
				timeFromLastNodeToHome = calculateLastLocationHome(route);
				
				if(arrivalTime < customer.getTimeFrom()){
					double waitingTime = customer.getTimeFrom() - arrivalTime;
					arrivalTime = arrivalTime + waitingTime;
					nodeTime = nodeTime + waitingTime;
				}
				
				if(isFeasible(vrpProblem, route, customer, nodeTime, arrivalTime, timeFromNodeToHome)){
					route.getCustomers().add(customer);
					route.setTotalTime(route.getTotalTime() + nodeTime);
					
					usedCustomers.add(customer);
				}
				
			}
			
			route.setTotalTime(route.getTotalTime() + timeFromLastNodeToHome);
			solution.getRoutes().add(route);
			for(Customer location: usedCustomers){
				customers.remove(location);
			}
		}
		for(Route route : solution.getRoutes()){
			route.setTotalDemand(route.calculateTotalDemand());
		}
		return solution;
	}

	public static boolean isFeasible(VrpProblem vrpProblem, Route route, Customer customer, double nodeTime, double arrivalTime, double timeFromNodeToHome) {
		if(route.getTotalDemand() > vrpProblem.getCapacity())
			return false;
		if(route.getTotalTime() + nodeTime >= vrpProblem.getMAX_ROUTE_TIME()){
			return false;
		}
		if(arrivalTime > customer.getTimeTo()){
			return false;
		}
		if(arrivalTime < customer.getTimeFrom()){
			return false;
		}
		if(route.getTotalTime() + nodeTime + timeFromNodeToHome >= vrpProblem.getMAX_ROUTE_TIME()){
			return false;
		}
		
		return true;
	}

	public static double calculateLastLocationHome(Route route) {
		return route.getCustomers().get(route.getCustomers().size()-1).getDistanceMatrix().get(0);
	}

	public static double calculateTimeHome(Route route, Customer customer) { 
		return route.getCustomers().get(0).getDistanceMatrix().get(customer.getId());
	}

	public static double calculateTravelTime(Route route, Customer customer) {
		return route.getCustomers() . get( route.getCustomers().size() -1 ) . getDistanceMatrix(). get( customer.getId() );
	}
	
	//feasible : kha thi
	public static double calculateFitness(VrpProblem vrpProblem, FeasibleSolution solution, int counter) {
		
		ArrayList<Route> routes = solution.getRoutes();
		
		if(routes.size() == 0){
			return 0.0;
		}
	
		double fitness = 1.00;
		double numberOfVehicles = vrpProblem.getNumOfVehicle();
		
		double latestArrivalTime = 0.00;
		
		for(Route route : routes){
			if(route.getTotalTime() > latestArrivalTime){
				latestArrivalTime = route.getTotalTime();
			}
		}
		
		double timeValue = (latestArrivalTime/vrpProblem.getMAX_ROUTE_TIME());
		
		fitness = fitness - (numberOfVehicles/counter) - (timeValue/counter);
		
		return fitness;
		
	}

}
