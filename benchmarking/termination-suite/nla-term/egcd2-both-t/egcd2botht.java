/*
 
*  extended Euclid's algorithm
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class egcd2botht {

	public static void loop(int x, int y) {

    int a, b, p, q, r, s, c, k;


    if (x<1) {
	return;
    }

    if (y < 1) {
	return;
    }

    a = x;
    b = y;
    p = 1;
    q = 0;
    r = 0;
    s = 1;
    c = 0;
    k = 0;
    while (true) {
        if (!(b != 0))
            break;
        c = a;
        k = 0;

        while (c >= x * q + y * s) {

            c = c - b;
            k = k + 1;
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
