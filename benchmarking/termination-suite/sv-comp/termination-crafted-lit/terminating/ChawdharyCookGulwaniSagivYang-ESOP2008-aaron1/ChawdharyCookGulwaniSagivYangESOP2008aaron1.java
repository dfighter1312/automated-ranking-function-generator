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

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron1 {

	public static void loop(int i, int j, int an, int bn) {

	while ((an >= i && bn >= j) || (an >= i && bn <= j) || (an <= i && bn >= j)) {
		if (an >= i && bn >= j) {
			if (new Random().nextInt() != 0) {
				j = j + 1;
			} else {
				i = i + 1;
			}
		} else {if (an >= i && bn <= j) {
			i = i + 1;
		} else {if (an <= i && bn >= j) {
			j = j + 1;
		}}}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 4) {
		int i = args[0].length();
		int j = args[1].length();
		int an = args[2].length();
		int bn = args[3].length();
		loop(i, j, an, bn);
		}
	}
}
