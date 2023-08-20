package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ProducerMain {

    private static final Logger LOGGER = Logger.getLogger(ProducerMain.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException, ExecutionException {
        Properties properties = PropertiesUtils.loadProperties(ProducerMain.class.getClassLoader());
        int threadQuantity = Integer.parseInt(properties.getProperty("producer.quantity"));
        LOGGER.info("Starting " + threadQuantity + " threads");
        runThreads(threadQuantity, properties);
        LOGGER.info("All threads finished");
    }

    private static void runThreads(int threadQuantity, Properties properties) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadQuantity);
        final List<Future<Object>> futures = executorService.invokeAll(buildProducers(threadQuantity, properties));
        //wait for threads
        for (Future<Object> future : futures) {
            future.get();
        }
    }

    private static Collection<? extends Callable<Object>> buildProducers(int threadNumber, Properties properties) {
        Collection<SocketMessageSender> producers = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            producers.add(new DefaultSocketMessageSender(properties, new DefaultMessageProducer(properties)));
        }
        return producers;
    }


}