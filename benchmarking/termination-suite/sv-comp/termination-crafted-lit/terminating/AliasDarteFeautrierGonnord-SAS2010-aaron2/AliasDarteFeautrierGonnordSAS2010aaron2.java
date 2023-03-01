	/*
 * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 *
 * Translated from C to java: Julian Parsert
 *
 */

import java.util.Random;


public class AliasDarteFeautrierGonnordSAS2010aaron2 {

	public static void loop(int tx, int x, int y) {

		while (x >= y && tx >= 0) {
			if (new Random().nextInt() >= 0) {
				x = x - 1 - tx;
			} else {
				y = y + 1 + tx;
			}
		}
	}


	public static void main(String[] args) {
		if (args.length >= 3) {
			int tx = args[0].length();
			int x = args[1].length();
			int y = args[2].length();
			loop(tx, x, y);
		}
	}
}
