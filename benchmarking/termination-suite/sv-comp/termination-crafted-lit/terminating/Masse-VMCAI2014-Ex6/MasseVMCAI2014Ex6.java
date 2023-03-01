/* 
 *
 * Program from Ex6 of
 * 2014VMCAI - Masse - Policy Iteration-Based Conditional Termination and Ranking Functions
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class MasseVMCAI2014Ex6 {

    public static void loop(int x, int y) {
        while (x >= 0) {
            x = x + y;
            if (y >= 0) {
                y = y - 1;
            }
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
