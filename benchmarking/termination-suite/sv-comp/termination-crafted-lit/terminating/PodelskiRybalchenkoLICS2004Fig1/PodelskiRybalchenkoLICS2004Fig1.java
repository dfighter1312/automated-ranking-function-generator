/* 
 * Program from Fig.1 of
 * 2004LICS - Podelski, Rybalchenko - Transition Invariants
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class PodelskiRybalchenkoLICS2004Fig1 {

    public static void loop(int x, int y) {
        while (x >= 0 && x <= 1073741823) {
	        y = 1;
		    while (y < x) {
			    y = 2*y;
		    }
		    x = x - 1;
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 2) {
            int x = args[0].length();
            int y = args[1].length();
            loop(x,y);
        }

    }
}
