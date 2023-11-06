package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;
import com.github.avrilfanomar.news.feed.producer.socket.DefaultSocketMessageProducer;
import com.github.avrilfanomar.news.feed.producer.socket.SocketMessageSender;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ProducerMain {

    private static final Logger LOGGER = Logger.getLogger(ProducerMain.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        var properties = PropertiesUtils.loadProperties(ProducerMain.class.getClassLoader());

        var countRefresher = new ProducedMessageCountRefresher(properties);
        try (var scheduler = Executors.newScheduledThreadPool(1)) {
            scheduler.scheduleAtFixedRate(countRefresher, 1, 1, TimeUnit.SECONDS);
            runProducerThreadsAndWaitForThem(properties, countRefresher.getCounter());
        }
        LOGGER.info("All threads finished");
    }

    private static void runProducerThreadsAndWaitForThem(Properties properties, AtomicLong counter)
        throws InterruptedException {
        var threadQuantity = Short.parseShort(properties.getProperty("producer.threads.count"));
        LOGGER.info("Starting " + threadQuantity + " threads");

        try (var executorService = Executors.newFixedThreadPool(threadQuantity)) {
            executorService.invokeAll(buildProducers(threadQuantity, properties, counter));
        }
    }

    private static Collection<? extends Callable<Void>> buildProducers(int threadNumber, Properties properties, AtomicLong counter) {
        var producers = new ArrayList<SocketMessageSender>();
        for (int i = 0; i < threadNumber; i++) {
            producers.add(new DefaultSocketMessageProducer(properties, new DefaultMessageProducer(properties), counter));
        }
        return producers;
    }


}