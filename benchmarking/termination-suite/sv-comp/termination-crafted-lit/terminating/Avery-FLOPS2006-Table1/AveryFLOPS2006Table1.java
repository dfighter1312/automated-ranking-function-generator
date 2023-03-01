/*
  * Program from Table 1 of
 * 2006FLOPS - James Avery - Size-Change Termination and Bound Analysis
 *
 * Date: 18.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 

 
public class AveryFLOPS2006Table1 {

	public static void loop(int x, int y) {


    int z, i;

	z = 0;
	i = x;
	if (y > 0 && x > 0) {
    	while (i > 0) {
	    	i = i - 1;
		    z = z + 1;
	    }
    	while (i < y) {
	    	i = i + 1;
		    z = z - 1;
	    }
	}

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x = args[0].length();
		int y = args[1].length();
		loop(x,y);
		}
	}
}
