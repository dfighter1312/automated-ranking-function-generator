/*
 extended Euclid's algorithm
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class egcdbotht {

	public static void loop(int x, int y) {

    int a, b, p, q, r, s;

    if (x>=1 && y>=1) {
        a = x;
        b = y;
        p = 1;
        q = 0;
        r = 0;
        s = 1;

        while (y * r + x * p != x * q + y * s) {

            if (a > b) {
                a = a - b;
                p = p - q;
                r = r - s;
            } else {
                b = b - a;
                q = q - p;
                s = s - r;
            }
        }
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
