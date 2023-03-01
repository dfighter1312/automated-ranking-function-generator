/*
  * Program used in the experimental evaluation of the following paper.
 * 2008ESOP - Chawdhary,Cook,Gulwani,Sagiv,Yang - Ranking Abstractions
 *
 * Date: 2014
 * Author: Caterina Urban
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class ChawdharyCookGulwaniSagivYangESOP2008aaron6 {

	public static void loop(int x, int tx, int y, int ty, int n) {


	if (x + y >= 0) {
		while (x <= n && x >= 2 * tx + y && y >= ty + 1 && x >= tx + 1) {
			if ( new Random().nextInt() != 0) {
				tx = x;
				ty = y;
				x = new Random().nextInt();
				y = new Random().nextInt();
			} else {
				tx = x;
				x = new Random().nextInt();
			}
		}
	}	


	}

	public static void main(String[] args) {
	if (args.length >= 5) {
		int x =args[0].length();
		int tx = args[1].length();
		int y = args[2].length();
		int ty = args[3].length();
		int n = args[4].length();
		loop(x,tx,y,ty,n);
		}
	}
}
