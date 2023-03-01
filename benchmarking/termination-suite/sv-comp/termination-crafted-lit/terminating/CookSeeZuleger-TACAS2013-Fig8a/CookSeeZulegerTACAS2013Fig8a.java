/*
  * Program depicted in Fig.8a of
 * 2013TACAS - Cook,See,Zuleger - Ramsey vs. Lexicographic Termination Proving
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class CookSeeZulegerTACAS2013Fig8a {

	public static void loop(int x) {


	while (x != 0) {
		if (x > 0) {
			x = x - 1;
		} else {
			x = x + 1;
		}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x =  args[0].length();
		loop(x);
		}
	}
}
