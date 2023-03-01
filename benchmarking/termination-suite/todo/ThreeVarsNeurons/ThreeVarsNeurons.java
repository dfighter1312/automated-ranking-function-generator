public class ThreeVarsNeurons {

    public static void loop(int x, int y, int z) {
      while (x < y && y < z) {
        x++;
        y++;
      }
    }


    public static void main(String[] args) {
        if(args.length >= 3) {
          loop(args[0].length(),args[1].length(), args[2].length());
        }
    }
}
