package com.github.avrilfanomar.news.feed.consumer.processor;

import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.NewsFeedMessageAnalyzer;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class BlockingQueueNewsFeedMessageProcessor extends AbstractConcurrentNewsFeedMessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(BlockingQueueNewsFeedMessageProcessor.class.getName());

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000000);

    public BlockingQueueNewsFeedMessageProcessor(Properties properties,
                                                 NewsFeedMessageAnalyzer newsFeedMessageAnalyzer) {
        super(properties, newsFeedMessageAnalyzer);
        runThreads(Short.parseShort(properties.getProperty("consumer.threads.count")));
    }

    @Override
    protected void processMessage(String encodedMessage) {
        try {
            if (!queue.offer(encodedMessage, 1, TimeUnit.SECONDS)) {
                LOGGER.warning("Failed to queue message: " + encodedMessage);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void consume() {
        while (!Thread.interrupted()) {
            try {
                String message = queue.take();
                processMessage(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
