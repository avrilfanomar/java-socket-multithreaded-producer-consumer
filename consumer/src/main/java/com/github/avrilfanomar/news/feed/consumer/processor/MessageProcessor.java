package com.github.avrilfanomar.news.feed.consumer.processor;

import java.nio.ByteBuffer;

public interface MessageProcessor {

    void process(ByteBuffer buffer);
}
