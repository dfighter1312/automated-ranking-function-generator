/*
  Compute the floor of the square root, by Dijkstra
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class dijkstra1botht {

	public static void loop(int n) {


    int p, q, r, h;



    p = 0;
    q = 1;
    r = n;
    h = 0;
    while ( q <= n ) {

        q = 4 * q;
    }


    while (r >= 2 * p + q) {
  

        q = q / 4;
        h = p + q;
        p = p / 2;
        if (r >= h) {
            p = p + q;
            r = r - h;
        }
    }
   

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
