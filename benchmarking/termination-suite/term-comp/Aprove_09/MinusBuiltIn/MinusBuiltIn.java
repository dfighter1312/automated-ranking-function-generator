public class MinusBuiltIn{


  public static int loop(int x, int y) {
  
   int res = 0;



    while (x > y) {

      y++;
      res++;

    }
    
    return res;
  }

  public static void main(String[] args) {
    Random.args = args;
    int x = Random.random();
    int y = Random.random();
    
    int res = loop(x, y);
   
  }

}

