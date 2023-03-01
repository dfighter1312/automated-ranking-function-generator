
extern int __VERIFIER_nondet_int(void);

int main() {
    int a;
    int b;
    a = __VERIFIER_nondet_int();
    b = __VERIFIER_nondet_int();
    
    int tmp;
    while(b > 0 && a > 0) {
      tmp = b;
      b = a % b;
      a = tmp;
    }

    // a is gcd
    
    return 0;
}
