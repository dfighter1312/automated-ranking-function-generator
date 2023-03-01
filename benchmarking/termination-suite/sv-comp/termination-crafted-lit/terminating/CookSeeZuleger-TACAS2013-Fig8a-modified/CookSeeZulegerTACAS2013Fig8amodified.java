/*
  * Modified variant of the program depicted in Fig.8a of
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

 
public class CookSeeZulegerTACAS2013Fig8amodified {

	public static void loop(int K, int x) {


    while (x != K) {
        if (x > K) {
            x = x - 1;
        } else {
            x = x + 1;
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
