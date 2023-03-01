typedef enum {false,true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {

    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();

    int res = 1;

    if (x < 0 || y <= 1) return 0;
    else {
      while (x > y) { 
        y = y*y;
        res = 2*res;
      }
    }
    
    return 0;
}
