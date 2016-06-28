/**
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 *
 * Vivek Kumar: Ported to HJ.
 */

package edu.rice.hjlib.benchmark;

import edu.rice.hj.api.HjSuspendable;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class QuickSortBenchmark extends Benchmark {

    private int SORT_THRESHOLD = 32_768;
    private int LENGTH = SORT_THRESHOLD * 64;

    private short[] numbers;

    public static void main(final String[] args) {
        BenchmarkRunner.runBenchmark(args, new QuickSortBenchmark());
    }

    @Override
    public void initialize(final String[] args) {
        final int argLimit = args.length - 1;
        for (int i = 0; i < argLimit; i++) {
            final String argName = args[i];
            final String argValue = args[i + 1];

            if (argName.equalsIgnoreCase("-n")) {
                LENGTH = Integer.parseInt(argValue);
            } else if (argName.equalsIgnoreCase("-g")) {
                SORT_THRESHOLD = Integer.parseInt(argValue);
            }
        }

        init();
    }

    private void init() {

        numbers = new short[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            numbers[i] = (short) (Math.random() * LENGTH * 100);
            //copy[i] = numbers[i];
        }
    }

    @Override
    public void printArgInfo() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "n", LENGTH);
        System.out.printf(BenchmarkRunner.argOutputFormat, "granularity", SORT_THRESHOLD);
    }

    @Override
    public void runIteration() {
        launchHabaneroApp(new HjSuspendable() {
            @Override
            public void run() throws SuspendableException {
                finish(new HjSuspendable() {
                    @Override
                    public void run() throws SuspendableException {
                        quickSortParallel(numbers, 0, LENGTH - 1);
                    }
                });
            }
        });
    }

    @Override
    public void cleanupIteration(final boolean lastIteration, final double execTimeMillis) {

        if (!lastIteration) {
            init();
        }
    }

    private void quickSortParallel(final short[] array, final int left, final int right) throws SuspendableException {
        //System.out.println("Sort called with "+left+" "+right);
        if (right - left < SORT_THRESHOLD) {
            quickSortSequential(array, left, right);
        } else {
            final int pivotMin = Math.min(array[left], Math.min(array[right], array[(left + right) / 2]));
            final int pivotMax = Math.max(array[left], Math.min(array[right], array[(left + right) / 2]));
            final int pivot = (short) ((pivotMin + pivotMax) / 2);

            final int index = partitionSequential(array, left, right, pivot);
            if (left < index - 1) {
                async(new HjSuspendable() {
                    @Override
                    public void run() throws SuspendableException {
                        QuickSortBenchmark.this.quickSortParallel(array, left, index - 1);
                    }
                });
            }
            if (index < right) {
                QuickSortBenchmark.this.quickSortParallel(array, index, right);
            }
        }
    }

    private void quickSortSequential(final short[] array, final int left, final int right) {

        if (left < right) {
            final int pivotMin = Math.min(array[left], Math.min(array[right], array[(left + right) / 2]));
            final int pivotMax = Math.max(array[left], Math.min(array[right], array[(left + right) / 2]));
            final int pivot = (short) ((pivotMin + pivotMax) / 2);

            final int index = partitionSequential(array, left, right, pivot);
            // System.out.println(String.format("(%4d, %4d, %4d)", left, index, right));

            if (left < index - 1) {
                quickSortSequential(array, left, index - 1);
            }
            if (index < right) {
                quickSortSequential(array, index, right);
            }
        }
    }

    private int partitionSequential(final short[] array, final int left, final int right, final int pivot) {
        int i = left;
        int j = right;
        short tmpx;
        while (i <= j) {
            while (i <= j && array[i] < pivot) {
                i++;
            }
            while (j >= i && array[j] > pivot) {
                j--;
            }
            if (i <= j) {
                tmpx = array[i];
                array[i] = array[j];
                array[j] = tmpx;
                i++;
                j--;
            }
        }
        // ensure the pivot index is inside the bounds [left, right]
        return Math.min(i, LENGTH);
    }
}
