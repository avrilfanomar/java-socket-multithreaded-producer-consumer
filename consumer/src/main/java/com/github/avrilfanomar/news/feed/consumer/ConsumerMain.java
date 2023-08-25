package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.DefaultHeadlineCollector;
import com.github.avrilfanomar.news.feed.consumer.processor.analyzer.FeedHeadlineAnalyzer;
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

            DefaultHeadlineCollector headlineCollector = new DefaultHeadlineCollector(properties);
            scheduler.scheduleAtFixedRate(headlineCollector::printTopHeadlines, 10, 10, TimeUnit.SECONDS);

            FeedHeadlineAnalyzer analyzer = new FeedHeadlineAnalyzer(properties, headlineCollector);
            ServerSocketConsumer consumer = new ServerSocketConsumer(properties, analyzer);
            consumer.start();
        } finally {
            scheduler.shutdown();
        }
    }
}