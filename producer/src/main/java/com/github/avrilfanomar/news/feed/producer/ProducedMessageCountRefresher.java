package com.github.avrilfanomar.news.feed.producer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ProducedMessageCountRefresher implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ProducedMessageCountRefresher.class.getName());

    private final AtomicLong counter = new AtomicLong();
    private final long frequencyThreshold;

    public ProducedMessageCountRefresher(Properties properties) {
        this.frequencyThreshold = Long.parseLong(properties.getProperty("producer.frequency.per.second"));
    }

    @Override
    public void run() {
        long produced = counter.getAndSet(0);
        if (produced < frequencyThreshold) {
            LOGGER.warning("Produced only around " + produced + " messages out of " + frequencyThreshold);
        }
    }

    public AtomicLong getCounter() {
        return counter;
    }
}
