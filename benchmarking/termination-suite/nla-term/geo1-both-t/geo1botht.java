/*
 Geometric Series
computes x=(z-1)* sum(z^k)[k=0..k-1] , y = z^k
returns 1+x-y == 0
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
 
public class geo1botht {

	public static void loop(int z, int k) {

    int x, y, c;


    x = 1;
    y = z;
    c = 1;

    while (true) {


        if (!(x*z - x - y + 1 + c < k)) 
            break;

        c = c + 1;
        x = x * z + 1;
        y = y * z;

    }  //geo1

    x = x * (z - 1);

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		loop(x,y);
		
		}
	}
}
