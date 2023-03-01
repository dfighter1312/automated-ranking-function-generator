/*
  * Program from Fig.1a of
 * 2006CAV - Gopan,Reps - Lookahead Widening
 *
 * Date: 2014-06-22
 * Author: Caterina Urban, Matthias Heizmann
*
* Translated from C to Java: Julian Parsert
*
*
*/  

 
public class GopanRepsCAV2006Fig1a {

	public static void loop() {

    int x, y;
	x = 0;
    y = 0;
	while (y >= 0) {
		if (x <= 50) {
			y = y + 1;
		} else {
			y = y - 1;
		}
		x = x + 1;
	}

	}

	public static void main(String[] args) {

		loop();
	}
	
}
