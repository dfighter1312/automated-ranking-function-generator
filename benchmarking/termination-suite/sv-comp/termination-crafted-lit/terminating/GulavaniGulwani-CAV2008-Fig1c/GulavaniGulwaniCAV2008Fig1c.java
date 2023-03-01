/*
 * Program from Fig.1c of
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

 
public class GulavaniGulwaniCAV2008Fig1c {

	public static void loop(int x, int i, int n) {

	while (x < n) {
		i = i + 1;
		x = x + 1;
	}


	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		loop(x,y,z);
		}
	}
}
