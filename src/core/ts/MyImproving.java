package core.ts;

import java.util.ArrayList;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.VrpProblem;
import core.util.MyUtility;

public class MyImproving {
	public static void twoOpt(Route route1, Route route2, VrpProblem vrpProblem) {
		System.out.println("2-opt");
		
		int min_i = -1;
		int min_j = -1;
		double minchange;
		do {
			minchange = 0.00;
			for (int i = 1; i < route1.getListOfDelivery().size() - 1; i++) {
				for (int j = 1; j < route2.getListOfDelivery().size() - 1; j++) {
					/*
					 * edge_ij is distance between i vs j between route 1 & 2
					 * edge_ij1 is distance between i+1 vs j+1 between route 1 &
					 * 2 edge_ii is distance between i vs i+1 of route1 edge_jj
					 * is distance between j vs j+1 of route2
					 */
					double edge_ij = MyUtility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCustomer(),
							route2.getListOfDelivery().get(j).getLocationOfCustomer());
					double edge_ij1 = MyUtility.calculateDistance(
							route1.getListOfDelivery().get(i + 1).getLocationOfCustomer(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCustomer());
					double edge_ii = MyUtility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCustomer(),
							route1.getListOfDelivery().get(i + 1).getLocationOfCustomer());
					double edge_jj = MyUtility.calculateDistance(route2.getListOfDelivery().get(j).getLocationOfCustomer(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCustomer());

					double change = edge_ij + edge_ij1 - edge_ii - edge_jj;

					if (minchange > change) {
						minchange = change;
						min_i = i;
						min_j = j;
					}
				}
			}
			// TODO check max route time and the other constraints
			if (min_i > 0 && min_j > 0 && min_j < route2.getListOfDelivery().size() - 1) {
				// insert Node j and j+1 into route1 route1 : 0...i j i+1 j+1...
				Delivery customer_j = route2.getListOfDelivery().get(min_j);
				Delivery customer_j1 = route2.getListOfDelivery().get(min_j + 1);

				// insert j and j+1 to route 1

				boolean isInsertable = isInsertable(vrpProblem,route1, route2, customer_j, min_i + 1, min_j);
				if (isInsertable) {
					route1.getListOfDelivery().add(min_i + 1, customer_j);
					route2.getListOfDelivery().remove(customer_j);
					route1.setTotalDemand(route1.getTotalDemand() + customer_j.getDemand());
					route2.setTotalDemand(route2.getTotalDemand() - customer_j.getDemand());
				}
				isInsertable = isInsertable(vrpProblem,route1, route2, customer_j1, min_i + 3, min_j);
				if (isInsertable) {
					route1.getListOfDelivery().add(min_i + 3, customer_j1);
					route2.getListOfDelivery().remove(customer_j1);
					route1.setTotalDemand(route1.getTotalDemand() + customer_j1.getDemand());
					route2.setTotalDemand(route2.getTotalDemand() - customer_j1.getDemand());
				}
				System.out.println("------");
			}

		} while (minchange < 0);
		/*
		 * routes.add(route1); if(route2.sizeofRoute() > 0){ routes.add(route2);
		 * } return routes;
		 */
	}

	/**
	 * check constraint when insert customer at position i of route 1
	 * 
	 * @param r1
	 * @param r2
	 * @param customer
	 * @return true/false
	 */
	// TODO fix isInsertable with time constraints
	public static boolean isInsertable(VrpProblem vrpProblem, Route route1, Route route2, Delivery customer, int i, int j) {
		Route r1 = (Route) route1.clone();
		Route r2 = (Route) route2.clone();

		if (r1.getTotalDemand() + customer.getDemand() > vrpProblem.getCapacityOfVehicle())
			return false;
		return true;

		/*
		 * double edge_ij =
		 * MyUltility.calculateDistance(r1.getLocations().get(i),
		 * r2.getLocations().get(j)); //spped of the vehicle set to 1; // t =
		 * V/S int speed = 1; double travel_time = edge_ij / speed ;
		 */

	}
	/*
	 * Test construction method parallel mix sequential
	 */
	public MySolution initialSolutionParallel() {
		/*ArrayList<Location> customers = this.vrpProblem.getLocations();
		ArrayList<Double> savingMatrix = calculateSavingMatrix();
		//order increasing....
		Collections.sort(savingMatrix);
		
		// order non-increasing
		Collections.reverse(savingMatrix);
		
		System.out.println(savingMatrix);
		
		//Parallel construct
		//determine the size of route for the initial
		double power = (int)Math.log10(customers.size());
		int numOfRoute = (int)Math.pow(2, power) + 2;
		
		
		for(int i = 0 ; i < numOfRoute ; i ++ ){
			//Create route i
			//update not routed customers 
		}
		
		// Improving numOfRoute routes to minimize size each of route
		for(int i = 0 ; i < numOfRoute -1 ; i ++){
			for(int j = i+1 ; j < numOfRoute ; j ++){
				//twoOpt(route[i],route[j]);
			}
		}*/
		//remove empty route like {0,0} in list of Route -> update numofRoute
		
		//Sequential construct
		/*
		 * while(!unrouted.isEmpty()){
		 * 	for(int i = 0 ; i < numOfRoute ; i ++){
		 * 		add customer(i) to route(i) 
		 * 	}
		 *  still unrouted customer initial new route
		 *  create new route with the remaining customer
		 * }
		 */
		
		
		
		return null;
	}
	/*
	 * Slice list of Locations to n piece
	 * then execute Construction method each piece with parallel approach  
	 */
	public MySolution initialSolutionParallel_Type3() {
		/*ArrayList<Location> customers = this.vrpProblem.getLocations();
		ArrayList<Double> savingMatrix = calculateSavingMatrix();
		//order increasing....
		Collections.sort(savingMatrix);
		
		// order non-increasing
		Collections.reverse(savingMatrix);
		
		System.out.println(savingMatrix);
		
		//Parallel construct
		//determine the number of piece for list customers
		double nPiece = (int)Math.log10(customers.size());
		//get (nPiece) sub-list based on nPiece
		
		
		for(int i = 0 ; i < nPiece ; i ++ ){
			//Create all route for sub-list i
			//update not routed customers 
		}
		
		int numOfRoute = this.feasibleSolution.getRoutes().size();
		// Improving numOfRoute routes to minimize size each of route
		for(int i = 0 ; i < numOfRoute -1 ; i ++){
			for(int j = i+1 ; j < numOfRoute ; j ++){
				//twoOpt(route[i],route[j]);
			}
		}*/
		//remove empty route like {0,0} in list of Route -> update numofRoute
		
		return null;
	}

	public double calculateSaving(int i, int j) {
		//ArrayList<Location> customers = this.vrpProblem.getLocations();
		double saving = 0.0;
		/*double c_i0 = MyUltility.getDistance(customers.get(i), customers.get(0));
		double c_0j = MyUltility.getDistance(customers.get(0), customers.get(j));
		double c_ij = MyUltility.getDistance(customers.get(i), customers.get(j));

		saving = MyUltility.round(c_i0 + c_0j - c_ij, 2);*/
		return saving;
	}
	public ArrayList<Double> calculateSavingMatrix(){
		//ArrayList<Location> customers = this.vrpProblem.getLocations();
		ArrayList<Double> savingMatrix = new ArrayList<>();
		/*for(int i = 1 ; i < customers.size()-2; i++){
			for(int j = i+1 ; j < customers.size()-1 ; j++){
				savingMatrix.add(calculateSaving(i, j));
			}
		}*/
		return savingMatrix;
	}
}
