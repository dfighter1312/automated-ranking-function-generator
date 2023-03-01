/*
  * Program from Figure 8 of
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

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig8 {

	public static void loop(int x, int y) {

	if (2*y >= 1) {
    	while (x >= 0) {
	    	x = x - 2*y + 1;
	    }
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
