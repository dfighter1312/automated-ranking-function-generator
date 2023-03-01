/*
  * Program from Fig.3 of
 * 2013TACAS - Cook,See,Zuleger - Ramsey vs. Lexicographic Termination Proving
 *
 * Date: 8.6.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class CookSeeZulegerTACAS2013Fig1 {

	public static void loop(int x, int y) {

    while (x>0 && y>0) {
        if (new Random().nextInt() != 0) {
            x = x - 1;
        } else {
            x = new Random().nextInt();
            y = y - 1;
        }
    }


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x =  args[0].length();
		int y =  args[1].length();
		loop(x,y);
		}
	}
}
