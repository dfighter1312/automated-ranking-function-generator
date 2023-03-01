/*
 	//#Termination
/*
 * Program from Fig.1a of
 * 2009PLDI - Gulwani,Jain,Koskinen - Control-flow refinement and progress invariants for bound analysis
 *
 * Date: 9.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
*
* Translated from C to Java: Julian Parsert
*
*
*/  
 
import java.util.Random;

 
public class GulwaniJainKoskinenPLDI2009Fig1 {

	public static void loop(int id, int maxId) {

    int tmp;


    if(0 <= id && id < maxId) {
        tmp = id+1;
        while(tmp!=id && new Random().nextInt() != 0) {
            if (tmp <= maxId) {
                tmp = tmp + 1;
            } else {
                tmp = 0;
            }
        }
    }

	}

	public static void main(String[] args) {
	if (args.length >= 2) {
		int x =args[0].length();
		int y =args[1].length();
		loop(x, y);
		}
	}
}
