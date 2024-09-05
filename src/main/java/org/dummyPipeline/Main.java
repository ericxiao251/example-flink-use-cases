package org.dummyPipeline;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        //src.main.java.org.dummyPipeline.Main
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a DataStream using a custom SourceFunction
        DataStream<String> stream = env.addSource(new IncrementingSource());
        stream
                .keyBy(value -> value)
                .process(new KeyedProcessFunction<String, String, String>() {
                    private MapState<String, String> state;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        state = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                                "state",
                                String.class,
                                String.class
                        ));
                    }

                    @Override
                    public void processElement(String key, Context ctx, Collector<String> out) throws Exception {
                        // Accumulate a large amount of state
                        state.put(key, key);
                    }
                });

        // Execute the job
        env.execute();
    }

    // Custom SourceFunction to emit incrementing integers
    public static class IncrementingSource implements SourceFunction<String> {
        private volatile boolean isRunning = true;
        private Long current = 0L;

        @Override
        public void run(SourceContext<String> ctx) throws InterruptedException {
            while (isRunning) {
                // Emit the current value
                ctx.collect(current.toString());
                // Increment the value
                current++;
                // sleep
                TimeUnit.SECONDS.sleep(10);
            }
        }

        @Override
        public void cancel() {
            // Stop the source
            isRunning = false;
        }
    }
}

