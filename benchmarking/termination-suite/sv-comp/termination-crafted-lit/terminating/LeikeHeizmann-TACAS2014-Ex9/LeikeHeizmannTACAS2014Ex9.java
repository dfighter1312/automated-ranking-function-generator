/* 
 *
 * Program from Figure 1 of
 * 2014TACAS - Leike, Heizmann - Ranking Templates for Linear Loops
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;


public class LeikeHeizmannTACAS2014Ex9 {

    public static void loop(int q, int p) {
        while (q > 0 && p > 0 && p != q) {
            if (q < p) {
                q = q - 1;
            } else {
                if (p < q) {
                    p = p - 1;
                }
            }
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 2) {
            int q =args[0].length();
            int p = args[1].length();

            loop(q, p);
        }

    }
}
