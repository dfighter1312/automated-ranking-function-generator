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

 
public class AliasDarteFeautrierGonnordSAS2010complex {

	public static void loop(int a, int b) {

	while (a < 30) {
		while (b < a) {
			if (b > 5) {
				b = b + 7;
			} else {
				b = b + 2;
            }
			if (b >= 10 && b <= 12) {
				a = a + 10;
			} else {
				a = a + 1;
            }
		}
		a = a + 2;
		b = b - 10;
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
