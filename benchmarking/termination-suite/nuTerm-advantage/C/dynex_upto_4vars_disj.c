extern int __VERIFIER_nondet_int(void);


int main() {

  int a = 0;
  int n = __VERIFIER_nondet_int();
  int m = __VERIFIER_nondet_int();
  int o = __VERIFIER_nondet_int();
  int p = __VERIFIER_nondet_int();

  while (a * a * 4 <= n || a * a * 4 <= m ||  a * a * 4 <= o ||  a * a * 4 <= p) {
      a = a + 1;
  }

  return 0;
    
}
