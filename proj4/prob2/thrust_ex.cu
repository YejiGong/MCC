#include <stdio.h>
#include <time.h>
#include <thrust/device_vector.h>
#include <thrust/transform_reduce.h>

#define BLOCK_SIZE 256

struct calculate_sum : public thrust::unary_function<long, double>
{
    const double step;

    calculate_sum(double _step) : step(_step) {}

    __host__ __device__
    double operator()(const long& i) const
    {
        double x = (i + 0.5) * step;
        return 4.0 / (1.0 + x * x);
    }
};

int main()
{
    long num_steps = 1000000000;
    double step = 1.0 / (double)num_steps;

    thrust::device_vector<long> d_indices(num_steps);
    thrust::sequence(d_indices.begin(), d_indices.end());

    clock_t start = clock();

    double sum = thrust::transform_reduce(d_indices.begin(), d_indices.end(), calculate_sum(step), 0.0, thrust::plus<double>());

    double pi = step * sum;

    clock_t end = clock();
    double elapsedTime = (double)(end-start)/CLOCKS_PER_SEC;

    printf("Execution Time: %.10lf sec\n", elapsedTime);
    printf("pi=%.10lf\n", pi);

    return 0;
}
