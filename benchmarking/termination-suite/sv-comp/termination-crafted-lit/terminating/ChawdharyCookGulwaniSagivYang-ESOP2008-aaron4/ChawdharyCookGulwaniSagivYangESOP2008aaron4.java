/*
  * Program used in the experimental evaluation of the following paper.
 * 2008ESOP - Chawdhary,Cook,Gulwani,Sagiv,Yang - Ranking Abstractions
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron4 {

	public static void loop(int i, int j, int k, int an, int bn, int tk) {

	while (((an >= i && bn >= j) || (an >= i && bn <= j) || (an <= i && bn >= j)) && k >= tk + 1) {
		if (an >= i && bn >= j) {
			if (new Random().nextInt() != 0) {
				j = j + k;
				tk = k;
				k =new Random().nextInt();
			} else {
				i = i + 1;
			}
		} else {if (an >= i && bn <= j) {
			i = i + 1;
		} else {if (an <= i && bn >= j) {
			j = j + k;
			tk = k;
			k = new Random().nextInt();
		}}}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 6) {
		int i = args[0].length();
		int j = args[1].length();
		int k = args[2].length();
		int an = args[3].length();
		int bn = args[4].length();
		int tk = args[5].length();
		
		loop(i,j,k,an,bn,tk);
		}
	}
}
