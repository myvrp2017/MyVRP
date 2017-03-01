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
	public void setAttribute(Route r){
		this.listOfDelivery= (ArrayList) r.listOfDelivery.clone();
		this.listOfStage= (ArrayList) r.listOfStage.clone();
		this.totalDemand = r.totalDemand;
		this.totalDistance = r.totalDistance;
		this.totalTravelTime = r.totalTravelTime;
	}
	@Override
	public String toString() {
		String out = "TotalDemand=" + totalDemand + ", totalDistance="
				+ totalDistance + ", totalTravelTime=" + totalTravelTime + ",\nroute :";
		for(Delivery delivery : this.listOfDelivery){
			out += delivery.getId() + "->";
		}
		out = out.substring(0, out.length()-2);
		return out;
	}
	public void calculateStage(){
		listOfStage.clear();
		for(int i = 0 ; i < listOfDelivery.size() -1 ; i++){
			Stage stage = new Stage();
			Stage stagePrevious = new Stage();
			// delivery1 -> delivery2
			Delivery delivery1 = listOfDelivery.get(i);
			Delivery delivery2 = listOfDelivery.get(i+1);
			Location departPoint = delivery1.getLocationOfCustomer();
			Location destinationPoint = delivery2.getLocationOfCustomer();
			double distance = MyUtility.calculateDistance(departPoint, destinationPoint);
			
			double travelTime = MyUtility.calculateTravelTime(distance, 1);
			double arrivingTime = delivery1.getTimewindowFrom() + travelTime;
			double distanceFromDepot = MyUtility.calculateDistance(destinationPoint, delivery2.getLocationOfDepot());
			
			
			double issuingTime = 0.00;
			double startingTime = 0.00;
			double endTime = 0.00;
			
			if(arrivingTime < delivery2.getTimewindowFrom())
				issuingTime = delivery2.getTimewindowFrom();
			else
				issuingTime = arrivingTime;
			endTime = issuingTime + delivery2.getServiceTime();
			
			int previous = i - 1;
			if(previous < 0){
				startingTime = 0.00;
			}else{
				stagePrevious = listOfStage.get(i-1);
				startingTime = stagePrevious.getEndTime();
				arrivingTime += startingTime;
			}
			
			stage = new Stage(departPoint,destinationPoint,
					startingTime,arrivingTime,issuingTime,endTime,distance,travelTime,distanceFromDepot);
			listOfStage.add(stage);
		}
		System.out.println(this.listOfDelivery.size());
		System.out.println(listOfStage.size());
	}
	
}
