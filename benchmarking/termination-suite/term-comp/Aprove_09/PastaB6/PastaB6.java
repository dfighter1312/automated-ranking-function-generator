/**
 * Example taken from "A Term Rewriting Approach to the Automated Termination
 * Analysis of Imperative Programs" (http://www.cs.unm.edu/~spf/papers/2009-02.pdf)
 * and converted to Java.
 */

public class PastaB6 {

	public static void loop(int x, int y) {
	
		while (x > 0 && y > 0) {
		        x--;
		        y--;
		}
	}

    public static void main(String[] args) {
        Random.args = args;
        int x = Random.random();
        int y = Random.random();
		
		loop(x, y);
        
    }
}
