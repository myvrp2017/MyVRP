package core.model;

import core.util.MyUtility;

public class Stage {
	private Location departPoint;
	private Location destinationPoint;
	private double startingTime;
	// after doing the unloading at previous location

	private double arrivingTime;
	// reaching time to the destination

	private double issuingTime;
	// time of during issuing when shipping early than the timeWindow

	private double endTime;
	// issuing Time + service time

	private double distance;

	private double travelTime;
	// duration from depart location -> destination location only without
	// service time consideration

	private double distanceFromDepot;
	
	public Stage() {
		super();
	}

	public Stage(Location departPoint, Location destinationPoint, double startingTime, double arrivingTime,
			double issuingTime, double endTime, double distance, double travelTime, double distanceFromDepot) {
		this.departPoint = departPoint;
		this.destinationPoint = destinationPoint;
		this.startingTime = startingTime;
		this.arrivingTime = arrivingTime;
		this.issuingTime = issuingTime;
		this.endTime = endTime;
		this.distance = distance;
		this.travelTime = travelTime;
		this.distanceFromDepot = distanceFromDepot;
	}

	public Stage(Location departPoint, Location destinationPoint, double startingTime, Delivery delivery) {
		this.departPoint = departPoint;
		this.destinationPoint = destinationPoint;
		this.startingTime = startingTime;
		this.distance = MyUtility.calculateDistance(departPoint, destinationPoint);
		this.travelTime = calculateTravelTime(this.distance, 1);
		this.arrivingTime = this.startingTime + this.travelTime;

		if (this.arrivingTime <= delivery.getTimewindowFrom())
			this.issuingTime = delivery.getTimewindowFrom();
		else
			this.issuingTime = this.arrivingTime;

		this.endTime = this.issuingTime + delivery.getServiceTime();
	}

	public Location getDepartPoint() {
		return departPoint;
	}

	public void setDepartPoint(Location departPoint) {
		this.departPoint = departPoint;
	}

	public Location getDestinationPoint() {
		return destinationPoint;
	}

	public void setDestinationPoint(Location destinationPoint) {
		this.destinationPoint = destinationPoint;
	}

	public double getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(double startingTime) {
		this.startingTime = startingTime;
	}

	public double getArrivingTime() {
		return arrivingTime;
	}

	public void setArrivingTime(double arrivingTime) {
		this.arrivingTime = arrivingTime;
	}

	public double getIssuingTime() {
		return issuingTime;
	}

	public void setIssuingTime(double issuingTime) {
		this.issuingTime = issuingTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	public double getDistanceFromDepot() {
		return distanceFromDepot;
	}

	public void setDistanceFromDepot(double distanceFromDepot) {
		this.distanceFromDepot = distanceFromDepot;
	}

	private double calculateTravelTime(double dist, float speed) {
		return dist / speed;
	}

	@Override
	public String toString() {
		return "Stage [startingTime=" + startingTime + ", arrivingTime=" + arrivingTime + ", issuingTime=" + issuingTime
				+ ", endTime=" + endTime + ", distance=" + distance + ", travelTime=" + travelTime
				+ ", distanceFromDepot=" + distanceFromDepot + "]\n";
	}

	
	
}
