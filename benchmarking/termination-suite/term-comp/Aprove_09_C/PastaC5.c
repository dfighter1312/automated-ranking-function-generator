
extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    
    if (x > 0 && y > 0) {
        while (x != y) {
            if (x > y) {
                x = x - y;
            } else {
                y = y - x;
            }
        }
    }
    return 0;
}
