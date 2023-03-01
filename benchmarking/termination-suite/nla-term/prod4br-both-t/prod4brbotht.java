/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class prod4brbotht {

	public static void loop(int x, int y) {

    int a, b, p, q;

    if (y < 1) {
        return;
    }

    a = x;
    b = y;
    p = 1;
    q = 0;

    while (true) {
        // __VERIFIER_assert(q + a * b * p == x * y);

        if (!(a != 0 && b + q + a * b * p - x * y != 0))
            break;

        if (a % 2 == 0 && b % 2 == 0) {
            a = a / 2;
            b = b / 2;
            p = 4 * p;
        } else if (a % 2 == 1 && b % 2 == 0) {
            a = a - 1;
            q = q + b * p;
        } else if (a % 2 == 0 && b % 2 == 1) {
            b = b - 1;
            q = q + a * p;
        } else {
            a = a - 1;
            b = b - 1;
            q = q + (a + b + 1) * p; /*fix a bug here---  was (a+b-1)*/
        }
    }



	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x,y);
		}
	}
}
