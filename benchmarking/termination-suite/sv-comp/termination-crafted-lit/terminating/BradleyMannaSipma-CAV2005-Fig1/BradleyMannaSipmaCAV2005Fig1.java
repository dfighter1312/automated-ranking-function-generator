/*
 
* 
* Program from Fig.1 of
 * 2005CAV - Bradley,Manna,Sipma - Linear Ranking with Reachability
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class BradleyMannaSipmaCAV2005Fig1 {

	public static void loop(int y1, int y2) {


	if (y1 > 0 && y2 > 0) {
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
		int y1 = args[0].length();
		int y2 = args[1].length();
		loop(y1, y2);
		}
	}
}
