import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class PaperEx1 {

  public static void main(String[] args) {

    List<Integer> l = new ArrayList<>();
    l.add(4);
    l.add(-3);

  }

  public static void removeNegative(LinkedList<Integer> list) {
    Iterator<Integer> i = list.iterator();
    while(i.hasNext()){
      Integer x = i.next();
      if(x < 0) {
        i.remove();
      }
    }
  }
}
