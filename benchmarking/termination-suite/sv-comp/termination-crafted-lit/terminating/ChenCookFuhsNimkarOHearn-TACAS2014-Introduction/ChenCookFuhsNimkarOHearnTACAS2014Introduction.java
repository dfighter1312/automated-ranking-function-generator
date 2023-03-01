/*
  * Program from the example depicted in the introduction of
 * 2014TACAS - Chen,Cook,Fuhs,Nimkar,O’Hearn - Proving Nontermination via Safety
 *
 * Date: 2014-06-28
 * Author: Matthias Heizmann
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ChenCookFuhsNimkarOHearnTACAS2014Introduction {

	public static void loop(int k, int i) {

	if (k >= 0) {
		// skip
	} else {
		i = -1;
	}
	while (i >= 0) {
		i = new Random().nextInt();
	}
	i = 2;


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int t = args[1].length();
		loop(x,t);
		}
	}
}
