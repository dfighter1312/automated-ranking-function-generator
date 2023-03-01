/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class ps2botht {

	public static void loop(int k) {

	
    int y, x, c;


    y = 0;
    x = 0;
    c = 0;

    while (true) {
        // __VERIFIER_assert((y * y) - 2 * x + y == 0);

        if (!(c + (y * y) - 2 * x + y < k))
            break;

        c = c + 1;
        y = y + 1;
        x = y + x;
    }
    // __VERIFIER_assert((y*y) - 2*x + y == 0);

	return;

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
