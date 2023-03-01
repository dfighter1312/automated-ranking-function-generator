/*
  * Program used in the experimental evaluation of the following paper.
 * 2008ESOP - Chawdhary,Cook,Gulwani,Sagiv,Yang - Ranking Abstractions
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron12 {

	public static void loop(int x, int y, int z) {

	while (x >= y) {
		if (new Random().nextInt() != 0) {
			x = x + 1;
			y = y + x;
		} else {
			x = x - z;
			y = y + (z * z);
			z = z - 1;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = args[0].length();
		int y =args[1].length();
		int z = args[2].length();
		loop(x,y,z);
		}
	}
}
