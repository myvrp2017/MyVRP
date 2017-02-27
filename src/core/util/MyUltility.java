package core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import core.model.Location;
import core.model.VrpProblem;
import model.Customer;

public class MyUltility {
	
	public static VrpProblem readFile(String fileName) throws FileNotFoundException {
		File file = new File("data/"+fileName);
		ArrayList<Location> locations = new ArrayList<>();
		VrpProblem vrp = null;
		
		int numOfVehicle = 0;
		int capacity = 0 ;
		Scanner scanner = new Scanner(file);
		try {
			int numLine=1;
			while(scanner.hasNextLine()){
				Scanner line = new Scanner(scanner.nextLine());
				if(numLine == 5){
					numOfVehicle = line.nextInt();
					capacity = line.nextInt();
					
				}else if(numLine > 9){
					int id = line.nextInt();
					double x = line.nextDouble();
					double y = line.nextDouble();
					int demand = line.nextInt();
					int timeFrom = line.nextInt();
					int timeTo = line.nextInt();
					int serviceTime = line.nextInt(); // nodeTime
					
					Location location = new Location(id ,x ,y, timeFrom, timeTo, serviceTime, demand); // id = 0 (depot)
					locations.add(location);
				}
				numLine++;
			}
		}finally{
			scanner.close();
			MyUltility.calculateDistanceMatrix(locations);
			vrp = new VrpProblem(fileName, numOfVehicle, capacity,locations);
			
			vrp.setMAX_ROUTE_TIME(locations.get(0).getTimeTo());
		}
		return vrp;
	}
	
	public static void calculateDistanceMatrix(ArrayList<Customer> locations) {
		ArrayList<Double> distanceMatrix = null;
		for (Customer location1 : locations) {
			distanceMatrix = new ArrayList<Double>();
			for (Customer location2 : locations) {
				double distance = MyUltility.getDistance(location1, location2);
				distanceMatrix.add(distance);
			}
			location1.setDistanceMatrix(distanceMatrix);
		}
	}

	public static double getDistance(Customer l1, Customer l2) {
		double x12 = l1.getX() - l2.getX();
		double y12 = l1.getY() - l2.getY();
		return round(Math.sqrt(x12 * x12 + y12 * y12) , 2);
	}
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
