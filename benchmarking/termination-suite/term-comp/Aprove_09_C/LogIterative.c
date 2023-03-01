
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    
    int res = 0;
    while (x >= y && y > 1) {
      res++;
      x = x/y;
    }
    
    return 0;
}
