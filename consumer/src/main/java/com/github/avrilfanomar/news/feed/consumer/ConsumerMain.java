package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.consumer.processor.QueuedConcurrentNewsFeedMessageProcessor;
import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.DefaultNewsFeedMessageAnalyzer;
import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerMain {

    public static void main(String[] args) throws Exception {
        try (var scheduler = Executors.newScheduledThreadPool(1)) {
            var properties = PropertiesUtils.loadProperties(ConsumerMain.class.getClassLoader());

            var headlineCollector = new DefaultNewsFeedMessageAnalyzer(properties);
            scheduler.scheduleAtFixedRate(headlineCollector::printTopHeadlines, 10, 10, TimeUnit.SECONDS);

            var processor = new QueuedConcurrentNewsFeedMessageProcessor(properties, headlineCollector);
            try (var consumer = new ServerSocketConsumer(properties, processor)) {
                consumer.start();
            }
        }
    }
}