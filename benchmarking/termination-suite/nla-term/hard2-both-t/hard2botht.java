/*
 
  hardware integer division program, by Manna
  returns q==A//B
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class hard2botht {

	public static void loop(int A) {


    int B;
    int r, d, p, q;

    B = 1;

    r = A;
    d = B;
    p = 1;
    q = 0;

    while (true) {
        // __VERIFIER_assert(q == 0);
        // __VERIFIER_assert(r == A);
        // __VERIFIER_assert(d == B * p);
        if (!(B * p - d + r >= d)) break;

        d = 2 * d;
        p = 2 * p;
    }

    while (true) {
        // __VERIFIER_assert(A == q*B + r);
        // __VERIFIER_assert(d == B*p);

        if (!(q*B + r - A + p != 1)) break;

        d = d / 2;
        p = p / 2;
        if (r >= d) {
            r = r - d;
            q = q + p;
        }
    }

    // __VERIFIER_assert(A == d*q + r);
    // __VERIFIER_assert(B == d);    


	}

	public static void main(String[] args) {
	if (args.length >= 1) {
		int x = Integer.parseInt(args[0]);
		loop(x);
		}
	}
}
