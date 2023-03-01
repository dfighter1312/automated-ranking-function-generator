


public class BiasExample {

  public static void loop(int i, int k) {
    while(i > -100 ) {
      i--;
    }
  }

  public static void main(String[] args) {

    int i = Integer.parseInt(args[0]);
    int j = Integer.parseInt(args[1]);

    loop(i, j);
  }
}
