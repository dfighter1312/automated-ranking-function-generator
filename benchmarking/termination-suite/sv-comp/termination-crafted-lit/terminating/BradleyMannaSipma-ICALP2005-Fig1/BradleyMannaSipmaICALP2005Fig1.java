/*
  * Program from Fig.1 of
 * 2005ICALP - Bradley,Manna,Sipma - The Polyranking Principle
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class BradleyMannaSipmaICALP2005Fig1 {

	public static void loop(int x, int y, int N) {


	// continue only for values where there won't be any overflow or underflow
	// on systems where sizeof(int)=4 holds.
	if (N < 536870912 && x < 536870912 && y < 536870912 && x >= -1073741824) {
    	if (x + y >= 0) {
	    	while (x <= N) {
		    	if (new Random().nextInt() != 0) {
			    	x = 2*x + y;
				    y = y + 1;
    			} else {
	    			x = x + 1;
		    	}
		    }
	    }
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = args[0].length();
		int y = args[1].length();
		int N = args[2].length();
		loop(x,y,N);
		}
	}
}
