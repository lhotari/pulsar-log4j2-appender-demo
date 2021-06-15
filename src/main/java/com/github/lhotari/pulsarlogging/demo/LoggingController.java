package com.github.lhotari.pulsarlogging.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingController.class);

    @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public String log(@RequestParam(value = "msg", required = false) String msg) {
        if (msg != null) {
            LOG.info("{}", msg);
        } else {
            LOG.info("/log called");
        }
        return "OK";
    }

    @PostMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public String logPost(@RequestBody String body) {
        LOG.info("{}", body);
        return "OK";
    }
}
