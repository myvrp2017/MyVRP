package core.model;

import java.util.ArrayList;

import core.util.MyUtility;

public class Route {
	private ArrayList<Delivery> listOfDelivery;
	private double totalDemand;
	private double totalDistance;
	private double totalTravelTime;
	private ArrayList<Stage> listOfStage;

	public Route() {

	}

	public Route(ArrayList<Delivery> listOfDelivery, double totalDemand, double totalDistance, double totalTravelTime,
			ArrayList<Stage> listOfStage) {
		this.listOfDelivery = listOfDelivery;
		this.totalDemand = totalDemand;
		this.totalDistance = totalDistance;
		this.totalTravelTime = totalTravelTime;
		this.listOfStage = listOfStage;
	}

	@SuppressWarnings("unchecked")
	public Route(Route route) {
		this.listOfDelivery = (ArrayList<Delivery>) route.getListOfDelivery().clone();
		this.listOfStage = (ArrayList<Stage>) route.getListOfStage().clone();
		this.totalDemand = route.totalDemand;
		this.totalDistance = route.totalDistance;
		this.totalTravelTime = route.totalTravelTime;
	}

	public ArrayList<Delivery> getListOfDelivery() {
		return listOfDelivery;
	}

	public void setListOfDelivery(ArrayList<Delivery> listOfDelivery) {
		this.listOfDelivery = listOfDelivery;
	}

	public double getTotalDemand() {
		return totalDemand;
	}

