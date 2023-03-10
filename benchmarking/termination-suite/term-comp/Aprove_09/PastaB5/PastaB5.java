/**
 * Modified to external function call by Julian
 *
 * Example taken from "A Term Rewriting Approach to the Automated Termination
 * Analysis of Imperative Programs" (http://www.cs.unm.edu/~spf/papers/2009-02.pdf)
 * and converted to Java.
 */

public class PastaB5 {

    public static void loop(int x) {
      while (x > 0 && (x % 2) == 0) {
            x--;
        }
    }

    public static void main(String[] args) {
        Random.args = args;
        int x = Random.random();

        loop(x);
    }
}
