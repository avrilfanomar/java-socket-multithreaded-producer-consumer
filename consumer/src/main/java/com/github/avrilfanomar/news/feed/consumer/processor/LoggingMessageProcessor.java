package com.github.avrilfanomar.news.feed.consumer.processor;

import java.nio.ByteBuffer;
import java.util.logging.Logger;


public class LoggingMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(LoggingMessageProcessor.class.getName());

    @Override
    public void process(ByteBuffer buffer) {
        LOGGER.info("Received a message: " + new String(buffer.array()).trim());
    }
}
