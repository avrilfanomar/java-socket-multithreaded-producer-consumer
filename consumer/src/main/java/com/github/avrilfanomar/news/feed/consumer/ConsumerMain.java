package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.consumer.processor.LoggingMessageProcessor;
import com.github.avrilfanomar.news.feed.core.properties.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class ConsumerMain {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Properties properties = PropertiesUtils.loadProperties(ConsumerMain.class.getClassLoader());
        ServerSocketConsumer consumer = new ServerSocketConsumer(properties, new LoggingMessageProcessor());
        consumer.start();
    }
}