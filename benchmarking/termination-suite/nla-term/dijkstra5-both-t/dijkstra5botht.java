/*
 Compute the floor of the square root, by Dijkstra
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 


 
public class dijkstra5botht {

	public static void loop(int n, int k) {

    int p, q, r, h;

    p = 0;
    q = 1;
    r = n;
    h = 0;
    int c = 0;
    while ( q <= n ) {


        q = 4 * q;
    }


    while (h * h * p - 4 * h * n * q + 4 * n * p * q - p * q * q + 4 * h * q * r - 4 * p * q * r + c <= k) {

        q = q / 4;
        h = p + q;
        p = p / 2;
        if (r >= h) {
            p = p + q;
            r = r - h;
        }
        c++;
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
