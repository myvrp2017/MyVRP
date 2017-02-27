package core.model;

import java.util.Comparator;

public class Delivery {
	private double demand;
	private int timewindowFrom;
	private int timewindowTo;
	private int serviceTime;
	private int index;
	private Location locationOfDepot;
	private Location locationOfCustomer;

	public Location getLocationOfDepot() {
		return locationOfDepot;
	}

	public void setLocationOfDepot(Location locationOfDepot) {
		this.locationOfDepot = locationOfDepot;
	}

	public Location getLocationOfCus() {
		return locationOfCustomer;
	}

	public void setLocationOfCus(Location locationOfCus) {
		this.locationOfCustomer = locationOfCus;
	}

	public Delivery(double demand, int timewindowFrom, int timewindowTo, int serviceTime, int index,
			Location locationOfDepot, Location locationOfCustomer) {
		this.demand = demand;
		this.timewindowFrom = timewindowFrom;
		this.timewindowTo = timewindowTo;
		this.serviceTime = serviceTime;
		this.index = index;
		this.locationOfDepot = locationOfDepot;
		this.locationOfCustomer = locationOfCustomer;
	}

	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public int getTimewindowFrom() {
		return timewindowFrom;
	}

	public void setTimewindowFrom(int timewindowFrom) {
		this.timewindowFrom = timewindowFrom;
	}

	public int getTimewindowTo() {
		return timewindowTo;
	}

	public void setTimewindowTo(int timewindowTo) {
		this.timewindowTo = timewindowTo;
	}

	public int getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(int serviceTime) {
		this.serviceTime = serviceTime;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * additional
	 */
	public enum SortParameter {
		DISTANCE_DESC, EARLIEST_DEALINE_ASC, DEMAND_DESC
	}

	public static Comparator<Delivery> getComparator(SortParameter... sortParameters) {
		return new DeliveryComparator(sortParameters);
	}

	private static class DeliveryComparator implements Comparator<Delivery> {

		private SortParameter[] sortparameters;

		private DeliveryComparator(SortParameter[] sortparam) {
			this.sortparameters = sortparam;
		}

		private double calculateDistance(Location a, Location b) {
			double xDiff = a.getX() - b.getX();
			double yDiff = a.getY() - b.getY();
			return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		}

		public int compare(Delivery o1, Delivery o2) {
			int compatison;
			for (SortParameter sortparam : this.sortparameters) {
				switch (sortparam) {
				case DISTANCE_DESC:
					double dist1 = calculateDistance(o1.locationOfDepot, o1.locationOfCustomer);
					double dist2 = calculateDistance(o2.locationOfDepot, o2.locationOfCustomer);
					compatison = Double.compare(dist2, dist1);
					if (compatison != 0) {
						return compatison;
					}
					break;
				case EARLIEST_DEALINE_ASC:
					compatison = Double.compare(o1.timewindowFrom, o2.timewindowFrom);
					if (compatison != 0) {
						return compatison;
					}
					break;
				case DEMAND_DESC:
					compatison = Double.compare(o2.demand, o1.demand);
					if (compatison != 0) {
						return compatison;
					}
					break;
				}
			}
			return 0;
		}

	}
}
