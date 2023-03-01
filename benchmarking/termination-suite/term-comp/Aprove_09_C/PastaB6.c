
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();

    while (x > 0 && y > 0) {
        x--;
        y--;
	}
    return 0;
}
