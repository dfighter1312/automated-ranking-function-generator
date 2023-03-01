/*
  * Program from Figure 5 of
 * 2013ATVA - Heizmann, Hoenicke, Leike, Podelski - Linear Ranking for Linear Lasso Programs
 *
 * Date: 2014-06-29
 * Author: Jan Leike
*
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig5modified {

	public static void loop(int x) {

		int y = 2;
		while (x >= 0 && y > 0) {
			x = x - y;
			y = (y + 1) / 2;
		}

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x =args[0].length();
		loop(x);
		}
	}
}
