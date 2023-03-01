/*
  * Program from Fig.1a of
 * 2008CAV - Gulavani,Gulwani - A Numerical Abstract Domain Based on Expression Abstraction and Max Operator with Application in Timing Analysis
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class GulavaniGulwaniCAV2008Fig1a {

	public static void loop(int x, int y, int z, int i) {

	while (x < y) {
		i = i + 1;
		if (z > x) {
			x = x + 1;
		} else {
			z = z + 1;
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int x =args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		int i = args[3].length();
		loop(x, y, z, i);
		}
	}
}
