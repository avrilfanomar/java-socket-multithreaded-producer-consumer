package com.github.avrilfanomar.news.feed.consumer.processor.analyzer;

import com.github.avrilfanomar.news.feed.consumer.processor.MessageProcessor;
import com.github.avrilfanomar.news.feed.core.message.Message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FeedHeadlineAnalyzer implements MessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(FeedHeadlineAnalyzer.class.getName());

    private static final Pattern EOM_PATTERN = Pattern.compile(Message.EOM);


    private final HeadlineCollector headlineCollector;
    private final Charset charset;


    public FeedHeadlineAnalyzer(Properties properties, HeadlineCollector headlineCollector) {
        this.headlineCollector = headlineCollector;
        this.charset = Charset.forName(properties.getProperty("charset"));
    }

    @Override
    public void process(ByteBuffer buffer) {
        final String fullBuffer = new String(buffer.array(), 0, buffer.position(), charset);
        final int endIndex = fullBuffer.lastIndexOf(Message.EOM);
        if (endIndex == -1) {
            return;
        }
        final String[] encodedMessages = EOM_PATTERN.split(fullBuffer.subSequence(0, endIndex));
        for (String encodedMessage : encodedMessages) {
            processMessage(encodedMessage);
        }
        buffer.clear();
        if (endIndex < fullBuffer.length() - 1) {
            buffer.put(fullBuffer.substring(endIndex + 1).getBytes(charset));
        }
    }

    private void processMessage(String encodedMessage) {
        try {
            Message message = Message.parseMessage(encodedMessage);
            headlineCollector.submit(message);
        } catch (RuntimeException e) {
            LOGGER.warning("Failed to process message: " + encodedMessage);
        }
    }
}
