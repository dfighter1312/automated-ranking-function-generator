/* 
 * Program from Fig.7 of
 * 2013FSE - Nori,Sharma - Termination Proofs from Tests
 *
 * Date: 18.12.2013
 * Author: heizmann@informatik.uni-freiburg.de
 *
 *
 * Translated from C to Java: Julian Parsert, julian.parsert@gmail.com
 */


public class NoriSharmaFSE2013Fig7 {

    public static void loop(int i, int j, int M, int N) {
        int a, b, c;
        a = i;
        b = j;
        c = 0;
        while (i<M || j<N) {
            i = i + 1;
            j = j + 1;
            c = c + 1;
        }
    }

    public static void main(String[] args) {
        if(args.length >= 4) {

            int i = args[0].length();;
            int j = args[1].length();;
            int M = args[2].length();;
            int N = args[3].length();;

            loop(i, j, M, N);
        }

    }
}
