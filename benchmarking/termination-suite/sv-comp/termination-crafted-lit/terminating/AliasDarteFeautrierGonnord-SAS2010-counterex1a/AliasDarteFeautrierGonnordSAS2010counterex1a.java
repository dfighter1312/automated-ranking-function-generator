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
 
import java.util.Random;

 
public class AliasDarteFeautrierGonnordSAS2010counterex1a {

	public static void loop(int x, int y, int n, int b) {

	while (x >= 0 && 0 <= y && y <= n) {
		if (b == 0) {
			y = y + 1;
			if (new Random().nextInt() != 0) {
				b = 1;
            }
		} else {
			y = y - 1;
			if (new Random().nextInt() != 0) {
				x = x - 1;
				b = 0;
			}
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int n = Integer.parseInt(args[2]);
		int b = Integer.parseInt(args[3]);				
		loop(x, y, n, b);
		}
	}
}
