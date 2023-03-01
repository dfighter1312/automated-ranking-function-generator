/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class ps6botht {

	public static void loop(int k) {


    int  y, x, c;


    y = 0;
    x = 0;
    c = 0;

    while (true) {
        // __VERIFIER_assert(-2*y*y*y*y*y*y - 6 * y*y*y*y*y - 5 * y*y*y*y + y*y + 12*x == 0);

        if (!(c + -2*y*y*y*y*y*y - 6 * y*y*y*y*y - 5 * y*y*y*y + y*y + 12*x < k))
            break;

        c = c + 1;
        y = y + 1;
        x = y * y * y * y * y + x;
    }
    
    // __VERIFIER_assert(-2*y*y*y*y*y*y - 6 * y*y*y*y*y - 5 * y*y*y*y + y*y + 12*x == 0);
    // __VERIFIER_assert(k*y == y*y);      
    return ;


	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
