package core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import core.model.Delivery;
import core.model.Location;
import core.model.VrpProblem;

public class MyUtility {
	
	/*
	 * read file Benmarking Solomon for VRP 
	 */
	public static VrpProblem readFile(String fileName, boolean ignoreServiceTime) throws FileNotFoundException {
		VrpProblem vrp = null;
		File file = new File("data/" + fileName);

		int numOfVehicle = 0;
		double capacity = 0;
		double maxRouteTime = 0;

		ArrayList<Location> locationOfCustomers = new ArrayList<>();
		ArrayList<Delivery> deliveryList = new ArrayList<>();

		Location locationOfDepot = new Location();
		Scanner scanner = new Scanner(file);
		try {
			int numLine = 1;
			while (scanner.hasNextLine()) {
				@SuppressWarnings("resource")
				Scanner line = new Scanner(scanner.nextLine());
				if (numLine == 5) {
					numOfVehicle = line.nextInt();
					capacity = line.nextInt();

				} else if (numLine > 9) {
					int id = line.nextInt();
					double x = line.nextDouble();
					double y = line.nextDouble();
					int demand = line.nextInt();
					int timeFrom = line.nextInt();
					int timeTo = line.nextInt();
					int serviceTime = line.nextInt(); // nodeTime

					Location location = new Location(x, y);

					locationOfCustomers.add(location);

					// id = 0 (depot)
					if (id == 0) {
						locationOfDepot = new Location(x, y);
						maxRouteTime = timeTo;
					}
					if (ignoreServiceTime) {
						serviceTime = 0;
					}
					Delivery delivery = new Delivery(id, demand, timeFrom, timeTo, serviceTime, locationOfDepot,
							location);
					deliveryList.add(delivery);
				}
				numLine++;
			}
		} finally {
			scanner.close();
			
			MyUtility.calculateDistanceMatrix(locationOfCustomers);
			locationOfDepot.setDistanceMatrix(locationOfCustomers.get(0).getDistanceMatrix());
			vrp = new VrpProblem(fileName, numOfVehicle, capacity, maxRouteTime, locationOfDepot, locationOfCustomers,
					deliveryList);
		}
		return vrp;
	}

	/*
	 * calculate Distance Matrix of each location
	 */
	public static void calculateDistanceMatrix(ArrayList<Location> locations) {
		ArrayList<Double> distanceMatrix = null;
		for (Location location1 : locations) {
			distanceMatrix = new ArrayList<Double>();
			for (Location location2 : locations) {
				double distance = MyUtility.calculateDistance(location1, location2);
				distanceMatrix.add(distance);
			}
			location1.setDistanceMatrix(distanceMatrix);
		}
	}
	/*
	 * return distance betweeb location 1  && location 2
	 */
	public static double calculateDistance(Location location1, Location location2) {
		double x12 = location1.getX() - location2.getX();
		double y12 = location1.getY() - location2.getY();
		return round(Math.sqrt(x12 * x12 + y12 * y12), 2);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	public static double calculateTravelTime(double dist, float speed) {
		return dist / speed;
	}
}
