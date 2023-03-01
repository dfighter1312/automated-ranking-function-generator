/*
  * Program from Figure 7 of
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

 
public class HeizmannHoenickeLeikePodelskiATVA2013Fig7 {

	public static void loop(int a_length) {

	if (a_length <  1) {
		return ;
	}
	int[] a = new int[a_length];
	for (int j = 0; j < a_length; j++) {
		a[j] = new Random().nextInt();
	}
	int offset = 1;
	int i = 0;
	while (i < a_length) {
		if (a[i] < 0) {
			break;
		}
		i = i + offset + a[i];
	}

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		loop(x);
		}
	}
}
