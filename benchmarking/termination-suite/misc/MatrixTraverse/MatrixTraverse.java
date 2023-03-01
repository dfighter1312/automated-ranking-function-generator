
package classes;

public class MatrixTraverse {

  public static void main(String[] args) {
    if (args.length >= 2) {
      loop(args[0].length(), args[1].length());
    }
  }


  public static void loop(int n_rows, int n_cols) {

    int row = 0;
    int col = 0;
    while (row * n_cols + col <= n_cols * n_rows ) {
      row = row + 1;
      if (row == n_rows) {
        col = col+1;
        row = 0;
      } 
    }
  }
}
