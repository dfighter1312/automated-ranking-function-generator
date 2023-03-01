/* 
 *
 * Program from Example 1 of
 * 2014TACAS - Leike, Heizmann - Ranking Templates for Linear Loops
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class LeikeHeizmannTACAS2014Ex1 {

    public static void loop(int q, int y) {
        while (q > 0) {
            if (y > 0) {
                q = q - y - 1;
            } else {
                q = q + y - 1;
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
