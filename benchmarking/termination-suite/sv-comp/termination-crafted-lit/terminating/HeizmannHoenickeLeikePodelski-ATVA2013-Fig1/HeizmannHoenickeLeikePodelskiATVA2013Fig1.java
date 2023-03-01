/*
  * Program from Figure 1 of
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

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig1 {

	public static void loop(int x) {

    int  y;

	y = 23;
	while (x >= 0) {
		x = x - y;
		y = y + 1;
	}
	
	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x =args[0].length();
		loop(x);
		}
	}
}
