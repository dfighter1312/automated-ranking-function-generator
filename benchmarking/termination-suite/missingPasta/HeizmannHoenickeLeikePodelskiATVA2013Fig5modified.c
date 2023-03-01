/*
 * Program used in the experimental evaluation of the following papers.
 * 2008ESOP - Chawdhary,Cook,Gulwani,Sagiv,Yang - Ranking Abstractions
 * 2010SAS - Alias,Darte,Feautrier,Gonnord, Multi-dimensional Rankings, Program 
 *           Termination, and Complexity Bounds of Flowchart Programs
 *
 * Date: 2014
 * Author: Caterina Urban
 */

typedef enum {false, true} bool;

extern int __VERIFIER_nondet_int(void);

int main() {
    int x;
    x = __VERIFIER_nondet_int();
	
  int y = 2;
		while (x >= 0 && y > 0) {
			x = x - y;
			y = (y + 1) / 2;
		}

	return 0;
}
