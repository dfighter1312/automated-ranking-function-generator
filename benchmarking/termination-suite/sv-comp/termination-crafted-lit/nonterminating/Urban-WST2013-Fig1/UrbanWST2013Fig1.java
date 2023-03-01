/* 
 * Program from Fig.2[sic] of
 * 2013WST - Urban - Piecewise-Defined Ranking Functions
 *
 * Date: 12.12.2012
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class UrbanWST2013Fig1 {

    public static void loop(int x) {
        while (x <= 10) {
            if (x > 6) {
                x = x + 2;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            int x = args[0].length();
            loop(x);
        }
        
    }
}
