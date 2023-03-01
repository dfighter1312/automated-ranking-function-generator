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

 
public class ChawdharyCookGulwaniSagivYangESOP2008easy2 {

	public static void loop(int z) {

	
	int x = 12, y = 0;

	while (z > 0) {
		x = x + 1;
		y = y - 1;
		z = z - 1;
	}


	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		loop(x);
		}
	}
}
