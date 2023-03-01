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

 
public class ChawdharyCookGulwaniSagivYangESOP2008random1d {

	public static void loop(int a, int x, int max) {

	if (max > 0) {
		a = 0;
		x = 1;
		while (x <= max) {
			if (new Random().nextInt() != 0) {
				a = a + 1;
			} else {
				a = a - 1;
            }
			x = x + 1;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int a = args[0].length();
		int x = args[1].length();
		int max =args[2].length();
		loop(a, x, max);
		}
	}
}
