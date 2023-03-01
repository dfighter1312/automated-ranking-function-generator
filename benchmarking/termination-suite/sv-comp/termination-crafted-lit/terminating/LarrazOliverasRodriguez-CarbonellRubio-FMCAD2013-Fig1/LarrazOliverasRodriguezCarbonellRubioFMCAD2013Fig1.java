/* 
 *
 * Program from Fig.1 of
 * 2013FMCAD - Larraz,Oliveras,Rodriguez-Carbonell,Rubio - Proving Termination of Imperative Programs Using Max-SMT
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class LarrazOliverasRodriguezCarbonellRubioFMCAD2013Fig1 {

    public static void loop(int x, int y, int z) {
        if (x <= 10000 && x >= -10000 && y <= 10000 && z <= 10000) {
            while (y >= 1) {
                x = x - 1;
                while (y < z) {
                    x = x + 1;
                    z = z - 1;
                }
                y = x + y;
            }
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 3) {
            int x =args[0].length();
            int y =args[1].length();
            int z =args[2].length();

            loop(x, y, z);
        }

    }
}
