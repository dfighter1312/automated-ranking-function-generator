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
 
 
public class AliasDarteFeautrierGonnordSAS2010while2 {

	public static void loop(int N) {

    int i, j;

	i = N;
	while (i > 0) {
		j = N;
		while (j > 0) {
			j = j - 1;
        }
		i = i - 1;
	}


	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		loop(x);
		}
	}
}
