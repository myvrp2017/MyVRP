package core.model;

import java.util.ArrayList;

public class Route {
	private ArrayList<Delivery> listOfDelivery;
	private double totalDemand;
	private double totalDistance;
	private double totalTravelTime;
	private ArrayList<Stage> listOfStage;
	
	public Route(ArrayList<Delivery> listOfDelivery, double totalDemand, double totalDistance, double totalTravelTime) {
		this.listOfDelivery = listOfDelivery;
		this.totalDemand = totalDemand;
		this.totalDistance = totalDistance;
		this.totalTravelTime = totalTravelTime;
	}
	public Route(ArrayList<Delivery> listOfDelivery, double totalDemand, double totalDistance, double totalTravelTime,
			ArrayList<Stage> listOfStage) {
		this.listOfDelivery = listOfDelivery;
		this.totalDemand = totalDemand;
		this.totalDistance = totalDistance;
		this.totalTravelTime = totalTravelTime;
		this.listOfStage = listOfStage;
	}
	public Route(){}

	public Route(Route r){
		this.listOfDelivery = (ArrayList<Delivery>) r.listOfDelivery.clone();
		this.listOfStage = (ArrayList<Stage>) r.listOfStage.clone();
		this.totalDemand = r.totalDemand;
		this.totalDistance = r.totalDistance;
		this.totalTravelTime = r.totalTravelTime;
	}
	public void setAttribute(Route r){
		this.listOfDelivery= (ArrayList) r.listOfDelivery.clone();
		this.listOfStage= (ArrayList) r.listOfStage.clone();
		this.totalDemand = r.totalDemand;
		this.totalDistance = r.totalDistance;
		this.totalTravelTime = r.totalTravelTime;
	}
	public ArrayList<Stage> getListOfStage() {
		return listOfStage;
	}
	public void setListOfStage(ArrayList<Stage> listOfStage) {
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
	
	
}
