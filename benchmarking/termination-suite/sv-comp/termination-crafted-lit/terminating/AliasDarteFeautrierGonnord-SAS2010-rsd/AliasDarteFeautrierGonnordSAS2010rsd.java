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

 
public class AliasDarteFeautrierGonnordSAS2010rsd {

	public static void loop(int r) {

    int da, db, temp;

	if (r >= 0) {
		da = 2 * r;
		db = 2 * r;
		while (da >= r) {
			if (new Random().nextInt() != 0) {
				da = da - 1;
			} else {
				temp = da;
				da = db - 1;
				db = da;
			}
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
