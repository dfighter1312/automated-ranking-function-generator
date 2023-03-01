/*
 
* Program from Fig.1 of
 * 2013CAV - Brockschmidt,Cook,Fuhs - Better termination proving through cooperation
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class BrockschmidtCookFuhsCAV2013Fig1 {

	public static void loop(int i, int j, int n) {


    while (i < n) {
        j = 0;
        while (j <= i) {
            j = j + 1;
        }
        i = i + 1;
    }
	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int i = args[0].length();
		int j = args[1].length();
		int n = args[2].length();
		loop(i, j, n);
		}
	}
}
