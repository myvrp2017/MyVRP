package core.model;

import java.util.ArrayList;

public class VrpProblem {
	private String fileName;
	private int numOfVehicle;
	private int capacity;
	private ArrayList<Location> locations;
	private double MAX_ROUTE_TIME = 0; 

	public VrpProblem() {
		super();
	}
	
	public VrpProblem(String fileName, int numOfVehicle, int capacity, ArrayList<Location> locations) {
		super();
		this.fileName = fileName;
		this.numOfVehicle = numOfVehicle;
		this.capacity = capacity;
		this.locations = locations;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}

	public double getMAX_ROUTE_TIME() {
		return MAX_ROUTE_TIME;
	}

	public void setMAX_ROUTE_TIME(double MAX) {
		MAX_ROUTE_TIME = MAX;
	}
}
