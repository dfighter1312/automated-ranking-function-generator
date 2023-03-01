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
 
 
public class AliasDarteFeautrierGonnordSAS2010loops {

	public static void loop(int n, int y) {


    int x;
	x = n;
	if (x >= 0 && x <= 1073741823) {
		while (x >= 0) {
			y = 1;
			if (y < x) {
				while (y < x) {
					y = 2*y;
                }
			}
			x = x - 1;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int y = args[1].length();
		loop(x,y);
		}
	}
}
