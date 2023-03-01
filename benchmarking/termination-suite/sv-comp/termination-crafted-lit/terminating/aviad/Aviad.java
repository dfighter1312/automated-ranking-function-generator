/* 
 * An example using integer division, given to me by Aviad Pineles
 * Date: 15.12.2013
 * Author: Amir Ben-Amram, amirben@cs.mta.ac.il
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class Aviad {

    public static void loop(int a) {
        int tmp, count = 0;
        while(a > 1) {
        tmp = a % 2;
        if(tmp == 0) a = a / 2;
        else a = a - 1;
        count++;
        }
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            int x = args[0].length();
            loop(x);
        }
        
    }
}
