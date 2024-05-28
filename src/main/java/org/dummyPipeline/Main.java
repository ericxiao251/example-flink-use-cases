package org.dummyPipeline;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class Main {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a DataStream using a custom SourceFunction
        DataStream<Integer> stream = env.addSource(new IncrementingSource());

        // Print the stream to standard out
        stream.print();

        // Execute the job
        env.execute("Incrementing DataStream");
    }

    // Custom SourceFunction to emit incrementing integers
    public static class IncrementingSource implements SourceFunction<Integer> {
        private volatile boolean isRunning = true;
        private int current = 0;

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            while (isRunning) {
                // Emit the current value
                ctx.collect(current);
                // Increment the value
                current++;
                // Sleep for 5 seconds
                Thread.sleep(5000);
            }
        }

        @Override
        public void cancel() {
            // Stop the source
            isRunning = false;
        }
    }
}

