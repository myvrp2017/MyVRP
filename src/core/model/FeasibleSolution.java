package core.model;

import java.util.ArrayList;

public class FeasibleSolution {

	private ArrayList<Route> routes;

	public FeasibleSolution() {
		this.routes = new ArrayList<>();
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(ArrayList<Route> routes) {
		this.routes = routes;
	}

	public double getLatestTime() {

		double latest = 0;

		for (Route route : routes) {
			if (route.getTotalTime() > latest) {
				latest = route.getTotalTime();
			}
		}

		return latest;
	}

	public double getTotalTime() {
		double total = 0.00;

		for (Route route : routes) {
			total += route.getTotalTime();
		}
		return total;
	}
	public double getTotalDistance() {
		double total = 0.00;

		for (Route route : routes) {
			total += route.calculateTotalDistance();
		}
		return total;
	}

	public ArrayList<Customer> getAllCustomers() {

		ArrayList<Customer> customers = new ArrayList<>();
		ArrayList<Customer> customers2 = new ArrayList<>();

		for (Route route : routes) {
			customers.addAll(route.getCustomers());
		}

		for (Customer location : customers) {
			if (location.getId() != 0) {
				customers2.add(location);
			}
		}

		return customers2;

	}

	public void exportRoute() {
		for (Route route : this.routes) {
			System.out.print("\nRoute : ");
			for (Customer customer : route.getCustomers()) {
				System.out.print(customer.getId() + " - ");
			}
		}
	}

}
