package com.example.WebFluxExample.utils;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public class ReadUtils {

    private static final int DEFAULT_WAITING_LINE_TIMEOUT_SECONDS = 2;
    private static final int TIMEOUT_IN_SECONDS = 3;

    public static Flux<String> getLinesStream(BufferedReader reader) {
        return Flux.generate(getLineConsumer(reader));
    }

    private static Consumer<SynchronousSink<String>> getLineConsumer(BufferedReader reader) {
        return sink -> {
            try {
                // имитация поступления новых данных
                TimeUnit.SECONDS.sleep(1);
                sink.next(readLineIfExist(reader));
            } catch (TimeoutException e) {
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        };
    }

    private static String readLineIfExist(BufferedReader reader) throws TimeoutException {
        ExecutorService executor = newFixedThreadPool(1);
        AtomicBoolean key = new AtomicBoolean(true);
        Future<String> future = executor.submit(() -> readLineWhile(reader, key));
        try {
            return future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException timeOutException) {
            throw timeOutException;
        } catch (Exception exception) {
            throw new IllegalStateException("Can not read.", exception);
        } finally {
            key.set(false);
            executor.shutdown();
        }
    }

    private static String readLineWhile(BufferedReader reader, AtomicBoolean key) throws IOException, InterruptedException {
        while (key.get()) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return line;
            } else {
                TimeUnit.SECONDS.sleep(DEFAULT_WAITING_LINE_TIMEOUT_SECONDS);
            }
        }
        return null;
    }
}
