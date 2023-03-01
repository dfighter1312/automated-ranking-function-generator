typedef enum {false,true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    int z;
    x = __VERIFIER_nondet_int();
    y = __VERIFIER_nondet_int();
    z = __VERIFIER_nondet_int();

    while(x > z || y > z) {
            if (x > z) {
                --x;
            } else if (y > z) {
                --y;
            }
    }
    
    return 0;
}
