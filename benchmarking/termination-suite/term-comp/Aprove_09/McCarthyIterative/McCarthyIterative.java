public class McCarthyIterative {
  public static int run(int x) {
    int c = 1;
    while (c > 0) {
      if (x > 100) {
        x -= 10;
        c--;
      } else {
        x += 11;
        c++;
      }
    }
    return x;
  }

  public static void main(String[] args) {
    Random.args = args;
    run(Random.random());
  }
}
