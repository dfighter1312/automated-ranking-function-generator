/* 
 *
 * Program from Example 7 of
 * 2014TACAS - Leike, Heizmann - Ranking Templates for Linear Loops
 * 
 * In the conference version of this paper, the authors claimed that this
 * lasso program does not have a multiphase ranking function. However, this
 * lasso program has the following 2-phase ranking function.
 *     f_0(x, y) = 2q + z
 *     f_1(x, y) = q
 * The authors thank Samir Genaim for pointing out this error in their paper.
 *
 * Date: 2014-06-29
 * Author: Jan Leike
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

public class LeikeHeizmannTACAS2014Ex7 {

    public static void loop(int q, int z) {
        while (q > 0) {
	    	q = q + z - 1;
		    z = -z;
	    }
    }

    public static void main(String[] args) {
        if(args.length >= 2) {
            int q = args[0].length();
            int z = args[1].length();

            loop(q, z);
        }

    }
}
