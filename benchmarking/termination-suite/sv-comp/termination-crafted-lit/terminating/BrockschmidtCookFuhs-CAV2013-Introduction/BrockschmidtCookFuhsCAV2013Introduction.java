/*
  * Program from the introduction of
 * 2013CAV - Brockschmidt,Cook,Fuhs - Better termination proving through cooperation -draft
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class BrockschmidtCookFuhsCAV2013Introduction {

	public static void loop(int x) {


    int  y;
    y = 1;
    while (x > 0) {
        x = x - y;
        y = y + 1;
    }

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = args[0].length();
		loop(x);
		}
	}
}
