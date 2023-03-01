/*
 
Printing consecutive cubes, by Cohen
http://www.cs.upc.edu/~erodri/webpage/polynomial_invariants/cohencu.htm
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class cohencu6botht {

	public static void loop(int a) {

    int n, x, y, z;
    n = 0;
    x = 0;
    y = 1;
    z = 6;

    while (n <= a) {

        n = n + y - 3*n*n - 3*n; 
        x = x + y;
        y = y + z;
        z = z + 6;
    }

	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
