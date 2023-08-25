package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ProducerMain {

    private static final Logger LOGGER = Logger.getLogger(ProducerMain.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Properties properties = PropertiesUtils.loadProperties(ProducerMain.class.getClassLoader());

        runThreadsAndWaitForThem(properties);
        LOGGER.info("All threads finished");
    }

    private static void runThreadsAndWaitForThem(Properties properties) throws InterruptedException {
        short threadQuantity = Short.parseShort(properties.getProperty("producer.threads"));
        LOGGER.info("Starting " + threadQuantity + " threads");

        ExecutorService executorService = Executors.newFixedThreadPool(threadQuantity);
        final List<Future<Void>> futures = executorService.invokeAll(buildProducers(threadQuantity, properties));

        //wait for threads
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                LOGGER.severe("Exception while waiting for thread to finish: " + e.getMessage());
            }
        }
        executorService.shutdown();
    }

    private static Collection<? extends Callable<Void>> buildProducers(int threadNumber, Properties properties) {
        Collection<SocketMessageSender> producers = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            producers.add(new DefaultSocketMessageProducer(properties, new DefaultMessageProducer(properties)));
        }
        return producers;
    }


}