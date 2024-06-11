package org.exampleHttpApi;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class CoffeeOrderHttpOperator extends RichAsyncFunction<String, CoffeeOrder> {
    // https://github.com/caarlos0-graveyard/flink-async-http-example/blob/main/src/main/java/dev/caarlos0/StreamingJob.java

    private static final Logger logger = LoggerFactory.getLogger(CoffeeOrderHttpOperator.class);

    private transient CloseableHttpAsyncClient client;
//    Approach 2.1: Automatically parse POJO.
//    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void open(Configuration parameters) {
        client = HttpAsyncClients.custom().build();
        client.start();
    }

    @Override
    public void asyncInvoke(String pokemonNumber, ResultFuture<CoffeeOrder> resultFuture) {
        final HttpUriRequest request =
            RequestBuilder.get(String.format("http://localhost:8080/coffee?size=S&name=cappuccino"))
                        .addHeader(ACCEPT, APPLICATION_JSON.getMimeType())
                        .addHeader("Authorization", "Basic Q29mZmVlIGRyaW5rZXI6SSBsb3ZlIGNvZmZlZQ==")
                        .build();

        final Future<HttpResponse> result = client.execute(request, null);

        CompletableFuture.supplyAsync(
                () -> {
                    try {
                        HttpResponse response = result.get();
                        ObjectNode object = new ObjectMapper().readValue(response.getEntity().getContent(), ObjectNode.class);

                        return new CoffeeOrder(
                            object.get("size").asText(),
                            object.get("name").asText(),
                            object.get("prepTime").asInt()
                        );
                    } catch (ExecutionException | InterruptedException | IOException e) {
                        return "Bad";
                        // TODO: Add some proper logging to DataDog, Prometheus, etc.
                    }
                })
                .whenCompleteAsync(
                        (response, ex) -> {
                            if (ex == null) {
                                System.out.println(response);
                                resultFuture.complete(Collections.singleton((CoffeeOrder) response));
                            } else {
                                resultFuture.completeExceptionally(ex);
                            }
                        });
    }


    @Override
    public void close() throws Exception {
        if (client != null && client.isRunning()) {
            client.close();
        }
    }
}
