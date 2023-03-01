/* 
 *
 * Program from Example 9 of
 * 2014WST - Leike, Heizmann - Geometric Series as Nontermination Arguments for Linear Lasso Programs
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;


public class LeikeHeizmannWST2014Ex9 {

    public static void loop(int x) {
        while (x > 0) {
		x = x / 2;
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 1) {
            int x =args[0].length();

            loop(x);
        }

    }
}
