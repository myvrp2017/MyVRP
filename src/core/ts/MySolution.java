package core.ts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.coinor.opents.Solution;
import org.coinor.opents.SolutionAdapter;

import core.model.Delivery;
import core.model.Location;
import core.model.Route;
import core.model.Stage;
import core.model.VrpProblem;
import core.util.MyUltility;

public class MySolution extends SolutionAdapter {

	public ArrayList<Route> RouteList;
	public double totalDistance = 0;
	public double totalTravelTime = 0;
	public List<Integer> exeptionList;
	public List<Integer> visited = new ArrayList<Integer>();
	float lamda;
	float u;
	float alpha1;
	float alpha2;
	float speed = 1;

	public MySolution() {
	}

	public MySolution(VrpProblem vrp, int sortparam, float u, float lamda, float alpha1, float alpha2) {
		this.seedInsertion(vrp, sortparam, lamda, u, alpha1, alpha2);
		calculateDistanceAndTravelTime();
	}

	public MySolution(MySolution solution) {
		this.RouteList = (ArrayList<Route>) solution.RouteList.clone();
		this.totalDistance = solution.totalDistance;
		this.totalTravelTime = solution.totalTravelTime;
		this.exeptionList = solution.exeptionList;
	}

	public Object clone() {
		MySolution copy = (MySolution) super.clone();
		copy.RouteList = (ArrayList<Route>) this.RouteList.clone();
		return copy;
	} // end clone

	public void seedInsertion(VrpProblem vrp, int sortparam, float lamda, float u, float alpha1, float alpha2) {

		this.alpha1 = alpha1;
		this.alpha2 = alpha2;
		this.u = u;
		this.lamda = lamda;
		this.exeptionList = checkInput(vrp);
		if (!this.exeptionList.isEmpty()) {
			for (int e = 0; e < this.exeptionList.size(); e++) {
				System.out.println("Can not scheduled : " + this.exeptionList.get(e));
			}
		}
		int n_unvisited = vrp.getLocationOfCustomers().size() - this.exeptionList.size();
		boolean[] unvisited = new boolean[vrp.getLocationOfCustomers().size()];
		unvisited[0] = true;

		ArrayList<Route> routes = new ArrayList<Route>();

		@SuppressWarnings("unchecked")
		ArrayList<Delivery> sortD = (ArrayList<Delivery>) vrp.getDeliveryList().clone();
		ArrayList<Delivery> listD = sortDelivery(sortD, sortparam);

		while (n_unvisited != 0) {

			Route rseed = new Route();
			rseed = initSeedRoute(vrp, listD, unvisited);

			n_unvisited = updateN(unvisited, rseed);
			boolean noMoreFeasibleCustomer = false;
			while (!noMoreFeasibleCustomer) {
				int uStar = -1;

				uStar = findCustomerAndUpdateSeed(unvisited, rseed, vrp);
				/*
				 * construction may be replaced
				 */

				if (uStar == -1)
					noMoreFeasibleCustomer = true;
				else
					unvisited[uStar] = true;
			}
			n_unvisited = updateN(unvisited, rseed);
			routes.add(rseed);
		}
		this.RouteList = routes;
	}

	private Route initSeedRoute(VrpProblem vrp, List<Delivery> listD, boolean[] unvisited) {
		int i;
		double trlTime = 0;
		double arrTime = 0;
		double issuingTime = 0;
		double endTime = 0;
		double returnDepotTime = 0;
		ArrayList<Double> distanceMatrix = vrp.getDepot().getDistanceMatrix();
		for (i = 0; i < listD.size() ; i++) {
			if (this.exeptionList.contains(listD.get(i).getIndex()))
				continue;
			if (listD.get(i).getIndex() == 0)
				continue;
			if (unvisited[listD.get(i).getIndex()])
				continue;
			trlTime = calculateTrlTime(distanceMatrix.get(listD.get(i).getIndex()), this.speed);
			arrTime = vrp.getDeliveryList().get(0).getTimewindowFrom() + trlTime;
			if (arrTime > listD.get(i).getTimewindowTo())
				continue;
			if (arrTime < listD.get(i).getTimewindowFrom())
				issuingTime = listD.get(i).getTimewindowFrom();
			else
				issuingTime = arrTime;
			endTime = issuingTime + listD.get(i).getServiceTime();
			returnDepotTime = endTime + trlTime;
			if (returnDepotTime > vrp.getDeliveryList().get(0).getTimewindowTo())
				continue;
			break;
		}
		//System.out.println("i la :" + i);
		// depot -> A -> depot
		Stage s_OA = new Stage(vrp.getDepot(), listD.get(i).getLocationOfCus(), 0, arrTime, issuingTime, endTime,
				distanceMatrix.get(listD.get(i).getIndex()), trlTime, distanceMatrix.get(listD.get(i).getIndex()));
		Stage s_AO = new Stage(listD.get(i).getLocationOfCus(), vrp.getDepot(), endTime, returnDepotTime,
				returnDepotTime, returnDepotTime, distanceMatrix.get(listD.get(i).getIndex()), trlTime,
				2 * distanceMatrix.get(listD.get(i).getIndex()));

		ArrayList<Delivery> list = new ArrayList<Delivery>();
		list.add(vrp.getDeliveryList().get(0));
		list.add(listD.get(i));
		list.add(vrp.getDeliveryList().get(0));

		ArrayList<Stage> listOfStage = new ArrayList<Stage>();
		listOfStage.add(s_OA);
		listOfStage.add(s_AO);

		return new Route(list, listD.get(i).getDemand(), 2 * distanceMatrix.get(listD.get(i).getIndex()),
				returnDepotTime, listOfStage);
	}

