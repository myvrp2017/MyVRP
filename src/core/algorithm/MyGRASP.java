package core.algorithm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import core.model.Location;
import core.model.VrpProblem;
import core.ts.MySolution;
import core.util.notuse_GenerateSolutionString;
import core.util.notuse_SolutionGenerator;

/**
 * 
 * @author PhatNguyen
 * 
 *         GRASP algorithm procedure GRASP(Max Iterations,Seed) 1 Read Input();
 *         2 for k = 1,..., Max Iterations do 3 Solution â†? Greedy Randomized
 *         Construction(Seed); 4 if Solution is not feasible then 5 Solution â†?
 *         Repair(Solution) ; 6 end; 7 Solution â†? Local Search(Solution); 8
 *         Update Solution(Solution,Best Solution); 9 end; 10 return Best
 *         Solution; end GRASP.
 */

public class MyGRASP {

	// properties
	VrpProblem vrpProblem;
	private ArrayList<Location> customers;
	private double maxRouteTime;

	// solution
	private MySolution bestSolution;

	/*
	 * alpha is the size of RCL. threshold is the value of fitness when evaluate
	 * feasible solution. betaValue is used in expression c(e) <= beta * (cmax -
	 * cmin) with c is possible added costs when building RCL with policy 2
	 * policy 1 : choose the alpha element by randomization after that choose
	 * the best element . policy 2 : choose the best alpha item after that
	 * choose one by randomization
	 */
	// params
	private double threshold;
	private double betaValue;
	private int alpha;

	private int maxIterations;

	public MyGRASP() {
		super();
		this.betaValue = 0.1;
	}

	public MyGRASP(VrpProblem vrpProblem, double threshold, int alpha, double betaValue, int maxIterations) {
		super();
		this.vrpProblem = vrpProblem;
		this.customers = (ArrayList<Location>) vrpProblem.getLocations().clone();
		this.maxRouteTime = vrpProblem.getMAX_ROUTE_TIME();

		this.threshold = threshold;
		this.betaValue = betaValue;
		this.maxIterations = maxIterations;
		this.bestSolution = new MySolution();

		doGRASP();

	}

	// main method
	private void doGRASP() {
		ArrayList<Location> customersNotUsed = (ArrayList<Location>) customers.clone();
		
		/*
		 * remove depot and shuffle list if customerNotUsed
		 */
		ArrayList<Location> customerToVisit = (ArrayList<Location>) customersNotUsed.clone();
		
		customerToVisit.remove(0);
		Collections.shuffle(customerToVisit);
		
		
		/*
		 * remove customer in customerNotUesd if it in customerToVisit
		 */
		for (Location customer : customerToVisit) {
			customersNotUsed.remove(customer);
		}

		for (Location customer : customerToVisit) {
			customersNotUsed.add(customer);
		}
		MySolution bestSolution = new MySolution();
		bestSolution.setFitness(0.00);
		
		bestSolution.setFeasibleSolution(notuse_SolutionGenerator.genereateSolution(vrpProblem,customersNotUsed));
		int counter = 0;
		while (counter < this.maxIterations && bestSolution.getFitness() < threshold) {
			System.out.println("loop "+counter);
			MySolution candidate = new MySolution();
			candidate = GRC();
			/*
			if(candidate.getFitness() < threshold){
				candidate.repairSolution();
			}*/
			candidate = localSearch(candidate);
			/*
			 * compare cost between solution "candidate" and "bestSolution" if
			 * candidate has min cost -> upddate bestSolution = candidate
			 */
			int cost = calculateCost(candidate);
			candidate.setCost(cost);

			if (candidate.getCost() < bestSolution.getCost()) {
				
				System.out.println("candidate vs best : " + candidate.getCost() + " vs " + bestSolution.getCost() );
				bestSolution = candidate;
				bestSolution.setFitness(notuse_SolutionGenerator.calculateFitness(vrpProblem,
						bestSolution.getFeasibleSolution(), customers.size()));
			}
			counter++;

		}
		this.bestSolution = bestSolution;
		//bestSolution.getFeasibleSolution().exportRoute();
	}

	// Greedy Randomized Construction
	// GRC polcity 1 : choose a random RCL and choose the best element in this
	private MySolution GRC() {

		MySolution candidateSolution = new MySolution();

		Location initialNode = customers.get(0); // depot
		candidateSolution.getLocationInSolution().add(initialNode);

		ArrayList<Location> notVisitedLocations = (ArrayList<Location>) this.customers.clone();

		/*
		 * visitedLocations contain ID of customer that visited
		 */
		ArrayList<Integer> visitedLocations = new ArrayList<Integer>();

		notVisitedLocations.remove(0);
		visitedLocations.add(0);

		boolean done = false;

		while (!done) {
			// init list of feature cost
			HashMap<Integer, Double> featureCost = generateFeatureCost(notVisitedLocations, visitedLocations,
					candidateSolution);
			// find min vs max cost in featureCost
			double minCost = findMin(featureCost);
			double maxCost = findMax(featureCost);

			// init random RCL
			ArrayList<Location> RCL = (ArrayList<Location>) notVisitedLocations.clone();
			Collections.shuffle(RCL);

			if (!notVisitedLocations.isEmpty() && !RCL.isEmpty()) {
				Location choosenLocation = new Location();
				int keyMin = findMinKey(featureCost);
				for (Location customer : RCL) {
					if (customer.getId() == keyMin) {
						choosenLocation = customer;
					}
				}

				candidateSolution.getLocationInSolution().add(choosenLocation);

				// update visitedLocations and notVisitedLocations
				visitedLocations.add(choosenLocation.getId());
				notVisitedLocations.remove(choosenLocation);

			}
			if (notVisitedLocations.isEmpty()) {
				ArrayList<Location> routeToEvaluate = new ArrayList<>();

				for (Location customer : candidateSolution.getLocationInSolution()) {
					routeToEvaluate.add(customer);
				}

				candidateSolution.setFeasibleSolution(
						util.notuse_SolutionGenerator.genereateSolution(this.vrpProblem, routeToEvaluate));
				done = true;
			}

			/*
			 * System.out.println("while loop"); counter++;
			 */
		}

		/*candidateSolution.getFeasibleSolution().exportRoute();*/
		return candidateSolution;
	}

