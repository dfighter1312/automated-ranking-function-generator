/* 
 *
 * Program from Fig.1a of
 * 2014VMCAI - Mass - Policy Iteration-Based Conditional Termination and Ranking Functions
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class MasseVMCAI2014Fig1a {

    public static void loop(int a, int b) {
        while (a >= 0) {
            a = a + b;
            if (b >= 0) {
                b = -b - 1;
            } else {
                b = -b;
            }
        }
    }

    public static void main(String[] args) {
        if(args.length >= 1) {
            int x = args[0].length();
            int y = args[1].length();

            loop(x, y);
        }

    }
}