	private int findCustomerAndUpdateSeed(boolean[] unvisited, Route rseed, VrpProblem vrp) {
		int uStar = -1;
		ArrayList<MyInsertionProfit> p = new ArrayList<MyInsertionProfit>();
		for (int i = 1; i < unvisited.length; i++) {
			if (!unvisited[i] && !this.exeptionList.contains(i)) {
				for (int j = 0; j < rseed.getListOfStage().size(); j++) {
					Route tmpRoute = rseed.clone();
					Stage tmpStage = rseed.getListOfStage().get(j);

					double c1 = calculeteProfit(tmpStage, rseed, i, vrp, 1);
					double c2 = calculeteProfit(tmpStage, rseed, i, vrp, 2);
					if (isFeasible(tmpRoute, tmpStage, i, vrp, j, rseed)) {
						uStar = i;
						p.add(new MyInsertionProfit(c1, c2, uStar, tmpRoute));
					}
				}
			}
		}
		MyInsertionProfit tmp = null;
		if (uStar != -1) {
			Comparator<MyInsertionProfit> c = MyInsertionProfit.getComparator(MyInsertionProfit.SortParameter.C2_DES,
					MyInsertionProfit.SortParameter.C1_ASC);
			Collections.sort(p, c);
			tmp = p.get(0);
			uStar = tmp.getuStar();
			rseed.setAttribute(tmp.getUpdateRoute());
			//System.out.println(rseed);
		}
		return uStar;
	}

