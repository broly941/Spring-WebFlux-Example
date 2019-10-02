package com.example.WebFluxExample.controller;

import com.example.WebFluxExample.service.ReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.FileNotFoundException;

@Slf4j
@RequestMapping("/webflux")
@RestController
public class CustomReactiveController {

    private final ReactiveService reactiveService;

    public CustomReactiveController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    @GetMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getMessages() throws FileNotFoundException {
        log.info("Get messages from file.");
        return reactiveService.readFile();
    }
}
