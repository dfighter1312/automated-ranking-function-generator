/*
 
* * Program from Fig.1 of
 * 2012SAS - Chen,Flur,Mukhopadhyay - Termination Proofs for Linear Simple Loops
 *
 * Date: 2013-12-18
 * Author: heizmann@informatik.uni-freiburg.de
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class ChenFlurMukhopadhyaySAS2012Fig1 {

	public static void loop(int x, int y, int z) {

	while (x > 0) {
		x = x + y;
		y = z;
		z = -z -1;
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		loop(x, y, z);
		}
	}
}
