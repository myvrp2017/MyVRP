package core.model;

import java.util.ArrayList;

public class Location {
	
	private double x,y;
	/*
	 * distanceMatrix is an array contains distance between this Location and the other
	 */
	private ArrayList<Double> distanceMatrix ;
	
	
	public Location() {
		super();
	}
	public Location(double x, double y){
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public ArrayList<Double> getDistanceMatrix() {
		return distanceMatrix;
	}
	public void setDistanceMatrix(ArrayList<Double> distanceMatrix) {
		this.distanceMatrix = distanceMatrix;
	}
	
	
	
}
