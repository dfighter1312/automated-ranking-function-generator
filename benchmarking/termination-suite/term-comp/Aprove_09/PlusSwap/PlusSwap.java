public class PlusSwap{
  
  public static void loop(int x, int y) {
  
    int z;
    int res = 0;

    while (y > 0) {

      z = x;
      x = y-1;
      y = z;
      res++;

    }

    res = res + x;
  }
  
  public static void main(String[] args) {
    Random.args = args;
    int x = Random.random();
    int y = Random.random();
    
    loop(x,y);
  }
}
