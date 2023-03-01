/*
  * Program from Fig.1 of
 * 2001TACAS - Colon,Sipma - Synthesis of Linear Ranking Functions
 *
 * Date: 2014-06-21
 * Author: Caterina Urban, Matthias Heizmann
 *
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 


 
public class ColonSipmaTACAS2001Fig1 {

	public static void loop(int k, int i, int j) {


		int tmp;

		while (i <= 100 && j <= k) {
			tmp = i;
			i = j;
			j = tmp + 1;
			k = k - 1;
		}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int k =  args[0].length();
		int i =  args[1].length();
		int j =  args[2].length();
		loop(k,i,j);
		}
	}
}
