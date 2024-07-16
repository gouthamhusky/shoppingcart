package com.philips.shoppingcart.utils;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class MetricsReporter {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${cart.webapp.statsd.client.host}")
    private String host;


    private final StatsDClient statsd = new NonBlockingStatsDClient(appName, host, 8125);

    public void recordCounter(HttpStatus status){
        String counterName = new StringBuilder().append(status.toString())
                                                .toString();
        statsd.incrementCounter(counterName);
    }

}
