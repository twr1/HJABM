/**
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 *
 * Vivek Kumar: Ported to HJ.
 */

package edu.rice.hjlib.benchmark;

import edu.rice.hj.Module0;
import edu.rice.hj.api.HjSuspendable;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class MatrixMultiplicationBenchmark extends Benchmark {

    private static int n = 1_024;
    private static int granularity = 512;

    private boolean check = false;
    private float[] a;
    private float[] b;
    private float[][] c;

    public static void main(final String[] args) {
        BenchmarkRunner.runBenchmark(args, new MatrixMultiplicationBenchmark());
    }

    @Override
    public void initialize(final String[] args) {
        final int argLimit = args.length - 1;
        for (int i = 0; i < argLimit; i++) {
            final String argName = args[i];
            final String argValue = args[i + 1];

            if (argName.equalsIgnoreCase("-n")) {
                n = Integer.parseInt(argValue);
            } else if (argName.equalsIgnoreCase("-check")) {
                check = Boolean.valueOf(argValue);
            } else if (argName.equalsIgnoreCase("-g")) {
                granularity = Integer.parseInt(argValue);
            }
        }

        init();
    }

    private void init() {

        a = new float[n * n];
        b = new float[n * n];
        c = new float[n][n];

        for (int i = 0; i < n * n; i++) {
            a[i] = 1.0F;
            b[i] = 1.0F;
        }
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                c[i][j] = 0;
            }
        }
    }

    @Override
    public void printArgInfo() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "n", n);
        System.out.printf(BenchmarkRunner.argOutputFormat, "granularity", granularity);
        System.out.printf(BenchmarkRunner.argOutputFormat, "check", check);
    }

    @Override
    public void runIteration() {
        launchHabaneroApp(new HjSuspendable() {
            @Override
            public void run() throws SuspendableException {
                compute(a, 0, 0, b, 0, 0, c, 0, 0, n);
            }
        });
    }

    @Override
    public void cleanupIteration(final boolean lastIteration, final double execTimeMillis) {

        if (check) {
            check(c, n);
        }

        if (!lastIteration) {
            init();
        }
    }

    private void check(final float[][] c, final int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (c[i][j] != n) {
                    System.out.println("Check Failed at [" + i + "][" + j + "]: " + c[i][j]);
                    return;
                }
            }
        }
    }

    private void compute(final float[] A, final int aRow, final int aCol, final float[] B, final int bRow, final int bCol, final float[][] C, final int cRow, final int cCol, final int size) throws SuspendableException {
        if (size <= granularity) {
            multiplyStride2(A, aRow, aCol, B, bRow, bCol, C, cRow, cCol, size);
        } else {
            final int h = size / 2;
            finish(new HjSuspendable() {
                @Override
                public void run() throws SuspendableException {
                    async(new HjSuspendable() {
                        @Override
                        public void run() throws SuspendableException {    // Part-1
                            MatrixMultiplicationBenchmark.this.compute(A, aRow, aCol, B, bRow, bCol, C, cRow, cCol, h);
                            MatrixMultiplicationBenchmark.this.compute(A, aRow, aCol + h, B, bRow + h, bCol, C, cRow, cCol, h);
                        }
                    });
                    async(new HjSuspendable() {
                        @Override
                        public void run() throws SuspendableException {    // Part-2
                            MatrixMultiplicationBenchmark.this.compute(A, aRow, aCol, B, bRow, bCol + h, C, cRow, cCol + h, h);
                            MatrixMultiplicationBenchmark.this.compute(A, aRow, aCol + h, B, bRow + h, bCol + h, C, cRow, cCol + h, h);
                        }
                    });
                    async(new HjSuspendable() {
                        @Override
                        public void run() throws SuspendableException {    // Part-3
                            MatrixMultiplicationBenchmark.this.compute(A, aRow + h, aCol, B, bRow, bCol, C, cRow + h, cCol, h);
                            MatrixMultiplicationBenchmark.this.compute(A, aRow + h, aCol + h, B, bRow + h, bCol, C, cRow + h, cCol, h);
                        }
                    });
                    {    // Part-4
                        MatrixMultiplicationBenchmark.this.compute(A, aRow + h, aCol, B, bRow, bCol + h, C, cRow + h, cCol + h, h);
                        MatrixMultiplicationBenchmark.this.compute(A, aRow + h, aCol + h, B, bRow + h, bCol + h, C, cRow + h, cCol + h, h);
                    }
                }
            });
        }
    }

    private void multiplyStride2(final float[] A, final int aRow, final int aCol,
                                 final float[] B, final int bRow, final int bCol,
                                 final float[][] C, final int cRow, final int cCol,
                                 final int size) {
        for (int j = 0; j < size; j += 2) {
            for (int i = 0; i < size; i += 2) {

                final int a0 = aRow + i;
                final int a1 = aRow + i + 1;

                float s00 = 0.0F;
                float s01 = 0.0F;
                float s10 = 0.0F;
                float s11 = 0.0F;

                for (int k = 0; k < size; k += 2) {
                    final int b0 = bRow + k;

                    s00 += A[a0 + aCol + k] * B[b0 + bCol + j];
                    s10 += A[a1 + aCol + k] * B[b0 + bCol + j];
                    s01 += A[a0 + aCol + k] * B[b0 + bCol + j + 1];
                    s11 += A[a1 + aCol + k] * B[b0 + bCol + j + 1];

                    final int b1 = bRow + k + 1;

                    s00 += A[a0 + aCol + k + 1] * B[b1 + bCol + j];
                    s10 += A[a1 + aCol + k + 1] * B[b1 + bCol + j];
                    s01 += A[a0 + aCol + k + 1] * B[b1 + bCol + j + 1];
                    s11 += A[a1 + aCol + k + 1] * B[b1 + bCol + j + 1];
                }

                C[cRow + i][cCol + j] += s00;
                C[cRow + i][cCol + j + 1] += s01;
                C[cRow + i + 1][cCol + j] += s10;
                C[cRow + i + 1][cCol + j + 1] += s11;
            }
        }
    }
}
