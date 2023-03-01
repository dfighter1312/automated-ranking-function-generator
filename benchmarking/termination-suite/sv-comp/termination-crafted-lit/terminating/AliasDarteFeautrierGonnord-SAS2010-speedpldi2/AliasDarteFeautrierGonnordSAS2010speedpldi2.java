/*
 
* * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
 *
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class AliasDarteFeautrierGonnordSAS2010speedpldi2 {

	public static void loop(int n, int m) {

		int v1, v2;
		if (n >= 0 && m > 0) {
			v1 = n;
			v2 = 0;
			while (v1 > 0) {
				if (v2 < m) {
					v2 = v2 + 1;
					v1 = v1 - 1;
				} else {
					v2 = 0;
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args.length >= 2) {
			int n = args[0].length();
			int m = args[1].length();
			loop(n, m);
		}
	}
}
