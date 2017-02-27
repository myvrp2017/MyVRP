package core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class notuse_GenerateSolutionString {
	
	@SuppressWarnings("unused")
	private static String getArrivalTime(int arrivalTime) throws ParseException{
		 String myTime = "08:00";
		 SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		 Date d = df.parse(myTime); 
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(d);
		 cal.add(Calendar.MINUTE, arrivalTime);
		 String newTime = df.format(cal.getTime());
		 
		 return newTime;
	}

	/*public static String toString(String string, SolutionTwoopt bestsolution, ArrayList<Customer> customers) throws ParseException {
		
		FeasibleSolution solutionObject = bestsolution.getFeasibleSolution();
		 
		String solution = "For : "+ customers.size() + " customers, I could find " + solutionObject.getRoutes().size() + " routes using: " + string + "\n";
		solution += "Total Distance: " + solutionObject.getTotalDistance() + "\n";
		solution += "Totalcost: " + bestsolution.getCost() + "\n";
		solution += "Fitness: " + bestsolution.getFitness() + "\n";
		solution += "Arrival time at home for latest car: " + getArrivalTime((int) solutionObject.getLatestTime()) + "\n\n";
		
		for(int i = 0; i<solutionObject.getRoutes().size(); i++){
			solution += "Route " + (i+1) + " covers locations with ID: ";
			Route route = solutionObject.getRoutes().get(i);
			
			for(Customer location: route.getCustomers()){
				
				if(location.getId() == 0){
					continue;
				}
				
				solution += location.getId() + "-> ";
			}
			
			solution = solution.substring(0, solution.length()-3);
			
			solution += "\n";
		}
		
		return solution;
	}*/

}
