package core.algorithm;

import core.model.Route;
import core.util.MyUltility;
import model.Customer;

public class TwoOpt {
	public static void Run(Route route1, Route route2) {
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
					double edge_ij = MyUltility.calculateDistance(route1.getCustomers().get(i), route2.getCustomers().get(j));
					double edge_ij1 = MyUltility.calculateDistance(route1.getCustomers().get(i + 1),
							route2.getCustomers().get(j + 1));
					double edge_ii = MyUltility.calculateDistance(route1.getCustomers().get(i),
							route1.getCustomers().get(i + 1));
					double edge_jj = MyUltility.calculateDistance(route2.getCustomers().get(j),
							route2.getCustomers().get(j + 1));

					double change = edge_ij + edge_ij1 - edge_ii - edge_jj;

					if (minchange > change) {
						minchange = change;
						min_i = i;
						min_j = j;
					}
				}
			}
			// TODO check max route time and the other constraints
			if (min_i > 0 && min_j > 0 && min_j < route2.sizeofRoute() - 1) {
				// insert Node j and j+1 into route1 route1 : 0...i j i+1 j+1...
				Customer customer_j = route2.getCustomer(min_j);
				Customer customer_j1 = route2.getCustomer(min_j + 1);

				// insert j and j+1 to route 1

				/*
				 * check before insert
				 * 
				 * boolean isInsertable = isInsertable(route1, route2,
				 * customer_j, min_i + 1, min_j);
				 */
				route1.addCustomer(customer_j, min_i + 1);
				route2.removeCustomer(customer_j);
				/*
				 * isInsertable = isInsertable(route1, route2, customer_j1,
				 * min_i + 3, min_j);
				 */
				route1.addCustomer(customer_j1, min_i + 3);
				route2.removeCustomer(customer_j1);

				/*
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 * route1.setTotalDemand(route1.calculateTotalDemand());
				 */
				System.out.println("2-opt");
				route1.printRoute();
				route2.printRoute();
				System.out.println("------");
			}

		} while (minchange < 0);
	}
}
