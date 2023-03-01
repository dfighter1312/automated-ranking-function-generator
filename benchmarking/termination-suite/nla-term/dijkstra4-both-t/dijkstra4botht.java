/*
 
*Compute the floor of the square root, by Dijkstra
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class dijkstra4botht {

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


    while (h * h * n - 4 * h * n * p + 4 * (n * n) * q - n * q * q - h * h * r + 4 * h * p * r - 8 * n * q * r + q * q * r + 4 * q * r * r + c <= k) {


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
