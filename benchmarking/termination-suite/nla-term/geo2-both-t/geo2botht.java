/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class geo2botht {

	public static void loop(int z, int k) {


    int x, y, c;

    x = 1;
    y = 1;
    c = 1;

    while (true) {
        // __VERIFIER_assert(1 + x*z - x - z*y == 0);

        if (!(1 + x*z - x - z*y + c < k))
            break;

        c = c + 1;
        x = x * z + 1;
        y = y * z;
    }
    // __VERIFIER_assert(1 + x*z - x - z*y == 0);


	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x, y);
		}
	}
}
