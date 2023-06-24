#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

long num_steps = 10000000;
double step;

void main(int argc, char* argv[])
{
	long i; double x, pi, sum = 0.0;
	double start_time, end_time;

	start_time = omp_get_wtime();
	step = 1.0 / (double)num_steps;
	omp_set_num_threads(atoi(argv[3]));
	int type = atoi(argv[1]);

	if (type == 1) {
#pragma omp parallel for schedule(static, atoi(argv[2])) reduction(+:sum) private(x) 
		for (i = 0;i < num_steps; i++) {
			x = (i + 0.5) * step;
			sum = sum + 4.0 / (1.0 + x * x);
		}
	}
	else if (type == 2) {
#pragma omp parallel for schedule(dynamic, atoi(argv[2])) reduction(+:sum) private(x)
		for (i = 0;i < num_steps; i++) {
			x = (i + 0.5) * step;
			sum = sum + 4.0 / (1.0 + x * x);
		}

	}
	else if (type == 3) {
#pragma omp parallel for schedule(guided, atoi(argv[2])) reduction(+:sum) private(x)
		for (i = 0;i < num_steps; i++) {
			x = (i + 0.5) * step;
			sum = sum + 4.0 / (1.0 + x * x);
		}
	
	}
	pi = step * sum;
	end_time = omp_get_wtime();
	double timeDiff = end_time - start_time;
	printf("Execution Time : %lfms\n", timeDiff);

	printf("pi=%.24lf\n", pi);
}