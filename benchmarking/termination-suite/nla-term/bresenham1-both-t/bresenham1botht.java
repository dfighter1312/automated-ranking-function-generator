/*
   Bresenham's line drawing algorithm 
  from Srivastava et al.'s paper From Program Verification to Program Synthesis in POPL '10 

*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class bresenham1botht {

	public static void loop(int X, int Y, int k) {


    int v, x, y;

    v = 2 * Y - X;
    y = 0;
    x = 0;
    int c;
    c = 0;


    while (2*Y*x - 2*X*y - X + 2*Y - v + c <= k) {

        if (v < 0) {
            v = v + 2 * Y;
        } else {
            v = v + 2 * (Y - X);
            y++;
        }
        x++;
        c++;
    }

	}

	public static void main(String[] args) {
	if (args.length >= 3) {
		int x = Integer.parseInt(args[0]);		
		int y = Integer.parseInt(args[1]);
		int k = Integer.parseInt(args[2]);
		loop(x, y, k);
		}
	}
}
