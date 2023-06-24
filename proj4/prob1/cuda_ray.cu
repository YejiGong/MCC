#include <stdio.h>
#include <time.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <curand.h>
#include <curand_kernel.h>

#define CUDA 0
#define OPENMP 1
#define SPHERES 20

#define rnd(x) (x * curand_uniform(&local_state))
#define INF 2e10f
#define DIM 2048

struct Sphere {
    float r, b, g;
    float radius;
    float x, y, z;
    __device__ float hit(float ox, float oy, float* n) const {
        float dx = ox - x;
        float dy = oy - y;
        if (dx * dx + dy * dy < radius * radius) {
            float dz = sqrtf(radius * radius - dx * dx - dy * dy);
            *n = dz / sqrtf(radius * radius);
            return dz + z;
        }
        return -INF;
    }
};

__global__ void initializeSpheres(Sphere* s) {
    int i = threadIdx.x;
    if (i < SPHERES) {
        curandState local_state;
        curand_init(clock64(), i, 0, &local_state);
        s[i].r = rnd(1.0f);
        s[i].g = rnd(1.0f);
        s[i].b = rnd(1.0f);
        s[i].x = rnd(2000.0f) - 1000;
        s[i].y = rnd(2000.0f) - 1000;
        s[i].z = rnd(2000.0f) - 1000;
        s[i].radius = rnd(200.0f) + 40;
    }
}

__global__ void kernel(Sphere* s, unsigned char* ptr) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;
    
    int offset = x + y * DIM;

    float ox = (x - DIM / 2);
    float oy = (y - DIM / 2);

    float r = 0, g = 0, b = 0;
    float maxz = -INF;
    for (int i = 0; i < SPHERES; i++) {
        float n;
        float t = s[i].hit(ox, oy, &n);
        if (t > maxz) {
            float fscale = n;
            r = s[i].r * fscale;
            g = s[i].g * fscale;
            b = s[i].b * fscale;
            maxz = t;
        }
    }

    ptr[offset * 4 + 0] = (int)(r * 255);
    ptr[offset * 4 + 1] = (int)(g * 255);
    ptr[offset * 4 + 2] = (int)(b * 255);
    ptr[offset * 4 + 3] = 255;
}

void ppm_write(unsigned char* bitmap, int xdim, int ydim, FILE* fp) {
    int i, x, y;
    fprintf(fp, "P3\n");
    fprintf(fp, "%d %d\n", xdim, ydim);
    fprintf(fp, "255\n");
    for (y = 0; y < ydim; y++) {
        for (x = 0; x < xdim; x++) {
            i = x + y * xdim;
            fprintf(fp, "%d %d %d ", bitmap[4 * i], bitmap[4 * i + 1], bitmap[4 * i + 2]);
        }
        fprintf(fp, "\n");
    }
}

int main(int argc, char* argv[]) {
    int no_threads;
    int option;
    int x, y;
    unsigned char* bitmap;
    unsigned char* d_bitmap;

    srand(time(NULL));

    if (argc != 3) {
        printf("> a.out [option] [filename.ppm]\n");
        printf("[option] 0: CUDA, 1~16: OpenMP using 1~16 threads\n");
        printf("for example, '> a.out 8 result.ppm' means executing OpenMP with 8 threads\n");
        exit(0);
    }
    FILE* fp = fopen(argv[2], "w");

    if (strcmp(argv[1], "0") == 0)
        option = CUDA;
    else {
        option = OPENMP;
        no_threads = atoi(argv[1]);
    }

    

    Sphere* temp_s = (Sphere*)malloc(sizeof(Sphere) * SPHERES);

    Sphere* d_temp_s;
    cudaMalloc((void**)&d_temp_s, sizeof(Sphere) * SPHERES);
    cudaMemcpy(d_temp_s, temp_s, sizeof(Sphere) * SPHERES, cudaMemcpyHostToDevice);

    bitmap = (unsigned char*)malloc(sizeof(unsigned char) * DIM * DIM * 4);
    cudaMalloc((void**)&d_bitmap, sizeof(unsigned char) * DIM * DIM * 4);

    dim3 blocks(DIM/16, DIM/16);
    dim3 threads(16,16);


    clock_t start = clock();
    initializeSpheres<<<1, SPHERES>>>(d_temp_s);
    cudaMemcpy(temp_s, d_temp_s, sizeof(Sphere) * SPHERES, cudaMemcpyDeviceToHost);

    kernel<<<blocks, threads>>>(d_temp_s, d_bitmap);
    cudaMemcpy(bitmap, d_bitmap, sizeof(unsigned char) * DIM * DIM * 4, cudaMemcpyDeviceToHost);

    ppm_write(bitmap, DIM, DIM, fp);

    clock_t end = clock();
    double elapsedTime = (double)(end-start)/CLOCKS_PER_SEC;
    printf("CUDA ray tracing: %.10lf sec\n",elapsedTime);
    printf("[%s] was generated.", argv[2]);
    
    fclose(fp);
    free(bitmap);
    free(temp_s);
    cudaFree(d_temp_s);
    cudaFree(d_bitmap);

    return 0;
}
