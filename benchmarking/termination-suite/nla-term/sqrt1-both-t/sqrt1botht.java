/*
 
	// https://github.com/sosy-lab/sv-benchmarks/blob/master/c/nla-digbench/sqrt1.c
 Compute the floor of the square root of a natural number 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class sqrt1botht {

	public static void loop(int n, int k) {

    int a, s, t, c;


    a = 0;
    s = 1;
    t = 1;
    c = 0;

    while (t*t - 4*s + 2*t + 1 + c <= k) {
      //__VERIFIER_assert(t == 2*a + 1);
      //__VERIFIER_assert(s == (a + 1) * (a + 1));
      //__VERIFIER_assert(t*t - 4*s + 2*t + 1 == 0);
        // the above 2 should be equiv to 
      //if (!(s <= n))break;
        a = a + 1;
        t = t + 2;
        s = s + t;
	c = c + 1;
    }
    
    //__VERIFIER_assert(t == 2 * a + 1);
    //__VERIFIER_assert(s == (a + 1) * (a + 1));
    //__VERIFIER_assert(t*t - 4*s + 2*t + 1 == 0);

    return ;

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x,y);
		}
	}
}
