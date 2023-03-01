public class Round3{

  public static void loop(int x) {
  
  while (x % 3 != 0) {
      x++;
    }
  }

  public static void main(String[] args) {
    Random.args = args;
    int x = Random.random();

    loop(x);
  }
}
