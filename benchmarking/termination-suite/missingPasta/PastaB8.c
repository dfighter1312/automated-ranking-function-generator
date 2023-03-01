typedef enum {false,true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    int y;
    x = __VERIFIER_nondet_int();
    
    if (x > 0) {
            while (x != 0) {
                if (x % 2 == 0) {
                    x = x/2;
                } else {
                    x--;
                }
            }
        }
    return 0;
}
