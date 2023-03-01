/* 
 * Program from Fig.1 of
 * 2013WST - Urban - Piecewise-Defined Ranking Functions
 *
 * Date: 12.12.2012
 * Author: heizmann@informatik.uni-freiburg.de
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class UrbanWST2013Fig2modified1000 {

    public static void loop(int x1, int x2) {
        while (x1 <= 10) {
            x2 = 1000;
            while (x2 > 1) {
                x2 = x2 -1;
            }
            x1 = x1 + 1;
        }
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            int x = args[0].length();
            int y = args[1].length();
            loop(x,y);
        }
        
    }
}
