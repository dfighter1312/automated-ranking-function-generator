/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class ps5botht {

	public static void loop(int k) {

    int y, x, c;

    y = 0;
    x = 0;
    c = 0;

    while (true) {
        // __VERIFIER_assert(6*y*y*y*y*y + 15*y*y*y*y + 10*y*y*y - 30*x - y == 0);

        if (!(c + 6*y*y*y*y*y + 15*y*y*y*y + 10*y*y*y - 30*x - y < k))
            break;

        c = c + 1;
        y = y + 1;
        x = y * y * y * y + x;
    }

    // __VERIFIER_assert(6*y*y*y*y*y + 15*y*y*y*y + 10*y*y*y - 30*x - y == 0);
    // __VERIFIER_assert(k*y == y*y);
    return;
	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
