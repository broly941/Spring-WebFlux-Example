package com.example.WebFluxExample.service;

import com.example.WebFluxExample.utils.ReadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;

import java.io.*;

@Slf4j
@Service
public class ReactiveService {

    private static final String FILE_PATH = "classpath:messages.txt ";

    public Flux<String> readFile() throws FileNotFoundException {
        BufferedReader reader = openReader(ResourceUtils.getFile(FILE_PATH));
        return ReadUtils.getLinesStream(reader)
                .doOnError(this::throwRuntimeException)
                .doOnTerminate(() -> closeReader(reader));
    }

    private BufferedReader openReader(File file) {
        try {
            log.info("Open reader.");
            return new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            log.error("File with path {} not found", file.toPath());
            throw new RuntimeException(e);
        }
    }

    private void throwRuntimeException(Throwable throwable) {
        log.error("Error: " + throwable.getMessage());
        throw new RuntimeException(throwable);
    }

    private void closeReader(BufferedReader reader) {
        try {
            log.info("Close reader.");
            reader.close();
        } catch (IOException e) {
            log.error("File reader can not be closed.");
            throw new RuntimeException(e);
        }
    }
}
