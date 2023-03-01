/*
  * Date: 2014-06-08
 * Author: leike@informatik.uni-freiburg.de
 *
 *
 * This is Example 2.20 from the test suit used in
 *
 * Termination Proofs for Linear Simple Loops.
 * Hong Yi Chen, Shaked Flur, and Supratik Mukhopadhyay.
 * SAS 2012.
 *
 * The test suite is available at the following URL.
 * https://tigerbytes2.lsu.edu/users/hchen11/lsl/LSL_benchmark.txt
 *
 * Comment: terminating, linear
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ChenFlurMukhopadhyaySAS2012Ex220 {

	public static void loop(int x, int y) {

    while (x > y && y >= 1 && y <= 2) {
        x = x - y;
        y = new Random().nextInt();
    }

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int z = args[1].length();
		loop(x,z);
		}
	}
}
