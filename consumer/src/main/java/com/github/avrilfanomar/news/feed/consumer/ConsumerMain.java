package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.consumer.processor.MessageProcessor;
import com.github.avrilfanomar.news.feed.consumer.processor.QueuedConcurrentNewsFeedMessageProcessor;
import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.DefaultNewsFeedMessageAnalyzer;
import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsumerMain {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try {
            Properties properties = PropertiesUtils.loadProperties(ConsumerMain.class.getClassLoader());

            DefaultNewsFeedMessageAnalyzer headlineCollector = new DefaultNewsFeedMessageAnalyzer(properties);
            scheduler.scheduleAtFixedRate(headlineCollector::printTopHeadlines, 10, 10, TimeUnit.SECONDS);

            MessageProcessor processor = new QueuedConcurrentNewsFeedMessageProcessor(properties, headlineCollector);
            ServerSocketConsumer consumer = new ServerSocketConsumer(properties, processor);
            consumer.start();
        } finally {
            scheduler.shutdown();
        }
    }
}