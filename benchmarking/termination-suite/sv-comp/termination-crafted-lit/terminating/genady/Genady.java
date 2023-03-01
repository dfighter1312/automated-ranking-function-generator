/* 
 * An example that looks simple, given to me by Genady Trifon
 * Date: 15.12.2013
 * Author: Amir Ben-Amram, amirben@cs.mta.ac.il
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */



import java.util.Random;

public class Genady {

    public static void loop() {
        int i, j;
        j = 1;
        i = 10000;
        while (i-j >= 1) {
            j = j + 1;
            i = i - 1;
        }  
    }

    public static void main(String[] args) {
        loop();
    }
}
