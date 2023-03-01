/*
  Algorithm for finding the closest integer to square root,
 * more details, see : http://www.pedrofreire.com/sqrt/sqrt1.en.html 

Note: for some reason using cpa was able to disprove these
cpa.sh -kInduction -setprop solver.solver=z3 freire1.c
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class freire1botht {

	public static void loop(int a, int k) { // Added


    int r;

    int x;
    x = a / 2;
    r = 0;
    int c = 0;  
    
    while ( r*r - a - r + 2*x + c <= k ) {

        x = x - r;
        r = r + 1;
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
