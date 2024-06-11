package org.exampleHttpApi;


import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> inputStream = env.fromElements(
                "L"
                ,"M"
                ,"S"
                ,"L"
                ,"XL"
        );

        // TODO: Flink retries.
        DataStream<CoffeeOrder> outputStream = AsyncDataStream.unorderedWait(
                inputStream,
                new CoffeeOrderHttpOperator(),
                2,
                TimeUnit.MINUTES,
                1000
        );
//        outputStream.print();

        env.execute();
    }
}