	// Local Search Method
	private MySolution localSearch(MySolution candidate) {
		MySolution bestLocal = new MySolution();

		int counter = 0;

		while (counter < maxIterations) {
			MySolution solution = new MySolution();

			ArrayList<Location> customerInCandidate = new ArrayList<>();
			customerInCandidate.addAll(candidate.getFeasibleSolution().getAllLocations());

			Random r = new Random();
			int r1 = r.nextInt(customerInCandidate.size());
			int r2 = r.nextInt(customerInCandidate.size());

			Collections.swap(customerInCandidate, r1, r2);

			customerInCandidate.add(0, customers.get(0));

			solution.setFeasibleSolution(notuse_SolutionGenerator.genereateSolution(vrpProblem, customerInCandidate));
			solution.setCost(calculateCost(solution));

			if (solution.getCost() < bestLocal.getCost()) {
				bestLocal = solution;
			}

			counter++;
		}

		return bestLocal;
	}

	/**
	 * Support method
	 */

	/**
	 * 
	 * @param notVisitedLocations
	 * @param visitedLocations
	 * @param candidateSolution
	 * @return hash-map each key-value contains cost when adding customer[key]
	 *         to candidateSolution
	 */
	private HashMap<Integer, Double> generateFeatureCost(ArrayList<Location> notVisitedLocations,
			ArrayList<Integer> visitedLocations, MySolution candidateSolution) {

		HashMap<Integer, Double> featureCost = new HashMap<>();

		for (Location customer : notVisitedLocations) {
			if (visitedLocations.contains(customer.getId())) {
				continue;
			} else {
				double cost = costLocationToSolution(candidateSolution, customer);
				featureCost.put(customer.getId(), cost);
			}
		}

		return featureCost;

	}

	/**
	 * 
	 * @param featureCost
	 *            is a Hash-Map
	 * @return min | max cost in this Hash-Map
	 */
	private double findMax(HashMap<Integer, Double> featureCost) {
		double maxCost = 0.00;

		for (double value : featureCost.values()) {
			if (value > maxCost) {
				maxCost = value;
			}
		}

		return maxCost;
	}

	private double findMin(HashMap<Integer, Double> featureCost) {
		double minCost = Double.MAX_VALUE;

		for (double value : featureCost.values()) {
			if (value < minCost) {
				minCost = value;
			}
		}

		return minCost;
	}

	private ArrayList<Integer> addLocations(MySolution candidate) {

		ArrayList<Integer> usedLocations = new ArrayList<>();

		for (Location customer : candidate.getLocationInSolution()) {
			usedLocations.add(customer.getId());
		}

		return usedLocations;
	}

	/**
	 * 
	 * @param customersNotUsed
	 * @return locations after remove location has index 0 ( depot ) and shuffle
	 *         the other locations
	 */
	private ArrayList<Location> getLocationsToVisit(ArrayList<Location> customersNotUsed) {
	
		ArrayList<Location> customers = new ArrayList<>();
	
		for (Location customer : customersNotUsed) {
	
			if (customer.getId() == 0) {
				continue;
			}
	
			customers.add(customer);
		}
	
		// shuffle location like a randomized sort ;)
		Collections.shuffle(customers);
	
		return customers;
	}

	private int calculateCost(MySolution candidate) {
		int cost = 0;

		/* cost of 1000 for every car */
		cost += candidate.getFeasibleSolution().getRoutes().size() * 1000;

		/* cost 10 for every minute */
		cost += candidate.getFeasibleSolution().getLatestTime() * 10;
		return cost;
	}

	private double costLocationToSolution(MySolution candidate, Location customer) {
		double travelTimeBetweenNodes = candidate.getLocationInSolution()
				.get(candidate.getLocationInSolution().size() - 1).getDistanceMatrix().get(customer.getId());
		double nodeCost = travelTimeBetweenNodes + customer.getServiceTime();

		return nodeCost;
	}

	private double generateNewSeedValue(double seed) {
		seed = seed + 0.1;
		return seed > 1 ? 1 : seed;
	}

	private int findMinKey(HashMap<Integer, Double> featureCost2) {
		int keyMin = Integer.MAX_VALUE;

		for (Integer key : featureCost2.keySet()) {
			if (keyMin > key) {
				keyMin = key;
			}
		}

		return keyMin;
	}

	private int findMaxKey(HashMap<Integer, Double> featureCost) {
		int keyMax = 0;

		for (Integer key : featureCost.keySet()) {
			if (keyMax < key) {
				keyMax = key;
			}
		}

		return keyMax;
	}

	// in list of customer the id vs index as the same
	private ArrayList<Integer> randomAlphaElemen(int numofLocation, int alpha) {

		ArrayList<Integer> randoms = new ArrayList<Integer>();
		Random random = new Random();
		while (randoms.size() < alpha) {
			int index = random.nextInt(numofLocation);
			if (randoms.contains(index)) {
				continue;
			} else {
				randoms.add(index);
			}
		}
		return randoms;
	}

	@Override
	public String toString() {

		try {
			return notuse_GenerateSolutionString.toString("GRASP", bestSolution, customers);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "Solution not working";

	}
	public MySolution getBestSolution() {
		return bestSolution;
	}

}
