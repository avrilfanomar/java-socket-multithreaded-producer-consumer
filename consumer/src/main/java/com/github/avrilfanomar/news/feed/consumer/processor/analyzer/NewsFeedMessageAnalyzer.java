package com.github.avrilfanomar.news.feed.consumer.processor.analyzer;

import com.github.avrilfanomar.news.feed.core.message.Message;

public interface NewsFeedMessageAnalyzer {

    /**
     * Submits a message for analysis.
     * @param message the message to be analyzed
     */
    void submit(Message message);

    /**
     * Prints the top headlines, the number of positive and negative messages received, clears the counts afterward.
     * This method is thread-safe, but it's not blocking new messages from processing,
     * thus the positive and negative message count may be not exact.
     */
    void printTopHeadlines();
}
