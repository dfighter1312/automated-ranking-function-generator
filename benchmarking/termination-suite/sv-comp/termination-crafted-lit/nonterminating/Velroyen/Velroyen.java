/* 
 * Terminating Program for x < -5 OR 0 <= x <= 30 OR x > 35
 * (from benchmarks of) 2008TAP - Velroyen,Rummer - Non-Termination Checking for Imperative Programs
 *
 * Date: 18.12.2013
 * Author: urban@di.ens.fr
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class Velroyen {

    public static void loop(int x) {
        while (x!=0) {
	    if (-5 <= x && x <= 35) {
		    if (x < 0) {
			    x = -5;
			} else {
			    if (x > 30) {
				    x = 35;
				} else {
					x = x - 1;
				}
			}
		} else {
		    x = 0;
		}
	}
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            int x = Integer.parseInt(args[0]);
            loop(x);
        }
        
    }
}
