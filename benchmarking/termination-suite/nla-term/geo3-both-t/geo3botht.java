/*
 
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class geo3botht {

	public static void loop(int z, int a, int k) {


    int x, y, c;

    x = a;
    y = 1;
    c = 1;

    while (true) {


        if (!(z*x - x + a - a*z*y + c < k))
            break;

        c = c + 1;
        x = x * z + a;
        y = y * z;
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
