package edu.rice.hjlib;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.config.RuntimeType;
import edu.rice.hjlib.benchmark.JgfForkJoinBenchmark;
import edu.rice.hjlib.benchmark.MatrixMultiplicationBenchmark;
import edu.rice.hjlib.benchmark.QuickSortBenchmark;

public class HjlibActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        final Button btnDisplay = (Button) findViewById(R.id.btnSubmit);
        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HjSystemProperty.resetConfigurations();
                HjSystemProperty.nbAsyncOpt.set(true);

                final int availableProcessors = Runtime.getRuntime().availableProcessors();
                final int workerThreads = Math.max(4, availableProcessors);
                HjSystemProperty.numWorkers.set(workerThreads);
                System.out.println("Num Worker Threads: " + workerThreads);

//                HjSystemProperty.numWorkers.set(1);
//                System.out.println("Num Worker Threads: " + 1);

                final RadioGroup radioGroupMode = (RadioGroup) findViewById(R.id.radioMode);
                final int selectedModeId = radioGroupMode.getCheckedRadioButtonId();
                final RadioButton radioButtonMode = (RadioButton) findViewById(selectedModeId);
                System.out.println("Radio Button Mode: " + radioButtonMode.getText());
                if (radioButtonMode.getText().toString().toLowerCase().contains("jdk")) {
                    HjSystemProperty.runtime.set(RuntimeType.JAVA_UTIL_CONCURRENT.shortName);
                } else {
                    HjSystemProperty.runtime.set(RuntimeType.FORK_JOIN.shortName);
                }

                final Spinner spinnerNumIters = (Spinner) findViewById(R.id.spinnerNumIters);
                final String numIters = String.valueOf(spinnerNumIters.getSelectedItem());
                System.out.println("Spinner Num Iterations: " + numIters);
                final String[] benchmarkArgs = new String[]{"-iter", numIters};

                final RadioGroup radioGroupBenchmark = (RadioGroup) findViewById(R.id.radioBenchmark);
                final int selectedBenchmarkId = radioGroupBenchmark.getCheckedRadioButtonId();
                final RadioButton radioButtonBenchmark = (RadioButton) findViewById(selectedBenchmarkId);
                System.out.println("Radio Button Benchmark: " + radioButtonBenchmark.getText());

                final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(final Void... voids) {

                        final String benchmarkName = radioButtonBenchmark.getText().toString().toLowerCase();
                        if (benchmarkName.contains("JGF Fork Join".toLowerCase())) {
                            JgfForkJoinBenchmark.main(benchmarkArgs);
                        } else if (benchmarkName.contains("Matrix Multiplication".toLowerCase())) {
                            MatrixMultiplicationBenchmark.main(benchmarkArgs);
                        } else if (benchmarkName.contains("QuickSort".toLowerCase())) {
                            QuickSortBenchmark.main(benchmarkArgs);
                        }

                        return null;
                    }
                };
                asyncTask.execute();


            }

        });

    }
}
