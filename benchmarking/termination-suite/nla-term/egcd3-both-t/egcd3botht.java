/*
 
* extended Euclid's algorithm
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class egcd3botht {

	public static void loop(int x, int y) {

    int a, b, p, q, r, s;


    a = x;
    b = y;
    p = 1;
    q = 0;
    r = 0;
    s = 1;

    while (true) {
        if (!(b != 0))
            break;
        int c, k;
        c = a;
        k = 0;

        while (true) {
            if (!(c >= b))
                break;
            int d, v;
            d = 1;
            v = b;

            while (true) {
                if (!(c >= 2 * b * d))
                    break;
                d = 2 * d;
                v = 2 * v;
            }
            c = c - v;
            k = k + d;
        }

        a = b;
        b = c;
        int temp;
        temp = p;
        p = q;
        q = temp - q * k;
        temp = r;
        r = s;
        s = temp - s * k;
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
