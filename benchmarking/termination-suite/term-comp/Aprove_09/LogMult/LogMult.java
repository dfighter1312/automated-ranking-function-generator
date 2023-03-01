public class LogMult{

  public static int log(int x) {

    int res = 1;
    int y = 2;

    if (x < 0 || y <= 1) return 0;
    else {
      while (x > y) { 
        y = y*y;
        res = 2*res;
      }
    }
    return res;

  }





  public static void main(String[] args) {
    Random.args = args;

    int x = Random.random();
    log(x);
  }
}
