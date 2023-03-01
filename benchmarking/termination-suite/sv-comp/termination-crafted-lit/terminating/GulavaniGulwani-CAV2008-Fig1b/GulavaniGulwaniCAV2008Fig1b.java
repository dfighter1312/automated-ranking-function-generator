/*
  * Program from Fig.1b of
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

 
public class GulavaniGulwaniCAV2008Fig1b {

	public static void loop(int x, int i, int n, int m) {

	while (x < n) {
		i = i + 1;
		x = x + 1;
	}
	while (x < m) {
		i = i + 1;
		x = x + 1;
	}


	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int x = args[0].length();
		int i = args[1].length();
		int n = args[2].length();
		int m = args[3].length();
		loop(x,i,n,m);
		}
	}
}
