/* 
 * Program from Fig.1 of
 * 2011TACAS - Podelski,Rybalchenko - Transition Invariants and Transition Predicate Abstraction for Program Termination
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;

public class PodelskiRybalchenkoTACAS2011Fig1 {

    public static void loop(int y) {
       while (y >= 0) {
		y = y - 1;
	}
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            int x = args[0].length();
            loop(x);
        }
        
    }
}