	public void setTotalDemand(double totalDemand) {
		this.totalDemand = totalDemand;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public double getTotalTravelTime() {
		return totalTravelTime;
	}

	public void setTotalTravelTime(double totalTravelTime) {
		this.totalTravelTime = totalTravelTime;
	}

	public ArrayList<Stage> getListOfStage() {
		return listOfStage;
	}

	public void setListOfStage(ArrayList<Stage> listOfStage) {
		this.listOfStage = listOfStage;
	}

	public boolean checkRoute(VrpProblem bmInstance) {
		// check total quantity with capacity
		if (this.totalDemand > bmInstance.getCapacityOfVehicle()) {
			return false;
		}
		// Check local stage error with time window
		for (int j = 0; j < this.listOfStage.size(); j++) {
			Stage st = (Stage) this.listOfStage.get(j);
			Delivery delivery = (Delivery) this.listOfDelivery.get(j + 1);
			// Check time window of each location
			if (st.getArrivingTime() > delivery.getTimewindowTo()) {
				System.out.println("Time window error at delivery:" + delivery.getId());
				System.out.println("Arriving time: " + st.getArrivingTime() + " service time:"
						+ delivery.getServiceTime() + " max late: " + delivery.getTimewindowTo());
				return false;
			}
			if (st.getStartingTime() > st.getArrivingTime() || st.getArrivingTime() > st.getIssuingTime()
					|| st.getIssuingTime() > st.getEndTime()) {
				System.out.println("Error at route -time error in local stage: " + j);
				return false;
			}

		}
		// Check 2 adjecnt stages with the end time and starting time
		for (int j = 0; j < this.listOfStage.size() - 1; j++) {
			Stage st = (Stage) this.listOfStage.get(j);
			Stage st2 = (Stage) this.listOfStage.get(j + 1);
			if (st.getEndTime() > st2.getStartingTime()) {
				System.out.println("Error at route- in multiple stage: " + j);
				System.out.println(
						"End time of previous route: " + st.getEndTime() + " Starting time: " + st2.getStartingTime());
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Route clone() {
		Route route = new Route();
		route.setListOfDelivery((ArrayList<Delivery>) this.listOfDelivery.clone());
		route.setListOfStage((ArrayList<Stage>) this.listOfStage.clone());
		route.setTotalDemand(this.totalDemand);
		route.setTotalDistance(this.totalDistance);
		route.setTotalTravelTime(this.totalTravelTime);
		return route;
	}

	public boolean equals(Route route) {
		if (route.totalDemand != this.totalDemand) {
			return false;
		}
		if (route.totalDistance != this.totalDistance) {
			return false;
		}
		if (route.totalTravelTime != this.totalTravelTime) {
			return false;
		}
		for (int i = 0; i < this.listOfDelivery.size(); i++) {
			if (this.listOfDelivery.get(i).getId() != route.getListOfDelivery().get(i).getId()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(Route r) {
		this.listOfDelivery = (ArrayList<Delivery>) r.listOfDelivery.clone();
		this.listOfStage = (ArrayList<Stage>) r.listOfStage.clone();
		this.totalDemand = r.totalDemand;
		this.totalDistance = r.totalDistance;
		this.totalTravelTime = r.totalTravelTime;
	}

	@Override
	public String toString() {
		String out = "TotalDemand=" + totalDemand + ", totalDistance=" + totalDistance + ", totalTravelTime="
				+ totalTravelTime + ",\nroute :";
		for (Delivery delivery : this.listOfDelivery) {
			out += delivery.getId() + "->";
		}
		out = out.substring(0, out.length() - 2);
		return out;
	}

	public void calculateStage() {
		listOfStage.clear();
		for (int i = 0; i < listOfDelivery.size() - 1; i++) {
			Stage stage = new Stage();
			Stage stagePrevious = new Stage();
			// delivery1 -> delivery2
			Delivery delivery1 = listOfDelivery.get(i);
			Delivery delivery2 = listOfDelivery.get(i + 1);
			Location departPoint = delivery1.getLocationOfCustomer();
			Location destinationPoint = delivery2.getLocationOfCustomer();
			double distance = MyUtility.calculateDistance(departPoint, destinationPoint);

			double travelTime = MyUtility.calculateTravelTime(distance, 1);
			double arrivingTime = delivery1.getTimewindowFrom() + travelTime;
			double distanceFromDepot = MyUtility.calculateDistance(destinationPoint, delivery2.getLocationOfDepot());

			double issuingTime = 0.00;
			double startingTime = 0.00;
			double endTime = 0.00;
			
			int previous = i - 1;
			if (previous < 0) {
				startingTime = 0.00;
			} else {
				stagePrevious = listOfStage.get(i - 1);
				startingTime = stagePrevious.getEndTime();
				arrivingTime = startingTime + travelTime;
			}
			

			if (arrivingTime < delivery2.getTimewindowFrom())
				issuingTime = delivery2.getTimewindowFrom();
			else
				issuingTime = arrivingTime;
			
			endTime = issuingTime + delivery2.getServiceTime();

			

			stage = new Stage(departPoint, destinationPoint, startingTime, arrivingTime, issuingTime, endTime, distance,
					travelTime, distanceFromDepot);
			listOfStage.add(stage);
		}
		/*System.out.println(this.listOfDelivery.size());
		System.out.println(listOfStage.size());*/
	}

	public void calculateDemandAndDistance() {
		double totalDemand = 0.00;
		double totalDistance = 0.00;
		double startTime = 0;
		for (int i = 0; i < this.listOfDelivery.size() - 1; i++) {
			double distance = MyUtility.calculateDistance(listOfDelivery.get(i).getLocationOfCustomer(),
					listOfDelivery.get(i + 1).getLocationOfCustomer());
			double travelTime = MyUtility.calculateTravelTime(distance, 1);
			double arrivalTime = travelTime + startTime;

			double issuTime = 0;
			if (arrivalTime <= ((Delivery) this.listOfDelivery.get(i + 1)).getTimewindowFrom()) {
				issuTime = ((Delivery) this.listOfDelivery.get(i + 1)).getTimewindowFrom();
			} else {
				issuTime = arrivalTime;
			}
			startTime = issuTime + ((Delivery) this.listOfDelivery.get(i + 1)).getServiceTime();
			totalDemand += (double) ((Delivery) this.listOfDelivery.get(i)).getDemand();
			totalDistance += distance;
		}

		this.totalDistance = totalDistance;
		this.totalDemand = totalDemand;
		this.totalTravelTime = startTime;

	}

}
