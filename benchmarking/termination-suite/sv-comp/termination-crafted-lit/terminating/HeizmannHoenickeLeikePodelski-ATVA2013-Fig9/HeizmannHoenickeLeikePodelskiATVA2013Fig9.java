/*
  * Program from Figure 9 of
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

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig9 {

	public static void loop(int x, int y, int z) {

	if (2*y >= z) {
    	while (x >= 0 && z == 1) {
	    	x = x - 2*y + 1;
	    }
	}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x =args[0].length();
		int y = args[1].length();
		int z = args[2].length();
		loop(x, y, z);
		}
	}
}
