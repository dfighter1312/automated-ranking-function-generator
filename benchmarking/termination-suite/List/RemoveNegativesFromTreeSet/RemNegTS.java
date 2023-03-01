import java.util.Iterator;
import java.util.TreeSet;

public class RemNegTS {

  public static void main(String[] args) {

    TreeSet<Integer> l = new TreeSet<Integer>();
    l.add(4);
    l.add(-3);
    removeNegative(l);

  }

  public static void removeNegative(TreeSet<Integer> list) {
    Iterator<Integer> i = list.iterator();
    while(i.hasNext()){
      Integer x = i.next();
      if(x < 0) {
        i.remove();
      }
    }
  }
}
