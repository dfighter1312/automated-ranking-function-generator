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

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron2 {

	public static void loop(int tx, int x, int y) {


	while (x >= y && tx >= 0) {
		if (new Random().nextInt() != 0) {
			x = x - 1 - tx;
		} else {
			y = y + 1 + tx;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = args[0].length();
		int y = args[1].length();
		int tx = args[2].length();
		loop(x, y, tx);
		}
	}
}
