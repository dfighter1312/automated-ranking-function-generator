


public class LexicographicExample {

  public static void loop(int i, int k) {
    int j = 0;

    while (i < k) {
      j = 0;
      while (j < i) {
        j++;
      }
      i++;
    }
  }

  public static void main(String[] args) {

    int i = Integer.parseInt(args[0]);
    int j = Integer.parseInt(args[1]);

    loop(i, j);
  }
}
