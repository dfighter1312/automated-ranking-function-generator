/*
 //#terminating
* * Program from Fig.7b of
 * 2013TACAS - Cook,See,Zuleger - Ramsey vs. Lexicographic Termination Proving
 *
 * Date: 9.6.2013
 * Author: heizmann@informatik.uni-freiburg.de
 
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class CookSeeZulegerTACAS2013Fig7b {

	public static void loop(int x, int y, int z) {

	
	while (x>0 && y>0 && z>0) {
		if (new Random().nextInt() != 0) {
			x = x - 1;
		} else {if (new Random().nextInt() != 0) {
			y = y - 1;
			z = new Random().nextInt();
		} else {
			z = z - 1;
			x = new Random().nextInt();
		}}
	}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x =  args[0].length();
		int y =  args[1].length();
		int z =  args[2].length();
		loop(x, y, z);
		}
	}
}