	public boolean isFeasible(Route tmpRoute, Stage tmpStage, int positionOfCus, VrpProblem vrp, int positionInStage,
			Route currentRoute) {
		Delivery del_u = vrp.getDeliveryList().get(positionOfCus);
		Delivery del_j = currentRoute.getListOfDelivery().get(positionInStage + 1);
		double currentCapacity = currentRoute.getTotalDemand();
		Route saveRoute = currentRoute.clone();

		if (currentCapacity + del_u.getDemand() > vrp.getCapacityOfVehicle())
			return false;
		// insert u into ij
		double dist_iu = MyUltility.calculateDistance(tmpStage.getDepartPoint(),
				vrp.getLocationOfCustomers().get(positionOfCus));
		double trlTime_iu = calculateTrlTime(dist_iu, this.speed);
		double arrTime_iu = trlTime_iu + tmpStage.getStartingTime();
		if (arrTime_iu > del_u.getTimewindowTo())
			return false;
		double issuingTime_iu = 0;
		if (arrTime_iu <= del_u.getTimewindowFrom())
			issuingTime_iu = del_u.getTimewindowFrom();
		else
			issuingTime_iu = arrTime_iu;

		double dist_uj = MyUltility.calculateDistance(vrp.getLocationOfCustomers().get(positionOfCus),
				tmpStage.getDestinationPoint());
		double trlTime_uj = calculateTrlTime(dist_uj, this.speed);
		double arrTime_uj = trlTime_uj + issuingTime_iu + del_u.getServiceTime();
		if (arrTime_uj > del_j.getTimewindowTo())
			return false;
		double issuingTime_uj = 0;
		if (arrTime_uj <= del_j.getTimewindowFrom())
			issuingTime_uj = del_j.getTimewindowFrom();
		else
			issuingTime_uj = arrTime_uj;

		Stage s_iu = new Stage(tmpStage.getDepartPoint(), vrp.getLocationOfCustomers().get(positionOfCus),
				tmpStage.getStartingTime(), arrTime_iu, issuingTime_iu, issuingTime_iu + del_u.getServiceTime(),
				dist_iu, trlTime_iu, tmpStage.getDistanceFromDepot() + dist_iu);
		Stage s_uj = new Stage(vrp.getLocationOfCustomers().get(positionOfCus), tmpStage.getDestinationPoint(),
				issuingTime_iu + del_u.getServiceTime(), arrTime_uj, issuingTime_uj,
				issuingTime_uj + del_j.getServiceTime(), dist_uj, trlTime_uj,
				tmpStage.getDistanceFromDepot() + dist_iu + dist_uj);
		// calculate time after insert u
		double currentTotalTrlTime = issuingTime_uj + del_j.getServiceTime();
		for (int j = positionInStage + 1; j < currentRoute.getListOfStage().size(); j++) {
			Stage tmp1 = currentRoute.getListOfStage().get(j);
			Delivery tmp2 = currentRoute.getListOfDelivery().get(j + 1);
			if ((currentTotalTrlTime + tmp1.getTravelTime()) > tmp2.getTimewindowTo()) {
				currentRoute = saveRoute;
				return false;
			}
			Stage updateStage = new Stage(tmp1.getDepartPoint(), tmp1.getDestinationPoint(), currentTotalTrlTime, tmp2);
			tmpRoute.getListOfStage().remove(j);
			tmpRoute.getListOfStage().add(j, updateStage);
			currentTotalTrlTime = updateStage.getEndTime();
		}
		tmpRoute.getListOfDelivery().add(positionInStage + 1, del_u);
		tmpRoute.setTotalDemand(tmpRoute.getTotalDemand() + del_u.getDemand());
		tmpRoute.setTotalDistance(tmpRoute.getTotalDistance() - tmpStage.getDistance() + dist_iu + dist_uj);
		tmpRoute.setTotalTravelTime(currentTotalTrlTime);
		tmpRoute.getListOfStage().remove(positionInStage);
		tmpRoute.getListOfStage().add(positionInStage, s_iu);
		tmpRoute.getListOfStage().add(positionInStage + 1, s_uj);
		return true;
	}

	private double calculeteProfit(Stage tmpStage, Route rseed, int positionOfCus, VrpProblem vrp, int optionProfit) {

		double d_iu = MyUltility.calculateDistance(tmpStage.getDepartPoint(),
				vrp.getLocationOfCustomers().get(positionOfCus));
		double d_uj = MyUltility.calculateDistance(vrp.getLocationOfCustomers().get(positionOfCus),
				tmpStage.getDestinationPoint());
		double d_ij = tmpStage.getDistance();

		double t_iu = calculateTrlTime(d_iu, this.speed);
		double t_uj = calculateTrlTime(d_uj, this.speed);
		double t_ij = calculateTrlTime(d_ij, this.speed);

		double c11 = d_iu + d_uj - this.u * d_ij;

		double issuingTimeAtPosition;
		if (tmpStage.getStartingTime() + t_iu <= vrp.getDeliveryList().get(positionOfCus).getTimewindowFrom())
			issuingTimeAtPosition = vrp.getDeliveryList().get(positionOfCus).getTimewindowFrom();
		else
			issuingTimeAtPosition = tmpStage.getStartingTime() + t_iu;
		double arrTimeFromJtoU = issuingTimeAtPosition + vrp.getDeliveryList().get(positionOfCus).getServiceTime()
				+ t_uj;

		double c12 = arrTimeFromJtoU - tmpStage.getIssuingTime();

		double c1 = this.alpha1 * c11 + this.alpha2 * c12;

		double d_OU = vrp.getDepot().getDistanceMatrix().get(positionOfCus);
		double c2 = this.lamda * d_OU - c1;

		if (optionProfit == 1) {
			return c1;
		} else {
			return c2;
		}
	}

	private int updateN(boolean[] unvisited, Route rseed) {
		for (int i = 0; i < rseed.getListOfDelivery().size(); i++) {
			if (!visited.contains(rseed.getListOfDelivery().get(i).getIndex()))
				visited.add(rseed.getListOfDelivery().get(i).getIndex());
			unvisited[rseed.getListOfDelivery().get(i).getIndex()] = true;
		}
		return unvisited.length - this.visited.size() - this.exeptionList.size();
	}

	private double calculateTrlTime(double dist, float speed) {
		return dist / speed;
	}

