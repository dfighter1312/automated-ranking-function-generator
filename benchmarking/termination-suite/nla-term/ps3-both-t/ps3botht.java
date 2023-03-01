/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ps3botht {

	public static void loop(int k) {

    int  y, x, c;


    y = 0;
    x = 0;
    c = 0;

    while (true) {
        // __VERIFIER_assert(6*x - 2*y*y*y - 3*y*y - y == 0);

        if (!(c + 6*x - 2*y*y*y - 3*y*y - y < k))
            break;

        c = c + 1;
        y = y + 1;
        x = y * y + x;
    }

    return ;
	

	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			int x = Integer.parseInt(args[0]);
			loop(x);
		}
	}
}
