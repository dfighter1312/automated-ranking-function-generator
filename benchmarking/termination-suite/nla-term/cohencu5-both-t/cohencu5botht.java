/*
 
Printing consecutive cubes, by Cohen
http://www.cs.upc.edu/~erodri/webpage/polynomial_invariants/cohencu.htm
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class cohencu5botht {

	public static void loop(int a, int k) {

    int n, x, y, z;
    n = 0;
    x = 0;
    y = 1;
    z = 6;
    int c = 0;
    
    while ((z*z) - 12*y - 6*z + 12 + c <= k) {

        n = n + 1;
        x = x + y;
        y = y + z;
        z = z + 6;
        c = c + 1;
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
