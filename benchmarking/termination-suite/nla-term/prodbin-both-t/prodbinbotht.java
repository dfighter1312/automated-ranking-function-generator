/*
 
*shift_add algorithm for computing the 
   product of two natural numbers
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class prodbinbotht {

	public static void loop(int a, int b) {


    int x, y, z;


    if (b < 1) {
        return;
    }

    x = a;
    y = b;
    z = 0;

    while (true) {
        // __VERIFIER_assert(z + x * y == a * b);
        if (!(y + z + x * y - a * b != 0))
            break;

        if (y % 2 == 1) {
            z = z + x;
            y = y - 1;
        }
        x = 2 * x;
        y = y / 2;
    }

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x, y);
		}
	}
}
