/*
  Division algorithm from
 "Z. Manna, Mathematical Theory of Computation, McGraw-Hill, 1974"
 return x1 // x2
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class mannadivbotht {

	public static void loop(int x1, int x2) {

    int y1, y2, y3;
   
    if (x1 < 0 || x2 == 0) {
        return;
    }

    y1 = 0;
    y2 = 0;
    y3 = x1;

    while (true) {
        // __VERIFIER_assert(y1*x2 + y2 + y3 == x1);

        if (!(y3 != y1*x2 + y2 + y3 - x1)) break;

        if (y2 + 1 == x2) {
            y1 = y1 + 1;
            y2 = 0;
            y3 = y3 - 1;
        } else {
            y2 = y2 + 1;
            y3 = y3 - 1;
        }
    }
    // __VERIFIER_assert(y1*x2 + y2 == x1);


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x, y);
		}
	}
}
