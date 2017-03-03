package core.algorithm;

import java.util.ArrayList;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;
import core.ts.MySolution;
import core.util.MyUtility;

public class KOpt {

	/**
	 * Return the improved solution
	 * @param currentSolution current solution
	 */
	@SuppressWarnings("unchecked")
	public static void twoOptAlgorithmInter(MySolution currentSolution) {
		System.out.println("twoOptAlgorithm");
		
		System.out.println(currentSolution.toString());
		
		ArrayList<Route> routes = (ArrayList<Route>) currentSolution.getRouteList();
		int length = routes.size();

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (i != j) {
					System.out.println("check route before 2opt inter");
					routes.get(i).checkRoute(currentSolution.getVrpProblem());
					routes.get(j).checkRoute(currentSolution.getVrpProblem());
					
					doTwoOPtInter(routes.get(i), routes.get(j), i, j, currentSolution.getVrpProblem());
				}
			}
		}
		/* re-calculate distance and time of solution */
		currentSolution.calculateDistanceAndTravelTime();
		
		System.out.println("change route");

		System.out.println(currentSolution.toString());
		
		
		System.out.println("--twoOptAlgorithm--");
	}

	public static void twoOptAlgorithmIntra(MySolution currentSolution, VrpProblem vrpProblem) {
		System.out.println("--twoOptAlgorithm--");
		ArrayList<Route> listOfRoute = currentSolution.getRouteList();
		int length = listOfRoute.size();

		for (int i = 0; i < length; i++) {
			doTwoOptIntra(vrpProblem, listOfRoute.get(i), i);
		}
		/* re-calculate distance and time of solution */
		currentSolution.calculateDistanceAndTravelTime();

		System.out.println("--twoOptAlgorithmIntra--");
	}

	public static void doTwoOPtInter(Route route1, Route route2, int index1, int index2, VrpProblem vrpProblem) {
		int min_i = -1;
		int min_j = -1;
		double minchange = 0;

		Route r1 = new Route(route1);
		Route r2 = new Route(route2);
		
		

		Route best_r1 = new Route(route1);
		Route best_r2 = new Route(route2);

		double best_distance_r1 = best_r1.getTotalDistance();
		double best_distance_r2 = best_r2.getTotalDistance();

		double new_distance_r1 = best_r1.getTotalDistance();
		double new_distance_r2 = best_r2.getTotalDistance();

		do {
			minchange = 0;
			int length1 = r1.getListOfDelivery().size() - 2;
			int length2 = route2.getListOfDelivery().size() - 2;

			for (int i = 1; i < length1; i++) {
				for (int j = 1; j < length2; j++) {

					/*
					 * edge_ii is distance between i vs i+1 in route 1 edge_jj
					 * is distance between j vs j+1 in route 2 edge_ij1 is
					 * distance between i in route 1 vs j+1 in route 2 edge_ji1
					 * is distance between j in route 2 vs i+1 in route 1
					 */
					Delivery delivery_i1 = (Delivery) r1.getListOfDelivery().get(i + 1);
					Delivery delivery_j1 = (Delivery) r2.getListOfDelivery().get(j + 1);
					Location location_i1 = delivery_i1.getLocationOfCustomer();
					Location location_j1 = delivery_j1.getLocationOfCustomer();

					Stage stage_i = (Stage) r1.getListOfStage().get(i);
					Stage stage_j = (Stage) r2.getListOfStage().get(j);

					double edge_ii = MyUtility.calculateDistance(r1.getListOfDelivery().get(i).getLocationOfCustomer(),
							r1.getListOfDelivery().get(i + 1).getLocationOfCustomer());
					double edge_jj = MyUtility.calculateDistance(r2.getListOfDelivery().get(j).getLocationOfCustomer(),
							r2.getListOfDelivery().get(j + 1).getLocationOfCustomer());
					double edge_ij1 = MyUtility.calculateDistance(r1.getListOfDelivery().get(i).getLocationOfCustomer(),
							r2.getListOfDelivery().get(j + 1).getLocationOfCustomer());

					double edge_ji1 = MyUtility.calculateDistance(r2.getListOfDelivery().get(j).getLocationOfCustomer(),
							r1.getListOfDelivery().get(i + 1).getLocationOfCustomer());

					double travelTime_ij1 = MyUtility.calculateTravelTime(edge_ij1, 1);
					double arrivalTime_ij1 = travelTime_ij1 + stage_i.getStartingTime();
					boolean checkTime_ij1 = arrivalTime_ij1 > delivery_j1.getTimewindowTo();

					double travelTime_ji1 = MyUtility.calculateTravelTime(edge_ji1, 1);
					double arrivalTime_ji1 = travelTime_ji1 + stage_j.getStartingTime();
					boolean checkTime_ji1 = arrivalTime_ji1 > delivery_i1.getTimewindowTo();

					double change = edge_ij1 + edge_ji1 - edge_ii - edge_jj;

					if (minchange > change && !checkTime_ij1 && !checkTime_ji1) {

						minchange = change;
						min_i = i;
						min_j = j;
					}
				}
			}
			if (min_i > 0 && min_j > 0) {
				// swap i+1 vs j+1
				boolean isSwap = isSwap(vrpProblem, r1, r2, min_i + 1, min_j + 1);
				if (isSwap) {
					
					r1.checkRoute(vrpProblem);
					r1.checkRoute(vrpProblem);
					
					swap2OptInter(r1, r2, min_i + 1, min_j + 1);

					System.out.println("---doTwoOPtInter---route " + index1 + " vs route " + index2);
					System.out.println("swapped location " + r1.getListOfDelivery().get(min_i + 1).getId() + " and "
							+ r2.getListOfDelivery().get(min_j + 1).getId());

					r1.calculateDemandAndDistance();
					r1.calculateStage();

					r2.calculateDemandAndDistance();
					r2.calculateStage();

					// TODO ERROR 
					System.out.println("check route after swap 2opt inter");
					r1.checkRoute(vrpProblem);
					r1.checkRoute(vrpProblem);
					
					
					new_distance_r1 = r1.getTotalDistance();
					new_distance_r2 = r2.getTotalDistance();

					if (new_distance_r1 < best_distance_r1 && new_distance_r2 < best_distance_r2) {
						
						System.out.println("tim duoc best roi");
						best_r1.setAttribute(r1);
						best_r2.setAttribute(r2);

						best_distance_r1 = best_r1.getTotalDistance();
						best_distance_r2 = best_r2.getTotalDistance();
						
						System.out.println("route " + index1 + " distance : "+ best_distance_r1);
						System.out.println("route " + index2 + " distance : "+ best_distance_r2);
					}else{
						System.out.println("nopeeeee");
						r1.setAttribute(best_r1);
						r2.setAttribute(best_r2);
					}

				} else {
					/*System.out.println("---doTwoOPtInter---route " + index1 + " vs route " + index2);
					System.out.println("ERORR!\nCan't swap location " + r1.getListOfDelivery().get(min_i + 1).getId()
							+ " and " + r2.getListOfDelivery().get(min_j + 1).getId());*/
					System.out.println(".....");
				}
				min_i = -1;
				min_j = -1;
				minchange = 0;
			}

		} while (minchange < 0);

		if (!route1.equals(best_r1) && !route2.equals(best_r2)) {
			route1.setAttribute(best_r1);
			route2.setAttribute(best_r2);
			
			System.out.println("check route after 2opt inter");
			route1.checkRoute(vrpProblem);
			route1.checkRoute(vrpProblem);
		}
	}

	public static void doTwoOptIntra(VrpProblem vrpInstance, Route route, int index) {
		Route bestRoute = new Route(route);
		Route newRoute = new Route(route);
		ArrayList<Delivery> listOfLocation = (ArrayList<Delivery>) bestRoute.getListOfDelivery().clone();
		int length = listOfLocation.size();
		boolean startAgain = false;
		double best_distance = bestRoute.getTotalDistance();
		double new_distance = 0;
		int improve = 0;
		int un_improve = 0;

		System.out.println("best distance of route " + index + " init : " + best_distance);
		do {
			best_distance = bestRoute.getTotalDistance();
			for (int i = 1; i < length - 2; i++) {
				for (int j = i + 1; j < length - 1; j++) {
					newRoute = new Route(swap2OptIntra(bestRoute, i, j, index));
					new_distance = newRoute.getTotalDistance();
					boolean checkRoute = newRoute.checkRoute(vrpInstance);
					if (new_distance < best_distance && checkRoute) {
						best_distance = new_distance;
						System.out.println("change new route");
						bestRoute = new Route(newRoute);
						improve++;
					}
					un_improve++;
				}
			}
			startAgain = un_improve < 100 ? true : false;

		} while (startAgain);
		System.out.println("best distance of route " + index + " after two opt " + improve + " (s) : " + best_distance);

	}

	/**
	 * check swap delivery (j+1,i+1) with j1= i+1 ; i1= i+1
	 * 
	 * @param vrpInstance
	 *            VrpProblem
	 * @param route1
	 *            first route
	 * @param route2
	 *            second route
	 * @param i1
	 * @param j1
	 * @return can Swap delivery(j1,i1) ???
	 */
	private static boolean isSwap(VrpProblem vrpInstance, Route route1, Route route2, int i1, int j1) {

		double kc_ii1 = MyUtility.calculateDistance(route1.getListOfDelivery().get(i1 - 1).getLocationOfCustomer(),
				route1.getListOfDelivery().get(i1).getLocationOfCustomer());
		double kc_i1i2 = MyUtility.calculateDistance(route1.getListOfDelivery().get(i1).getLocationOfCustomer(),
				route1.getListOfDelivery().get(i1 + 1).getLocationOfCustomer());

		double kc_jj1 = MyUtility.calculateDistance(route2.getListOfDelivery().get(j1 - 1).getLocationOfCustomer(),
				route2.getListOfDelivery().get(j1).getLocationOfCustomer());
		double kc_j1j2 = MyUtility.calculateDistance(route2.getListOfDelivery().get(j1).getLocationOfCustomer(),
				route2.getListOfDelivery().get(j1 + 1).getLocationOfCustomer());

		double kc_j1i2 = MyUtility.calculateDistance(route2.getListOfDelivery().get(j1).getLocationOfCustomer(),
				route1.getListOfDelivery().get(i1 + 1).getLocationOfCustomer());
		double kc_i1j2 = MyUtility.calculateDistance(route1.getListOfDelivery().get(i1).getLocationOfCustomer(),
				route2.getListOfDelivery().get(j1 + 1).getLocationOfCustomer());

		Route r1 = new Route(route1);
		Route r2 = new Route(route2);

		Stage stage_i = (Stage) r1.getListOfStage().get(i1 - 1);
		Stage stage_j = (Stage) r2.getListOfStage().get(j1 - 1);

		// delivery of customer at position i
		Delivery delivery_i = (Delivery) r1.getListOfDelivery().get(i1 - 1);
		Delivery delivery_j = (Delivery) r2.getListOfDelivery().get(j1 - 1);

		Delivery delivery_i1 = (Delivery) r1.getListOfDelivery().get(i1);
		Delivery delivery_i2 = (Delivery) r1.getListOfDelivery().get(i1 + 1);

		Delivery delivery_j1 = (Delivery) r2.getListOfDelivery().get(j1);
		Delivery delivery_j2 = (Delivery) r2.getListOfDelivery().get(j1 + 1);

		double currentDemand_r1 = r1.getTotalDemand();
		double currentDemand_r2 = r2.getTotalDemand();

		if (currentDemand_r1 + delivery_j1.getDemand() - delivery_i1.getDemand() > vrpInstance.getCapacityOfVehicle()) {
			System.out.println("Error : demand > capacity");
			return false;
		}
		if (currentDemand_r2 + delivery_i1.getDemand() - delivery_j1.getDemand() > vrpInstance.getCapacityOfVehicle()) {
			System.out.println("===>Error : demand > capacity");
			return false;
		}

		// check after swap i1 vs j1
		// i -> j+1 -> i+2
		// i -> j+1
		double dist_ij1 = MyUtility.calculateDistance((Location) delivery_i.getLocationOfCustomer(),
				(Location) delivery_j1.getLocationOfCustomer());
		double travelTime_ij1 = MyUtility.calculateTravelTime(dist_ij1, 1);
		double arrivalTime_ij1 = travelTime_ij1 + stage_i.getStartingTime();

		if (arrivalTime_ij1 > delivery_j1.getTimewindowTo()) {
			return false;
		}
		double issuingTime_ij1 = 0;
		if (arrivalTime_ij1 <= delivery_j1.getTimewindowFrom()) {
			issuingTime_ij1 = delivery_j1.getTimewindowFrom();
		} else {
			issuingTime_ij1 = arrivalTime_ij1;
		}

		// j+1 -> i+2
		double dist_j1i2 = MyUtility.calculateDistance((Location) delivery_j1.getLocationOfCustomer(),
				(Location) delivery_i2.getLocationOfCustomer());
		double travelTime_j1i2 = MyUtility.calculateTravelTime(dist_j1i2, 1);
		double arrivalTime_j1i2 = travelTime_j1i2 + issuingTime_ij1 + delivery_j1.getServiceTime();

		if (arrivalTime_j1i2 > delivery_i2.getTimewindowTo()) {
			return false;
		}
		double issuingTime_j1i2 = 0;
		if (arrivalTime_j1i2 <= delivery_i2.getTimewindowFrom()) {
			issuingTime_j1i2 = delivery_i2.getTimewindowFrom();
		} else {
			issuingTime_j1i2 = arrivalTime_j1i2;
		}

		// dist_ji1 is distance between j vs i+1
		// j -> i+1 -> j+2
		// j -> i+1
		double dist_ji1 = MyUtility.calculateDistance(delivery_j.getLocationOfCustomer(),
				delivery_i1.getLocationOfCustomer());
		double travelTime_ji1 = MyUtility.calculateTravelTime(dist_ji1, 1);
		double arrivalTime_ji1 = travelTime_ji1 + stage_j.getStartingTime();
		if (arrivalTime_ji1 > delivery_i1.getTimewindowTo()) {
			return false;
		}
		double issuingTime_ji1 = 0;
		if (arrivalTime_ji1 <= delivery_i1.getTimewindowFrom()) {
			issuingTime_ji1 = delivery_i1.getTimewindowFrom();
		} else {
			issuingTime_ji1 = arrivalTime_ji1;
		}

		// i+1 -> j+2
		double dist_i1j2 = MyUtility.calculateDistance(delivery_i1.getLocationOfCustomer(),
				delivery_j2.getLocationOfCustomer());
		double travelTime_i1j2 = MyUtility.calculateTravelTime(dist_i1j2, 1);
		double arrivalTime_i1j2 = travelTime_i1j2 + issuingTime_ji1 + delivery_i1.getServiceTime();
		if (arrivalTime_i1j2 > delivery_j2.getTimewindowTo()) {
			return false;
		}
		double issuingTime_i1j2 = 0;
		if (arrivalTime_i1j2 <= delivery_j2.getTimewindowFrom()) {
			issuingTime_i1j2 = delivery_j2.getTimewindowFrom();
		} else {
			issuingTime_i1j2 = arrivalTime_i1j2;
		}

		// calculate time
		double currentTotalTravelTime_ij1 = issuingTime_j1i2 + delivery_i2.getServiceTime();
		double currentArrivelTime = 0;
		double currentIssuingTime = 0;
		for (int k = i1 + 1; k < r1.getListOfStage().size(); k++) {

			Stage stageTemp = r1.getListOfStage().get(k);
			Delivery deliveryTemp = r1.getListOfDelivery().get(k + 1);
			if ((currentTotalTravelTime_ij1 + stageTemp.getTravelTime()) > deliveryTemp.getTimewindowTo()) {
				return false;
			} else {

				currentArrivelTime = currentTotalTravelTime_ij1 + stageTemp.getTravelTime();
				if (currentArrivelTime < deliveryTemp.getTimewindowFrom()) {
					currentIssuingTime = deliveryTemp.getTimewindowFrom();
				} else {
					currentIssuingTime = currentArrivelTime;
				}
				currentTotalTravelTime_ij1 = currentIssuingTime + deliveryTemp.getServiceTime();
			}
		}

		// calculate time
		double currentTotalTravelTime_ji1 = issuingTime_i1j2 + delivery_j2.getServiceTime();

		currentArrivelTime = 0;
		currentIssuingTime = 0;
		for (int k = j1 + 1; k < r2.getListOfStage().size(); k++) {
			Stage stageTemp = (Stage) r2.getListOfStage().get(k);
			Delivery deliveryTemp = (Delivery) r2.getListOfDelivery().get(k + 1);
			if ((currentTotalTravelTime_ji1 + stageTemp.getTravelTime()) > deliveryTemp.getTimewindowTo()) {
				return false;
			} else {

				currentArrivelTime = currentTotalTravelTime_ji1 + stageTemp.getTravelTime();
				if (currentArrivelTime < deliveryTemp.getTimewindowFrom()) {
					currentIssuingTime = deliveryTemp.getTimewindowFrom();
				} else {
					currentIssuingTime = currentArrivelTime;
				}
				currentTotalTravelTime_ji1 = currentIssuingTime + deliveryTemp.getServiceTime();
			}
		}
		return true;
	}

	private static void swap2OptInter(Route route1, Route route2, int i, int j) {
		
		Delivery delivery_i = (Delivery) route1.getListOfDelivery().get(i);
		Delivery delivery_j = (Delivery) route2.getListOfDelivery().get(j);

		Delivery delivery_temp = new Delivery(delivery_i);

		delivery_i.setAttribute(delivery_j);
		delivery_j.setAttribute(delivery_temp);
		
		route1.calculateDemandAndDistance();
		route1.calculateStage();
		
		route2.calculateDemandAndDistance();
		route2.calculateStage();
		

	}

	private static Route swap2OptIntra(Route route, int i, int j, int index) {
		/**
		 * 2optSwap(route, i, k) { 1. take route[1] to route[i-1] and add them
		 * in order to new_route 2. take route[i] to route[k] and add them in
		 * reverse order to new_route 3. take route[k+1] to end and add them in
		 * order to new_route return new_route; }
		 */

		ArrayList<Delivery> listOfDelivery = (ArrayList<Delivery>) route.getListOfDelivery().clone();

		ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

		System.out.println("-----Starting.....Swap-----");

		// Step 1. take route[1] to route[i-1] and add them in order to new_route
		if (i - 1 >= 0) {
			for (int k = 0; k < i; k++) {
				deliveries.add(listOfDelivery.get(k));
			}
		}
		/*Step 2. take route[i] to route[k] and add them in reverse order to new_route*/
		if (j < listOfDelivery.size() - 1) {
			for (int k = j; k >= i; k--) {

				deliveries.add(listOfDelivery.get(k));
			}
		}
		/*Step 3. take route[k+1] to end and add them in order to new_route return new_route;*/
		if (j + 1 < listOfDelivery.size()) {
			for (int k = j + 1; k < listOfDelivery.size(); k++) {

				deliveries.add(listOfDelivery.get(k));
			}
		}
		
		System.out.println("Routes " + index + " swap i vs j ( " + i + ", " + j + " )");
		
		route.setListOfDelivery(deliveries);

		Route newRoute = new Route(route);

		newRoute.calculateDemandAndDistance();
		newRoute.calculateStage();

		System.out.println("-----End Swap-----");

		return newRoute;
	}

}
