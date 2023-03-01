/*
  * Program used in the experimental evaluation of the following paper.
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class AliasDarteFeautrierGonnordSAS2010exmini {

	public static void loop(int i, int j, int k, int tmp) {


	while (i <= 100 && j <= k) {
		tmp = i;
		i = j;
		j = tmp + 1;
		k = k - 1;
	}
}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int i = args[0].length();
		int j = args[1].length();
		int k = args[2].length();
		int tmp = args[3].length();
								
		loop(i, j, k, tmp);
		}
	}
}
