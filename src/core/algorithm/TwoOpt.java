package core.algorithm;

import core.model.Delivery;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;
import core.ts.MySolution;
import core.util.MyUtility;

public class TwoOpt {
	/*
	 * main idea : find pair i vs j after that . check it's rule to insert i
	 * between i vs i+1 -> insert j+1 after i_1
	 */
	public static void runTwoOPt(VrpProblem vrpProblem, Route route1, Route route2) {
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
					double edge_ij = MyUtility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCus(),
							route2.getListOfDelivery().get(j).getLocationOfCus());
					double edge_ij1 = MyUtility.calculateDistance(
							route1.getListOfDelivery().get(i + 1).getLocationOfCus(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCus());
					double edge_ii = MyUtility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCus(),
							route1.getListOfDelivery().get(i + 1).getLocationOfCus());
					double edge_jj = MyUtility.calculateDistance(route2.getListOfDelivery().get(j).getLocationOfCus(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCus());

					double change = edge_ij + edge_ij1 - edge_ii - edge_jj;

					if (minchange > change) {
						minchange = change;
						min_i = i;
						min_j = j;
					}
				}
			}
			if (min_i > 0 && min_j > 0 && min_j < route2.getListOfDelivery().size() - 1) {
				// insert Node j and j+1 into route1 route1 : 0...i j i+1 j+1...
				Delivery customer_j = route2.getListOfDelivery().get(min_j);
				Delivery customer_j1 = route2.getListOfDelivery().get(min_j + 1);

				// insert j and j+1 to route 1

				// check delivery of customer_j can be inserted into route 1
				// between i and i+1
				boolean isInsertable = isInsertable(vrpProblem.getCapacityOfVehicle(), route1, route2, customer_j, min_i, min_j);
				if (isInsertable) {
					route1.getListOfDelivery().add(min_i + 1, customer_j);
					route2.getListOfDelivery().remove(customer_j);
				}

				// check delivery of customer_j1 can be inserted into route 1
				// between i+2 and i+3
				isInsertable = isInsertable(vrpProblem.getCapacityOfVehicle(), route1, route2, customer_j1, min_i + 2, min_j);
				if (isInsertable) {
					route1.getListOfDelivery().add(min_i + 3, customer_j1);
					route2.getListOfDelivery().remove(customer_j1);
				}

				/*
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 */
				System.out.println("------");
			}

		} while (minchange < 0);
	}

	// check insertable when insert devlivery customer_j between i and i+1
	// j is position of delivery-customer_j in route2
	private static boolean isInsertable(double Capacity, Route route1, Route route2, Delivery customer_j, int i,
			int j) {
		Route r1 = route1.clone();
		Route r2 = route2.clone();

		Stage stage_i = new Stage();
		stage_i = r1.getListOfStage().get(i);

		// delivery of customer at position i
		Delivery customer_i = r1.getListOfDelivery().get(i);
		double currentDemand = r1.getTotalDemand();

		if (currentDemand + customer_j.getDemand() > Capacity)
			return false;

		// check when insert customer_j into route between i vs i+1
		double dist_ij = MyUtility.calculateDistance(customer_i.getLocationOfCus(), customer_j.getLocationOfCus());
		double travelTime_ij = MyUtility.calculateTravelTime(dist_ij, 1);
		double arrivalTime_ij = travelTime_ij + stage_i.getStartingTime();
		
		if (arrivalTime_ij > customer_j.getTimewindowTo())
			return false;
		double issuingTime_ij = 0;
		if (arrivalTime_ij <= customer_j.getTimewindowFrom())
			issuingTime_ij = customer_j.getTimewindowFrom();
		else
			issuingTime_ij = arrivalTime_ij;
		
		// delivery of customer at position i+1
		Delivery customer_i1 = r1.getListOfDelivery().get(i+1);
		//dist_ji1 is distance between j vs i+1
		double dist_ji1 = MyUtility.calculateDistance(customer_j.getLocationOfCus(),customer_i1.getLocationOfCus());
		double travelTime_ji1 = MyUtility.calculateTravelTime(dist_ji1, 1);
		double arrivalTime_ji1 = travelTime_ji1 + issuingTime_ij + customer_j.getServiceTime();
		if (arrivalTime_ji1 > customer_i1.getTimewindowTo())
			return false;
		double issuingTime_ji1 = 0;
		if (arrivalTime_ji1 <= customer_i1.getTimewindowFrom())
			issuingTime_ji1 = customer_j.getTimewindowFrom();
		else
			issuingTime_ji1 = arrivalTime_ji1;
		
		
		Stage S_ij = new Stage(stage_i.getDepartPoint(),customer_j.getLocationOfCus(),stage_i.getStartingTime(),
				arrivalTime_ij,issuingTime_ij,issuingTime_ij + customer_j.getServiceTime(),
				dist_ij,travelTime_ij,stage_i.getDistanceFromDepot() + dist_ij);
		Stage S_ji1 = new Stage(customer_j.getLocationOfCus(),stage_i.getDestinationPoint(),issuingTime_ij+ customer_j.getServiceTime(),
				arrivalTime_ji1,issuingTime_ji1,issuingTime_ji1 + customer_i1.getServiceTime(),
				dist_ji1,travelTime_ji1,stage_i.getDistanceFromDepot() + dist_ij+ dist_ji1);
		
		
		// calculate time after insert customer_j between i vs i+1
		double currentTotalTravelTime = issuingTime_ji1 + customer_j.getServiceTime();
		for (int k = i; k < r1.getListOfStage().size(); k++) {
			Stage stageTemp = r1.getListOfStage().get(k);
			Delivery stageTemp2 = r1.getListOfDelivery().get(k + 1);
			if ((currentTotalTravelTime + stageTemp.getTravelTime()) > stageTemp2.getTimewindowTo())
				return false;
			Stage updateStage = new Stage(stageTemp.getDepartPoint(), stageTemp.getDestinationPoint(), currentTotalTravelTime, stageTemp2);
			r1.getListOfStage().remove(j);
			r1.getListOfStage().add(j, updateStage);
			currentTotalTravelTime = updateStage.getEndTime();
		}
		
		// TODO fix thêm phần cập nhật route r1 và r2
		
		//add customer_j into r1 at the position i+1
		r1.getListOfDelivery().add(i + 1, customer_j);
		r1.setTotalDemand(r1.getTotalDemand() + customer_j.getDemand());
		r1.setTotalDistance(r1.getTotalDistance() - stage_i.getDistance() + dist_ij + dist_ji1);
		r1.setTotalTravelTime(currentTotalTravelTime);
		r1.getListOfStage().remove(i);
		r1.getListOfStage().add(i,S_ij);
		r1.getListOfStage().add(i + 1, S_ji1);
		
		
		return false;
	}
}
