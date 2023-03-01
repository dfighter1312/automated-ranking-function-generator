/**
 * AProVE was not able to show this terminating (in the webinterface).
*/
package classes;

public class GCD {

    public static int gcd(Integer i, Integer j) {
        if (i <= 0 || j <= 0) {return 0;}
     while (i != j) {
      if (i > j) {
        i = i - j;
      } else {
        j = j - i;
      }
    }
     return i;
  }
}
