/* 
 *
 * Program from the example (without number) of
 * 2010CAV - Kroening,Sharygina,Tsitovich,Wintersteiger - Termination Analysis with Compositional Transition Invariants
 *
 * Date: 2014
 * Author: Caterina Urban
 *
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


import java.util.Random;


public class KroeningSharyginaTsitovichWintersteigerCAV2010Ex {

    public static void loop(int i) {

        while (i < 255) {
		if (new Random().nextInt() != 0) {
			i = i + 1;
		} else {
			i = i + 2;
		}
	}
    }

    public static void main(String[] args) {
        if(args.length >= 1) {
            int x = args[0].length();
            loop(x);
        }

    }
}
