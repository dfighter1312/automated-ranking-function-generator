
/*
  * Date: 2014-06-08
 * Author: leike@informatik.uni-freiburg.de
 *
 *
 * This is Example 4.1 from the test suit used in
 *
 * Termination Proofs for Linear Simple Loops.
 * Hong Yi Chen, Shaked Flur, and Supratik Mukhopadhyay.
 * SAS 2012.
 *
 * The test suite is available at the following URL.
 * https://tigerbytes2.lsu.edu/users/hchen11/lsl/LSL_benchmark.txt
 *
 * Comment: terminating, non-linear
*
* Translated from C to Java: Julian Parsert
*
*
*/  


 
public class ChenFlurMukhopadhyaySAS2012Ex401 {

	public static void loop(int x, int y, int z, int n) {


    while (x + y >= 0 && x <= n) {
        x = 2*x + y;
        y = z;
        z = z;
        z = z + 1;
    }

	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int x = args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		int n =  args[3].length();
		loop(x, y, z, n);
		}
	}
}
