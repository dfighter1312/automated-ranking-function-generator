/**
 * AProVE was not able to show this terminating (in the webinterface).
*/
package classes;

import java.lang.*;

public class AbsValue {

  public static void main(String[] args) {
    int x = Integer.parseInt(args[0]);

    AbsValue.absToZero(x);
  }


  public static void absToZero(int x) {

    while (Math.abs(x) > 0) {
      if(x < 0){
        x++;
      } else if (x > 0) {
        x--;
      }
    }
  }
}
