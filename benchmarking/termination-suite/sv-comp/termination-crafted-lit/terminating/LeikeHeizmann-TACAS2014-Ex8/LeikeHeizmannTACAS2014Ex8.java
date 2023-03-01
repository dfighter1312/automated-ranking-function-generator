/* 
 *
 * Program from Example 8 of
 * 2014TACAS - Leike, Heizmann - Ranking Templates for Linear Loops
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;


public class LeikeHeizmannTACAS2014Ex8 {

    public static void loop(int q, int y) {
            while (q > 0) {
            if (y > 0) {
                y = 0;
                q = new Random().nextInt();
            } else {
                y = y - 1;
                q = q - 1;
            }
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 2) {
            int q = args[0].length();
            int y = args[1].length();

            loop(q, y);
        }

    }
}
