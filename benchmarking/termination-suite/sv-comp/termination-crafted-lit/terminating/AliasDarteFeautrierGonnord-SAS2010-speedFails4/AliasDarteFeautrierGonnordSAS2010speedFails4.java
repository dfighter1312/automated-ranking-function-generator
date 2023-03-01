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
 

 
public class AliasDarteFeautrierGonnordSAS2010speedFails4 {

	public static void loop(int i, int x, int n, int b) {

    int t;
	if (b >= 1) {
		t = 1;
	} else {
		t = -1;
    }
	while (x <= n) {
		if (b >= 1) {
			x = x + t;
		} else {
			x = x - t;
		}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int i = args[0].length();
		int x = args[1].length();
		int n = args[2].length();
		int b = args[3].length();
		loop(i, x, n, b);
		}
	}
}
