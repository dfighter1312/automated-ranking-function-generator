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

 
public class AliasDarteFeautrierGonnordSAS2010counterex1b {

	public static void loop(int x, int y, int n) {

	while (x >= 0) {
		while (y >= 0 && new Random().nextInt() != 0) {
			y = y - 1;
        }
		x = x - 1;
		while (y <= n && new Random().nextInt() != 0) {
			y = y + 1;
        }
	}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		loop(x, y, z);
		}
	}
}
