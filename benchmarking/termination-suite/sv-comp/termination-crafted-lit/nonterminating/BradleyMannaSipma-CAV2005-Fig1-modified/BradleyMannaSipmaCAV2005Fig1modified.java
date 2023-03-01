/*
  * Program from Fig.1 of
 * 2005CAV - Bradley,Manna,Sipma - Linear Ranking with Reachability
 * Modified version that can be nonterminating because we allow that inputs of
 * gcd may be zero.
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class BradleyMannaSipmaCAV2005Fig1modified {

	public static void loop(int y1, int y2) {

	if (y1 >= 0 && y2 >= 0) {
    	while (y1 != y2) {
	    	if (y1 > y2) {
		    	y1 = y1 - y2;
    		} else {
	    		y2 = y2 - y1;
		    }
	    }
	}
	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x,y);
		}
	}
}
