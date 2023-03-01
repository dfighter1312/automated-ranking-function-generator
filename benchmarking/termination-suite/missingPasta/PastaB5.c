typedef enum {false,true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {
    int x;

    x = __VERIFIER_nondet_int();
    
    while (x > 0 && (x % 2) == 0) {
        x--;
    }
    
    return 0;
}
