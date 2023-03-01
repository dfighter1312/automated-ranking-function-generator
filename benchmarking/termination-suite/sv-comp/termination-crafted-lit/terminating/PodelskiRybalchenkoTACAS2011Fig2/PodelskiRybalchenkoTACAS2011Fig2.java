/* 
 * Program from Fig.2 of
 * 2011TACAS - Podelski,Rybalchenko - Transition Invariants and Transition Predicate Abstraction for Program Termination
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;

public class PodelskiRybalchenkoTACAS2011Fig2 {

    public static void loop(int x, int y) {
        while (x >= 0) {
            y = 1;
            while (y < x) {
                y = y + 1;
            }
            x = x - 1; 
        }
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            int x = args[0].length();
            int y = args[1].length();
            loop(x, y);
        }
        
    }
}
