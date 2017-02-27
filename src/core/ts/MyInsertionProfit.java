package core.ts;

import java.util.Comparator;

import core.model.Route;


public class MyInsertionProfit {
	/*
	 * c1 vs c2 : cost
	 */
    private double c1;
    private double c2;
    private int uStar;
    private Route updateRoute;
    public MyInsertionProfit(double c1, double c2, int uStar,Route updateRoute) {
        this.c1 = c1;
        this.c2 = c2;
        this.uStar = uStar;
        this.updateRoute=updateRoute;
    }

    public double getC1() {
		return c1;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public double getC2() {
		return c2;
	}

	public void setC2(double c2) {
		this.c2 = c2;
	}

	public int getuStar() {
		return uStar;
	}

	public void setuStar(int uStar) {
		this.uStar = uStar;
	}

	public Route getUpdateRoute() {
		return updateRoute;
	}

	public void setUpdateRoute(Route updateRoute) {
		this.updateRoute = updateRoute;
	}

	public static Comparator<MyInsertionProfit> getComparator(SortParameter... sortParameters) {
        return new myInsertionProfitComparator(sortParameters);
    }

    public enum SortParameter {

        C1_ASC, C2_DES
    }

    private static class myInsertionProfitComparator implements Comparator<MyInsertionProfit> {

        private SortParameter[] parameters;

        private myInsertionProfitComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(MyInsertionProfit o1, MyInsertionProfit o2) {
            int comparison;
            for (SortParameter parameter : parameters) {
                switch (parameter) {
                    case C1_ASC:
                        comparison = Double.compare(o1.c1,o2.c1);
                        if (comparison != 0) {
                            return comparison;
                        }
                        break;
                    case C2_DES:
                        comparison = Double.compare(o2.c2,o1.c2);
                        if (comparison != 0) {
                            return comparison;
                        }
                        break;
                }
            }
            return 0;
        }
    }
}
