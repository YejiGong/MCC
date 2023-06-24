#include <omp.h>
#include<stdlib.h>
#include <stdio.h>
#define NUM 200000

int check_prime(int n) {
	int j;
	if (n <= 1) {
		return 0;
	}
	for (j = 2; j < n; j++) {
		if (n % j == 0) {
			return 0;
		}
	}
	return 1;
}

void main(int argc, char* argv[])
{
	int i;
	int result = 0;
	omp_set_num_threads(atoi(argv[2]));
	int type = atoi(argv[1]);
	double start_time, end_time;
	start_time = omp_get_wtime();
	if (type == 1) {
#pragma omp parallel for schedule(static) reduction(+:result)
		for (i = 0; i < NUM; i++) {
			if (check_prime(i) == 1) {
				result++;
			}
		}

	}
	else if (type == 2) {
#pragma omp parallel for schedule(dynamic) reduction(+:result)
		for (i = 0; i < NUM; i++) {
			if (check_prime(i) == 1) {
				result++;
			}
		}

	}
	else if (type == 3) {
#pragma omp parallel for schedule(static,10) reduction(+:result)
		for (i = 0; i < NUM; i++) {
			if (check_prime(i) == 1) {
				result++;
			}
		}

	}
	else if (type == 4) {
#pragma omp parallel for schedule(dynamic,10) reduction(+:result)
		for (i = 0; i < NUM; i++) {
			if (check_prime(i) == 1) {
				result++;
			}
		}

	}

	end_time = omp_get_wtime();
	double timeDiff = end_time - start_time;
	printf("Execution Time : %lfms\n", timeDiff);
	printf("Result : %d", result);
}