package core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class VrpProblem {
	private String fileName;
	private int numOfVehicle;
	private double capacityOfVehicle;
	private double maxRouteTime;
	private Location depot;

	private ArrayList<Location> locationOfCustomers;
	private ArrayList<Delivery> deliveryList;

	public VrpProblem() {
		this.locationOfCustomers = new ArrayList<Location>();
		this.deliveryList = new ArrayList<Delivery>();
	}

	public VrpProblem(String fileName, int numOfVehicle, double capacityOfVehicle, double maxRouteTime, Location depot,
			ArrayList<Location> locationOfCustomers, ArrayList<Delivery> deliveryList) {
		super();
		this.fileName = fileName;
		this.numOfVehicle = numOfVehicle;
		this.capacityOfVehicle = capacityOfVehicle;
		this.maxRouteTime = maxRouteTime;
		this.depot = depot;
		this.locationOfCustomers = locationOfCustomers;
		this.deliveryList = deliveryList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getNumOfVehicle() {
		return numOfVehicle;
	}

	public void setNumOfVehicle(int numOfVehicle) {
		this.numOfVehicle = numOfVehicle;
	}

	public double getCapacityOfVehicle() {
		return capacityOfVehicle;
	}

	public void setCapacityOfVehicle(double capacityOfVehicle) {
		this.capacityOfVehicle = capacityOfVehicle;
	}

	public double getMaxRouteTime() {
		return maxRouteTime;
	}

	public void setMaxRouteTime(double maxRouteTime) {
		this.maxRouteTime = maxRouteTime;
	}

	public Location getDepot() {
		return depot;
	}

	public void setDepot(Location depot) {
		this.depot = depot;
	}

	public ArrayList<Location> getLocationOfCustomers() {
		return locationOfCustomers;
	}

	public void setLocationOfCustomers(ArrayList<Location> locationOfCustomers) {
		this.locationOfCustomers = locationOfCustomers;
	}

	public ArrayList<Delivery> getDeliveryList() {
		return deliveryList;
	}

	public void setDeliveryList(ArrayList<Delivery> deliveryList) {
		this.deliveryList = deliveryList;
	}

	
}
