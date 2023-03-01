typedef enum {false,true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    
    while (x > y) {
      if ((y+1) % 2 == 0) {
        y = (y+1);
      } else {
        y = (y + 1) + 1;
      }
    }


    return 0;
}
