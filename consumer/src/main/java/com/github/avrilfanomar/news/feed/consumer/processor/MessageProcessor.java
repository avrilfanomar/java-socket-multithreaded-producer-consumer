package com.github.avrilfanomar.news.feed.consumer.processor;

import java.nio.ByteBuffer;

public interface MessageProcessor extends AutoCloseable {

    /**
     * Processes the message and clears the consumed part of the buffer afterward.
     *
     * @param buffer
     *     the message
     */
    void process(ByteBuffer buffer);
}
