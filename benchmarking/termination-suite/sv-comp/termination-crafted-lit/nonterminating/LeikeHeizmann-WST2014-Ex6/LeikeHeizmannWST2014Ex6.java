/* 
 *
 * Program from Example 6 of
 * 2014WST - Leike, Heizmann - Geometric Series as Nontermination Arguments for Linear Lasso Programs
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;


public class LeikeHeizmannWST2014Ex6 {

    public static void loop(int a, int b) {
        while (a >= 1 && b >= 1) {
		a = 2*a;
		b = 3*b;
	}
    }

    public static void main(String[] args) {
        if(args.length >= 2) {
            int x = args[0].length();
            int y = args[1].length();
            loop(x, y);
        }

    }
}
