package core.algorithm;

import core.model.Delivery;
import core.model.Route;
import core.ts.MySolution;
import core.util.MyUltility;

public class TwoOpt {
	/*
	 * main idea : find pair i vs j after that . check it's rule to insert i between i vs i+1
	 * -> insert j+1 after i_1 
	 */
	public static void runTwoOPt(MySolution mySolution,Route route1, Route route2) {
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
					double edge_ij = MyUltility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCus(), route2.getListOfDelivery().get(j).getLocationOfCus());
					double edge_ij1 = MyUltility.calculateDistance(route1.getListOfDelivery().get(i + 1).getLocationOfCus(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCus());
					double edge_ii = MyUltility.calculateDistance(route1.getListOfDelivery().get(i).getLocationOfCus(),
							route1.getListOfDelivery().get(i + 1).getLocationOfCus());
					double edge_jj = MyUltility.calculateDistance(route2.getListOfDelivery().get(j).getLocationOfCus(),
							route2.getListOfDelivery().get(j + 1).getLocationOfCus());

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

				
				 //check before insert
				 
				  boolean isInsertable = mySolution.isFeasible(route1, route2,customer_j, min_i + 1, min_j);
				 
				route1.getListOfDelivery().add(min_i + 1, customer_j);
				route2.getListOfDelivery().remove(customer_j);
				/*
				 * isInsertable = isInsertable(route1, route2, customer_j1,
				 * min_i + 3, min_j);
				 */
				route1.getListOfDelivery().add(min_i + 3, customer_j1);
				route2.getListOfDelivery().remove(customer_j1);

				/*
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 */
				System.out.println("------");
			}

		} while (minchange < 0);
	}
}
