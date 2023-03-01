/*
 
 * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and  *  Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
* Translated from C to Java: Julian Parsert
*
*
*/


import java.util.Random;

public class AliasDarteFeautrierGonnordSAS2010aaron3 {

	public static void loop(int x, int y, int z , int tx) {


	while (x >= y && x <= tx + z) {
		if (new Random().nextInt()>= 1) {
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
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		int tx = Integer.parseInt(args[3]);
		loop(x, y, z, tx);
	}
	}
}
