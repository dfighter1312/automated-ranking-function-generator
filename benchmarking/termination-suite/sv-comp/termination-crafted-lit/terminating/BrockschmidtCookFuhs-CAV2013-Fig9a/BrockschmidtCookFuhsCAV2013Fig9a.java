/*
  * Program from Fig.9a of a technical report which is based on
 * 2013CAV - Brockschmidt,Cook,Fuhs - Better termination proving through cooperation
 *
 * Date: 2014
 * Author: Caterina Urban
 *
*
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class BrockschmidtCookFuhsCAV2013Fig9a {

	public static void loop(int k, int i, int j, int n) {

	if (k >= 1) {
		i = 0;
		while (i < n) {
			j = 0;
			while (j <= i) {
				j = j + k;
			}
			i = i + 1;
		}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int k = args[0].length();
		int i = args[1].length();
		int j = args[2].length();
		int n = args[3].length();
		
		loop(k, i, j, n);
		}
	}
}
