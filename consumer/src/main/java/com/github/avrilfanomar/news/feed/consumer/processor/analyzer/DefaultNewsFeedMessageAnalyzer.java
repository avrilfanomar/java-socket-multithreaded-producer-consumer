package com.github.avrilfanomar.news.feed.consumer.processor.analyzer;

import com.github.avrilfanomar.news.feed.consumer.SynchronizedPriorityLimitedCollection;
import com.github.avrilfanomar.news.feed.core.domain.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultNewsFeedMessageAnalyzer implements NewsFeedMessageAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(DefaultNewsFeedMessageAnalyzer.class.getName());

    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");

    private final Set<String> positiveWords;
    private final AtomicLong positiveHeadlinesCount = new AtomicLong();
    private final AtomicLong negativeHeadlinesCount = new AtomicLong();
    private final SynchronizedPriorityLimitedCollection<Message> topHeadlines;

    public DefaultNewsFeedMessageAnalyzer(Properties properties) {
        this.positiveWords = Stream.of(properties.getProperty("words.positive").split(",")).collect(Collectors.toSet());
        short topElementCount = Short.parseShort(properties.getProperty("top.headlines.count"));
        this.topHeadlines = new SynchronizedPriorityLimitedCollection<>(topElementCount);
    }

    @Override
    public void submit(Message message) {
        if (isPositive(message.getHeadline())) {
            positiveHeadlinesCount.incrementAndGet();
            topHeadlines.offer(message);
        } else {
            negativeHeadlinesCount.incrementAndGet();
        }
    }

    @Override
    public void printTopHeadlines() {
        for (Message message : topHeadlines.getAllAndReset()) {
            LOGGER.info(message.getHeadline() + " (priority: " + message.getPriority() + ")");
        }
        LOGGER.info("Total positive headlines: " + positiveHeadlinesCount.getAndSet(0));
        LOGGER.info("Total negative headlines: " + negativeHeadlinesCount.getAndSet(0));
    }

    private boolean isPositive(String headline) {
        Collection<String> messageWords = SPACE_PATTERN.splitAsStream(headline).collect(Collectors.toCollection(ArrayList::new));
        int minPositiveWords = messageWords.size() / 2 + 1;
        messageWords.retainAll(positiveWords);
        return messageWords.size() >= minPositiveWords;
    }
}
