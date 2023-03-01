/* 
 * Terminating program which has a r.f. based on minimum
 *
 * Date: 15.12.2013
 * Author: Amir Ben-Amram, amirben@cs.mta.ac.il
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */

import java.util.Random;

public class MinRf {

    public static void loop(int x, int y) {
      int z;
      while (y > 0 && x > 0) {
        if (x>y) {
            z = y;
        } else {
            z = x;
        }
        if (new Random().nextInt() != 0) {
            y = y+x;
            x = z-1;
            z = y+z;
        } else {
            x = y+x;
            y = z-1;
            z = x+z;
        }
      }
    }

    public static void main(String[] args) {
        if (args.length >= 2){
            int x = args[0].length();
            int y = args[1].length();
            loop(x, y);
        }
    }
}
