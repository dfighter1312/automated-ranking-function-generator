/* 
 * Program from Fig.8 of
 * 2013FSE - Nori,Sharma - Termination Proofs from Tests
 *
 * Date: 18.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class NoriSharmaFSE2013Fig8 {

    public static void loop(int x, int y, int z) {
        int c, u, v, w;
        u = x;
        v = y;
        w = z;
        c = 0;
        while (x >= y) {
            c = c + 1;
            if (z > 1) {
                z = z - 1;
                x = x + z;
            } else {
                y = y + 1;
            }
        }
    }

    public static void main(String[] args) {
        if(args.length >= 3) {
            int x = args[0].length();
            int y = args[1].length();
            int z = args[2].length();
            loop(x,y, z);
        }

    }
}
