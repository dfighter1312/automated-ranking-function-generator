/*
  * Program depicted in Fig.8b of
 * 2013TACAS - Cook,See,Zuleger - Ramsey vs. Lexicographic Termination Proving
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class CookSeeZulegerTACAS2013Fig8b {

	public static void loop(int x, int M) {


	if (M > 0) {
		while (x != M) {
			if (x > M) {
				x = 0;
			} else {
				x = x + 1;
            }
		}
	}


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int M = args[1].length();
		loop(x,M);
		}
	}
}
