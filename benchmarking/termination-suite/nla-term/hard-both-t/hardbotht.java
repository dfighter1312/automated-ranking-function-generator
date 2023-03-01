/*
 	
  hardware integer division program, by Manna
  returns q==A//B
  
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class hardbotht {

	public static void loop(int A, int B) {

    int r, d, p, q;

    //assume_abort_if_not(B >= 1);
    if (B < 1) return;

    r = A;
    d = B;
    p = 1;
    q = 0;

    while (true) {
        // __VERIFIER_assert(q == 0);
        // __VERIFIER_assert(r == A);
        // __VERIFIER_assert(d == B * p);
        if (!(r >= B * p)) break;

        d = 2 * d;
        p = 2 * p;
    }

    while (true) {
        // __VERIFIER_assert(A == q*B + r);
        // __VERIFIER_assert(d == B*p);

        if (!(A - q*B - r + p != 1)) break;

        d = d / 2;
        p = p / 2;
        if (r >= d) {
            r = r - d;
            q = q + p;
        }
    }



	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x,y);
		}
	}
}
