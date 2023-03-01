
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    int z;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    z = __VERIFIER_nondet_int();


    while (x > z && y > z) {
        x--;
        y--;
    }
    return 0;
}
