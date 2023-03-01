extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    x = __VERIFIER_nondet_int();

    int res = 0;
    while (x > 1) {
      x = x/2;
      res++;
    }
    
    return 0;
}
