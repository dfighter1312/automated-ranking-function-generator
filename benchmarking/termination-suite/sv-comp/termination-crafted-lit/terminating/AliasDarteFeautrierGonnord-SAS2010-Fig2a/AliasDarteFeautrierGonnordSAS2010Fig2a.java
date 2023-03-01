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

 
public class AliasDarteFeautrierGonnordSAS2010Fig2a {

	public static void loop(int x, int y) {


	while (x >= 2) {
		x = x - 1;
        y = y + x;
		while (y >= x && new Random().nextInt() != 0) {
			y = y - 1;
		}
		x = x - 1;
        y = y - x;
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
