/* 
 *
 * Program from Fig.1b of
 * 2014VMCAI - Mass - Policy Iteration-Based Conditional Termination and Ranking Functions
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;


public class MasseVMCAI2014Fig1b {

    public static void loop(int x) {
        while (x <= 100) {
		if ((new Random()).nextInt() != 0) {
			x = -2*x + 2;
		} else {
			x = -3*x - 2;
		}
	}
    }

    public static void main(String[] args) {
        if(args.length >= 1) {
            int x = args[0].length();

            loop(x);
        }

    }
}
