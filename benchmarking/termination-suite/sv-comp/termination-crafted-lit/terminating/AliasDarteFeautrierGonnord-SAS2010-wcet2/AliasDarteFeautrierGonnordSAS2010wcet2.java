/*
  * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/ 
 
public class AliasDarteFeautrierGonnordSAS2010wcet2 {

	public static void loop(int i, int j) {
		while (i < 5) {
			j = 0;
			while (i > 2 && j <= 9) {
				j = j + 1;
			}
			i = i + 1;
		}
	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int y = args[1].length();
		loop(x, y);
		}
	}
}
