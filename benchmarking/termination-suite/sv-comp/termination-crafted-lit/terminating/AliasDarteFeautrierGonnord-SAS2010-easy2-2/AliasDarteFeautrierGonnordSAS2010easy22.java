/*
 
* * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
* Translated from C to Java: Julian Parsert
*
*
*/  
 
public class AliasDarteFeautrierGonnordSAS2010easy22 {

	public static void loop(int z) {
		int x = 0, y = 0;
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
