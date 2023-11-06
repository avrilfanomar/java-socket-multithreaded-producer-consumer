package com.github.avrilfanomar.news.feed.consumer.processor;

import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.NewsFeedMessageAnalyzer;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueuedConcurrentNewsFeedMessageProcessor extends AbstractConcurrentNewsFeedMessageProcessor {

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

    @Override
    protected void consume() {
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
