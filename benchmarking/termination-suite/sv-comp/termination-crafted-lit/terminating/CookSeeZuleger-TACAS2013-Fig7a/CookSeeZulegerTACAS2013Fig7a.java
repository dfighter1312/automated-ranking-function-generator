/*
 	//#terminating
* * Program from Fig.7a of
 * 2013TACAS - Cook,See,Zuleger - Ramsey vs. Lexicographic Termination Proving
 *
 * Date: 9.6.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class CookSeeZulegerTACAS2013Fig7a {

	public static void loop(int x, int y, int d) {



    while (x>0 && y>0 && d>0) {
        if (new Random().nextInt() != 0) {
            x = x - 1;
            d = new Random().nextInt();
        } else {
            x = new Random().nextInt();
            y = y - 1;
            d = d - 1;
        }
 
}

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x =  args[0].length();
		int y =  args[1].length();
		int z = args[2].length();
		loop(x, y, z);
		}
	}
}
