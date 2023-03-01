public class CountUpRound{


  public static void loop (int x, int y) {
     while (x > y) {
      if ((y+1) % 2 == 0) {
        y = (y+1);
      } else {
        y = (y + 1) + 1;
      }
    }
  }

  public static void main(String[] args) {
    Random.args = args;
    int x = Random.random();
    int y = Random.random();

    loop(x,y);

  }
}
