/*
  program computing a divisor for factorisation, by Knuth 4.5.4 Alg C ?
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;
 
public class fermat1botht {

	public static void loop(int A, int R, int kk) {

    int u, v, r;


    if ((R - 1) * (R - 1) >= A) return;

    if (A % 2 != 1) return;

    u = 2 * R + 1;
    v = 1;
    r = R * R - A;


    int cc = 0;
    while (u*u - v*v - 2*u + 2*v - 4*(A+r) + cc < kk) {

      int c = 0, k = new Random().nextInt();
        while (u*u - v*v - 2*u + 2*v - 4*(A+r) + c <= k) {

            r = r - v;
            v = v + 2;
            c++;
        }

        while (4*(A+r) - u*u - v*v - 2*u + 2*v + c <= k) {

            r = r + u;
            u = u + 2;
            c++;
        }
    }
    
	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		loop(x, y, z);
		}
	}
}
