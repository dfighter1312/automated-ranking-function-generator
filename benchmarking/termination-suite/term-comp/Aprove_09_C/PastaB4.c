
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();

    while (x > y) {
            int t = x;
            x = y;
            y = t;
    }
    return 0;
}
