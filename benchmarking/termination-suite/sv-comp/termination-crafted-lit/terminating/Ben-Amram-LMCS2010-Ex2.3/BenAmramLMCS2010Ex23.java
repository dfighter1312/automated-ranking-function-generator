/*
  * Program from Ex.2.3 of
 * 2010LMCS - Ben-Amram - Size-Change Termination, Monotonicity Constraints and Ranking Functions
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class BenAmramLMCS2010Ex23 {

	public static void loop(int x, int y, int z) {


	while (x > 0 && y > 0 && z > 0) {
		if (y > x) {
			y = z;
			x = new Random().nextInt();
			z = x - 1;
		} else {
			z = z - 1;
			x = new Random().nextInt();
			y = x - 1;
		}

		}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		loop(x, y, z);
		}
	}
}
