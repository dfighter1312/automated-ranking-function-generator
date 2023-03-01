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

 
public class ChawdharyCookGulwaniSagivYangESOP2008random2d {

	public static void loop() {

    int x, y, i, r, N;
	N = 10;
    x = 0;
    y = 0;
    i = 0;
	while (i < N) {
		i = i + 1;
		r = new Random().nextInt();
		if (r >= 0 && r <= 3) {
			if (r == 0) {
				x = x + 1;
			} else {if (r == 1) {
				x = x - 1;
			} else {if (r == 2) {
				y = y + 1;
			} else {if (r == 3) {
				y = y - 1;
            }}}}
		}
	}

	}

	public static void main(String[] args) {

	loop();
		
	}
}
