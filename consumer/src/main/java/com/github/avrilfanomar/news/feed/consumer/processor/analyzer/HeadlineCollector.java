package com.github.avrilfanomar.news.feed.consumer.processor.analyzer;

import com.github.avrilfanomar.news.feed.core.message.Message;

public interface HeadlineCollector {

    void submit(Message message);

    void printTopHeadlines();
}
