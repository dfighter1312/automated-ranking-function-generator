/* 
 * Program from Fig.3 of
 * 2014ESOP - Urban,Miné - An Abstract Domain to Infer Ordinal-Valued Ranking Functions
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;

public class UrbanMineESOP2014Fig3 {

    public static void loop(int x, int y) {
       while (x != 0 && y > 0) {
	    if (x > 0) {
		    if ((new Random()).nextInt() != 0) {
			    x = x - 1;
				y = (new Random()).nextInt();
			} else {
			    y = y - 1;
			}
		} else {
		    if ((new Random()).nextInt() != 0) {
			    x = x + 1;
			} else {
			    y = y - 1;
				x =(new Random()).nextInt();
			}		
		}
	}
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            int x = args[0].length();
            int y = args[1].length();
            loop(x, y);
        }
        
    }
}
