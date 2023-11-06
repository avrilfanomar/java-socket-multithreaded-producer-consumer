package com.github.avrilfanomar.news.feed.consumer.processor;

import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.NewsFeedMessageAnalyzer;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractConcurrentNewsFeedMessageProcessor extends NewsFeedMessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(AbstractConcurrentNewsFeedMessageProcessor.class.getName());

    private ExecutorService executorService;

    public AbstractConcurrentNewsFeedMessageProcessor(Properties properties, NewsFeedMessageAnalyzer newsFeedMessageAnalyzer) {
        super(properties, newsFeedMessageAnalyzer);
    }

    @Override
    public void close() {
        executorService.close();
    }

    protected void runThreads(short threadQuantity) {
        LOGGER.info("Starting " + threadQuantity + " consuming threads");
        this.executorService = Executors.newFixedThreadPool(threadQuantity);
        buildConsumers(threadQuantity).forEach(executorService::execute);
    }

    private Collection<? extends Runnable> buildConsumers(int threadNumber) {
        return IntStream.range(0, threadNumber)
                        .<Runnable>mapToObj(i -> this::consume)
                        .collect(Collectors.toList());
    }

    abstract protected void consume();
}
