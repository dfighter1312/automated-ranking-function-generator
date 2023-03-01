public class DivWithoutMinus{
  // adaption of the algorithm from [Kolbe 95]


  public static int div(int x, int y){
    int z;
    int res;

    z = y;
    res = 0;

    while (z > 0 && (y == 0 || y > 0 && x > 0))	{

      if (y == 0) {
        res = res + 1;
        y = z;
      }
      else {
        x = x + 1;
        y = y - 1;
      }
    }

    return res;
  }

  public static void main(String[] args) {
    Random.args = args;

    int x = Random.random();
    int y = Random.random();
    div(x,y);
  }
}

