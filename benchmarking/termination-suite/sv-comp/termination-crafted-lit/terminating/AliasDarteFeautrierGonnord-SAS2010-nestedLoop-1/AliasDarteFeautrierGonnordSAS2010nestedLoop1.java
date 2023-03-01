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
 
 
public class AliasDarteFeautrierGonnordSAS2010nestedLoop1 {

	public static void loop(int i, int j, int k, int n, int m, int N) {


	if (0 <= n && 0 <= m && 0 <= N) {
		i = 0;
		while (i < n) {
			j = 0;
			while (j < m) {
				j = j + 1;
				k = i;
				while (k < N - 1) {
					k = k + 1;
                }
				i = k;
			}
			i = i + 1;
		}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 6) {
		int i = args[0].length();
		int j = args[1].length();
		int k = args[2].length();
		int n = args[3].length();
		int m = args[4].length();
		int N = args[5].length();
		loop(i, j, k, n, m, N);
		}
	}
}
