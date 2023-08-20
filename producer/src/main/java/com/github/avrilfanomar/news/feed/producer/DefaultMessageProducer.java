package com.github.avrilfanomar.news.feed.producer;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultMessageProducer implements MessageProducer {

    private final Set<String> words;
    private final short minWords;
    private final short maxWords;
    private final short minPriority;
    private final short maxPriority;

    public DefaultMessageProducer(Properties properties) {
        this.words = Stream.of(properties.getProperty("words").split(",")).collect(Collectors.toSet());
        this.minWords = Short.parseShort(properties.getProperty("words.quantity.min"));
        this.maxWords = Short.parseShort(properties.getProperty("words.quantity.max"));
        this.minPriority = Short.parseShort(properties.getProperty("priority.min"));
        this.maxPriority = Short.parseShort(properties.getProperty("priority.max"));
    }

    @Override
    public String produce() {
        return "success failure high low über";
    }
}
