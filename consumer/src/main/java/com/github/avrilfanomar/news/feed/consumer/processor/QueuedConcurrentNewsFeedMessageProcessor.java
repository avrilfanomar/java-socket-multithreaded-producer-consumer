package com.github.avrilfanomar.news.feed.consumer.processor;

import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.NewsFeedMessageAnalyzer;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueuedConcurrentNewsFeedMessageProcessor extends NewsFeedMessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(QueuedConcurrentNewsFeedMessageProcessor.class.getName());

    private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    public QueuedConcurrentNewsFeedMessageProcessor(Properties properties,
                                                    NewsFeedMessageAnalyzer newsFeedMessageAnalyzer) {
        super(properties, newsFeedMessageAnalyzer);
        runThreads(Short.parseShort(properties.getProperty("consumer.threads.count")));
    }

    @Override
    protected void processMessage(String encodedMessage) {
        queue.add(encodedMessage);
    }

    private void runThreads(short threadQuantity) {
        LOGGER.info("Starting " + threadQuantity + " consuming threads");
        ExecutorService executorService = Executors.newFixedThreadPool(threadQuantity);
        buildConsumers(threadQuantity).forEach(executorService::execute);
    }

    private Collection<? extends Runnable> buildConsumers(int threadNumber) {
        return IntStream.range(0, threadNumber)
                        .<Runnable>mapToObj(i -> this::consume)
                        .collect(Collectors.toList());
    }

    private void consume() {
        while (!Thread.interrupted()) {
            String message = queue.poll();
            if (message == null) {
                Thread.yield();
            } else {
                super.processMessage(message);
            }
        }
    }
}
