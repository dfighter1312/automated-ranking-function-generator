/* 
 *
 * Program from Fig.1 of
 * 2010CAV - Kroening,Sharygina,Tsitovich,Wintersteiger - Termination Analysis with Compositional Transition Invariants
 *
 * Date: 12.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */



public class KroeningSharyginaTsitovichWintersteigerCAV2010Fig1 {

    public static void loop(int x) {
        int debug = 0;

        while (x < 255) {
            if (x % 2 != 0) {
                x--;
            } else {
                x += 2;
            }
            if (debug != 0) {
                x = 0;
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
