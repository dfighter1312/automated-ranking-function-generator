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

 
public class AliasDarteFeautrierGonnordSAS2010Fig1 {

	public static void loop(int m) {


    int x, y;
	y = 0;
    x = m;
	while (x >= 0 && y >= 0) {
		if (new Random().nextInt() != 0) {
			while (y <= m && new Random().nextInt() != 0) {
				y = y + 1;
			}
			x = x - 1;
		}
		y = y - 1;
	}
	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		loop(x);
		}
	}
}
