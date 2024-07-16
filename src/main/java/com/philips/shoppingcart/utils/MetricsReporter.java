package com.philips.shoppingcart.utils;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class MetricsReporter {

    private final StatsDClient statsd;

    public MetricsReporter(@Value("${spring.application.name}") String appName,
                           @Value("${cart.webapp.statsd.client.host}") String host) {
        this.statsd = new NonBlockingStatsDClient(appName, host, 8125);
    }

    public void recordCounter(HttpStatus status){
        String counterName = new StringBuilder().append(status.toString())
                                                .toString();
        statsd.incrementCounter(counterName);
    }

}
