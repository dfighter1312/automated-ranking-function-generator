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
 
import java.util.Random;

 
public class AliasDarteFeautrierGonnordSAS2010speedpldi4 {

	public static void loop(int n, int m) {

		int i;
		if (m > 0 && n > m) {
			i = n;
			while (i > 0) {
				if (i < m) {
					i = i - 1;
				} else {
					i = i - m;
		        }
			}
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
