/*
  * Program from Figure 2 of
 * 2013ATVA - Heizmann, Hoenicke, Leike, Podelski - Linear Ranking for Linear Lasso Programs
 *
 * Date: 2014-06-29
 * Author: Jan Leike
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig2 {

	public static void loop(int y) {

	int x = y + 42;
	while (x >= 0) {
		y = 2*y - x;
		x = (y + x) / 2;
	}

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x =args[0].length();
		loop(x);
		}
	}
}
