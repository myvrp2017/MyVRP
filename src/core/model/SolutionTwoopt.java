package core.model;

import java.util.ArrayList;
import java.util.Collection;

import util.MyUltility;

public class SolutionTwoopt {

	private FeasibleSolution feasibleSolution;
	private int cost;
	private double fitness = 0.0;
	private ArrayList<Customer> customerInSolution;
	private VrpProblem vrpProblem;

	public SolutionTwoopt() {
		feasibleSolution = new FeasibleSolution();
		customerInSolution = new ArrayList<>();

		vrpProblem = new VrpProblem();
		/**
		 * custom fix this cost for maximum
		 */
		cost = Integer.MAX_VALUE;
	}

	public VrpProblem getVrpProblem() {
		return vrpProblem;
	}

	public void setVrpProblem(VrpProblem vrpProblem) {
		this.vrpProblem = vrpProblem;
	}

	public void setCustomerInSolution(ArrayList<Customer> customerInSolution) {
		this.customerInSolution = customerInSolution;
	}

	public FeasibleSolution getFeasibleSolution() {
		return feasibleSolution;
	}

	public void setFeasibleSolution(FeasibleSolution feasibleSolution) {
		this.feasibleSolution = feasibleSolution;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public ArrayList<Customer> getCustomerInSolution() {
		return customerInSolution;
	}

	public void setCustomersInSolution(ArrayList<Customer> customersInSolution) {
		this.customerInSolution = customersInSolution;
	}

	public void repairSolution() {
		// TODO Auto-generated method stub
		FeasibleSolution feasibleSolution = this.feasibleSolution;

		ArrayList<Route> routes = feasibleSolution.getRoutes();
		int numOfRoute = routes.size();

		ArrayList<Route> newRoutes = new ArrayList<Route>();

		for (int k = 0; k < 1000; k++) {
			for (int i = 0; i < numOfRoute - 1; i++) {
				for (int j = i + 1; j < numOfRoute; j++) {
					// newRoutes.addAll(twoOpt(routes.get(i),routes.get(j)));
					// change = MyUltility.getDistance(routes.get(i),
					// routes.get(j)) + dist(i + 1, j + 1) - dist(i, i + 1) -
					// dist(j, j + 1);
					twoOpt(routes.get(i), routes.get(j));
				}
			}
		}
		/*
		 * for (int i = 0; i < feasibleSolution.getRoutes().size(); i++) {
		 * if(routes.get(i).sizeofRoute() == 1){
		 * feasibleSolution.getRoutes().remove(i); }else{ continue; } }
		 */

		for (Route route : feasibleSolution.getRoutes()) {
			if (route.sizeofRoute() == 1) {
				continue;
			} else {
				newRoutes.add(route);
			}
		}
		feasibleSolution.setRoutes(newRoutes);
	}

	/**
	 * idea : in route1 choose a location(i) ...find in route2 location(j)
	 * remove edge i_i+1 vs j_j+1 by edge i_j and i+1_j+1
	 * check constraint before iinsert location j,j+1 into route1  
	 * @param route1
	 * @param route2
	 */
	public void twoOpt(Route route1, Route route2) {
		int min_i = -1;
		int min_j = -1;
		double minchange;
		do {
			minchange = 0.00;
			for (int i = 1; i < route1.sizeofRoute() - 1; i++) {
				for (int j = 1; j < route2.sizeofRoute() - 1; j++) {
					/*
					 * edge_ij is distance between i vs j between route 1 & 2
					 * edge_ij1 is distance between i+1 vs j+1 between route 1 &
					 * 2 edge_ii is distance between i vs i+1 of route1 edge_jj
					 * is distance between j vs j+1 of route2
					 */
					double edge_ij = MyUltility.getDistance(route1.getCustomers().get(i), route2.getCustomers().get(j));
					double edge_ij1 = MyUltility.getDistance(route1.getCustomers().get(i + 1),
							route2.getCustomers().get(j + 1));
					double edge_ii = MyUltility.getDistance(route1.getCustomers().get(i),
							route1.getCustomers().get(i + 1));
					double edge_jj = MyUltility.getDistance(route2.getCustomers().get(j),
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

				boolean isInsertable = isInsertable(route1, route2, customer_j, min_i + 1, min_j);
				if (isInsertable) {
					route1.addCustomer(customer_j, min_i + 1);
					route2.removeCustomer(customer_j);
				}
				isInsertable = isInsertable(route1, route2, customer_j1, min_i + 3, min_j);
				if (isInsertable) {
					route1.addCustomer(customer_j1, min_i + 3);
					route2.removeCustomer(customer_j1);
				}

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
	public boolean isInsertable(Route route1, Route route2, Customer customer, int i, int j) {
		Route r1 = (Route) route1.clone();
		Route r2 = (Route) route2.clone();

		if (r1.getTotalDemand() + customer.getDemand() > this.vrpProblem.getCapacity())
			return false;
		return true;

		/*
		 * double edge_ij = MyUltility.getDistance(r1.getCustomers().get(i),
		 * r2.getCustomers().get(j)); //spped of the vehicle set to 1; // t =
		 * V/S int speed = 1; double travel_time = edge_ij / speed ;
		 */

	}

	@Override
	public String toString() {
		String out = "";
		int index = 0;
		for (Route route : this.feasibleSolution.getRoutes()) {
			out += "Route " + ++index + " : ";
			out += route.toString();
			out += "Total demand : " + route.getTotalDemand() + "\n";
		}
		return out;
	}

}
