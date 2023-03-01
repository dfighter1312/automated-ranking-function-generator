/* 
 * Program from Example 2 of
 * 2004VMCAI - Podelski,Rybalchenko - A complete method for the synthesis of linear ranking functions
 *
 * Date: 18.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class PodelskiRybalchenkoVMCAI2004Ex2 {

    public static void loop(int x) {
        while ( x >= 0 ) {
		    x = -2*x + 10;
		}
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            int x = args[0].length();
            loop(x);
        }
        
    }
}
