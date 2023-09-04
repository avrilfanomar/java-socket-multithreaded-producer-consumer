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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ProducerMain {

    private static final Logger LOGGER = Logger.getLogger(ProducerMain.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Properties properties = PropertiesUtils.loadProperties(ProducerMain.class.getClassLoader());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try {
            final ProducedMessageCountRefresher countRefresher = new ProducedMessageCountRefresher(properties);
            scheduler.scheduleAtFixedRate(countRefresher, 1, 1, TimeUnit.SECONDS);
            runProducerThreadsAndWaitForThem(properties, countRefresher.getCounter());
            LOGGER.info("All threads finished");
        } finally {
            scheduler.shutdown();
        }
    }

    private static void runProducerThreadsAndWaitForThem(Properties properties, AtomicLong counter)
        throws InterruptedException {
        short threadQuantity = Short.parseShort(properties.getProperty("producer.threads.count"));
        LOGGER.info("Starting " + threadQuantity + " threads");

        ExecutorService executorService = Executors.newFixedThreadPool(threadQuantity);
        try {
            executorService.invokeAll(buildProducers(threadQuantity, properties, counter));
        } finally {
            executorService.shutdown();
        }
    }

    private static Collection<? extends Callable<Void>> buildProducers(int threadNumber, Properties properties, AtomicLong counter) {
        Collection<SocketMessageSender> producers = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            producers.add(new DefaultSocketMessageProducer(properties, new DefaultMessageProducer(properties), counter));
        }
        return producers;
    }


}