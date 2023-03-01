
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    
    while (true) {
        if (x >= 0) {
            x--;
            y = __VERIFIER_nondet_int();
        } else if (y >= 0) {
            y--;
        } else {
            break;
        }
    }

    return 0;
}
