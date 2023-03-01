/**
 * Example taken from "A Term Rewriting Approach to the Automated Termination
 * Analysis of Imperative Programs" (http://www.cs.unm.edu/~spf/papers/2009-02.pdf)
 * and converted to Java.
 */


public class PastaA1 {

    public static void loop(int x) {
        while (x > 0) {
            int y = 0;
            while (y < x) {
                y++;
            }
            x--;
        }
    }
    

    public static void main(String[] args) {
        Random.args = args;
        int x = Random.random();
        
    }
}
