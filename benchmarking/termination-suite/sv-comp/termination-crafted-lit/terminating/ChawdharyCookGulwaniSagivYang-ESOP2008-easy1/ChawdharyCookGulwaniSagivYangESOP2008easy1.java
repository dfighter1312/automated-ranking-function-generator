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
 

 
public class ChawdharyCookGulwaniSagivYangESOP2008easy1 {

	public static void loop(int z) {

		int x = 0, y = 100;
		while (x < 40) {
			if (z == 0) {
				x = x + 1;
			} else {
				x = x + 2;
			}
		}

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int z =args[0].length();
		loop(z);
		}
	}
}