	private ArrayList<Delivery> sortDelivery(ArrayList<Delivery> sortD, int sortparam) {
		if (sortparam == 1) {
			Comparator<Delivery> c = Delivery.getComparator(Delivery.SortParameter.DEMAND_DESC,
					Delivery.SortParameter.EARLIEST_DEALINE_ASC);
			Collections.sort(sortD, c);
		}
		if (sortparam == 2) {
			Comparator<Delivery> c = Delivery.getComparator(Delivery.SortParameter.DISTANCE_DESC,
					Delivery.SortParameter.DEMAND_DESC);
			Collections.sort(sortD, c);
		}
		if (sortparam == 3) {
			Comparator<Delivery> c = Delivery.getComparator(Delivery.SortParameter.DEMAND_DESC,
					Delivery.SortParameter.DISTANCE_DESC);
			Collections.sort(sortD, c);
		}
		return sortD;
	}

	private List<Integer> checkInput(VrpProblem vrp) {
		List<Integer> exeption = new ArrayList<Integer>();
		ArrayList<Delivery> deliveryList = vrp.getDeliveryList();
		for (int i = 1; i < deliveryList.size(); i++) {
			if (deliveryList.get(i).getDemand() > vrp.getCapacityOfVehicle()) {
				exeption.add(i);
				continue;
			}
			ArrayList<Double> distanceMatrix = vrp.getLocationOfCustomers().get(0).getDistanceMatrix();
			double trlTime = calculateTrlTime(distanceMatrix.get(i), this.speed);
			if (trlTime > deliveryList.get(i).getTimewindowTo()) {
				exeption.add(i);
				continue;
			}
		}
		return exeption;
	}

	public void calculateDistanceAndTravelTime() {
		double totalDistance = 0;
		double totalTime = 0;
		for (int i = 0; i < this.RouteList.size(); i++) {
			totalDistance += this.RouteList.get(i).getTotalDistance();
			totalTime += this.RouteList.get(i).getTotalTravelTime();
		}
		this.totalDistance = totalDistance;
		this.totalTravelTime = totalTime;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append("Solution value: " + getObjectiveValue()[0]);
		for (int i = 0; i < this.RouteList.size(); i++) {
			s.append("Route " + i + " : [");
			for (int j = 0; j < this.RouteList.get(i).getListOfDelivery().size(); j++) {
				s.append(this.RouteList.get(i).getListOfDelivery().get(j).getIndex() + " , ");
			}
			s.append(" ]");
			s.append(" Total Demand: " + this.RouteList.get(i).getTotalDemand());
			s.append(" Total Distance: " + this.RouteList.get(i).getTotalDistance());
			s.append(" Total TravelTime: " + this.RouteList.get(i).getTotalTravelTime());
			this.totalDistance += this.RouteList.get(i).getTotalDistance();
			this.totalTravelTime += this.RouteList.get(i).getTotalTravelTime();
		}
		s.append("Total Route :" + this.RouteList.size());
		s.append("Total TravelTime: " + this.totalTravelTime);
		s.append("Total Distance: " + this.totalDistance);
		return s.toString();
	} // end toString

	/*
	 * PhatNguyen
	 */
	public void repairSolution() {
		/*
		 * Solution feasibleSolution = this.feasibleSolution;
		 * 
		 * ArrayList<Route> routes = feasibleSolution.getRoutes(); int
		 * numOfRoute = routes.size();
		 * 
		 * ArrayList<Route> newRoutes = new ArrayList<Route>();
		 * 
		 * for (int k = 0; k < 1000; k++) { for (int i = 0; i < numOfRoute - 1;
		 * i++) { for (int j = i + 1; j < numOfRoute; j++) { //
		 * newRoutes.addAll(twoOpt(routes.get(i),routes.get(j))); // change =
		 * MyUltility.getDistance(routes.get(i), // routes.get(j)) + dist(i + 1,
		 * j + 1) - dist(i, i + 1) - // dist(j, j + 1); twoOpt(routes.get(i),
		 * routes.get(j)); } } }
		 * 
		 * for (int i = 0; i < feasibleSolution.getRoutes().size(); i++) {
		 * if(routes.get(i).sizeofRoute() == 1){
		 * feasibleSolution.getRoutes().remove(i); }else{ continue; } }
		 * 
		 * 
		 * for (Route route : feasibleSolution.getRoutes()) { if
		 * (route.sizeofRoute() == 1) { continue; } else { newRoutes.add(route);
		 * } } feasibleSolution.setRoutes(newRoutes);
		 */
	}

} // end class MySolution
