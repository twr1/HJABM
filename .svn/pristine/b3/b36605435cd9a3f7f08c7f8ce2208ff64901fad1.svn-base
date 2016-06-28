package edu.rice.hjlib.benchmark;

import edu.rice.hj.Module0;
import edu.rice.hj.api.HjRunnable;
import edu.rice.hj.api.HjSuspendable;
import edu.rice.hj.api.SuspendableException;

import java.io.IOException;

import static edu.rice.hj.Module0.asyncNb;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * <p>JavaForkJoinBench class.</p>
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class JgfForkJoinBenchmark extends Benchmark {

    public int numTasks = 1_000;

    public static void main(final String[] args) {
        BenchmarkRunner.runBenchmark(args, new JgfForkJoinBenchmark());
    }

    private JgfForkJoinBenchmark() {
        // disallow instance creation from outside this package
    }

    @Override
    public void initialize(final String[] args) throws IOException {
        final int argLimit = args.length - 1;
        for (int i = 0; i < argLimit; i++) {
            final String argName = args[i];
            final String argValue = args[i + 1];

            if (argName.equalsIgnoreCase("-n")) {
                numTasks = Integer.parseInt(argValue);
            }
        }
    }

    @Override
    public void printArgInfo() {
        System.out.printf(BenchmarkRunner.argOutputFormat, "numTasks", numTasks);
    }

    @Override
    public void runIteration() {
        launchHabaneroApp(new HjSuspendable() {
            @Override
            public void run() throws SuspendableException {
                JgfForkJoinBenchmark.this.run(numTasks);
            }
        });
    }

    @Override
    public void cleanupIteration(final boolean lastIteration, final double execTimeMillis) {
        // nothing to do
    }

    public void run(final int numThreads) throws SuspendableException {

        finish(new HjSuspendable() {
            @Override
            public void run() throws SuspendableException {
                for (int k = 0; k < numThreads; k++) {
                    final ForkJoinTask task = new ForkJoinTask(k);
                    asyncNb(new HjRunnable() {
                        @Override
                        public void run() {
                            task.run();
                        }
                    });
                }
            }
        });

    }

    private static class ForkJoinTask implements Runnable {

        private final int id;

        /**
         * <p>Constructor for ForkJoinTask.</p>
         *
         * @param id a int.
         */
        public ForkJoinTask(final int id) {
            this.id = id;
        }

        /**
         * <p>run.</p>
         */
        public void run() {

            // do something trivial but which won't be optimised away!

            final double theta = 37.2;
            double sint;
            double res;

            sint = Math.sin(theta);
            res = sint * sint;

            //defeat dead code elimination
            if (res <= 0) {
                System.out.println("Benchmark exited with unrealistic res value " + res);
            }
        }
    }
}
