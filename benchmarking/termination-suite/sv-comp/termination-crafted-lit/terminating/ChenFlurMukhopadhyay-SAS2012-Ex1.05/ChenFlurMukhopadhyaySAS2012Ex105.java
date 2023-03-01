/*
  * Date: 2014-06-08
 * Author: leike@informatik.uni-freiburg.de
 *
 *
 * This is Example 1.5 from the test suit used in
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

 
public class ChenFlurMukhopadhyaySAS2012Ex105 {

	public static void loop(int x, int oldx) {


    while (x > 0 && 2*x <= oldx) {
        oldx = x;
        x = new Random().nextInt();
    }

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		int y = args[1].length();
		loop(x,y);
		}
	}
}
