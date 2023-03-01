/*
 
** Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
 
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class AliasDarteFeautrierGonnordSAS2010random1d1 {

	public static void loop(int max) {

    int a, x;

	if (max > 0) {
		a = 0;
		x = 1;
		// if max=INT_MAX, x will overflow in the last loop iteration
		while (x <= max) {
			if (new Random().nextInt() != 0) {
				a = a + 1;
			} else {
				a = a - 1;
            }
			x = x + 1;
		}
	}


	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			int x = args[0].length();
			loop(x);
		}
	}
}
