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

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron3 {

	public static void loop(int x, int y, int z, int tx) {


	while (x >= y && x <= tx + z) {
		if (new Random().nextInt()!= 0) {
			z = z - 1;
			tx = x;
			x = new Random().nextInt();
		} else {
			y = y + 1;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int x = args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		int tx = args[3].length();
		loop(x,y,z,tx);
		}
	}
}
