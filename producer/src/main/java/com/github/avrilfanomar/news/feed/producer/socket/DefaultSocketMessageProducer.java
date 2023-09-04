package com.github.avrilfanomar.news.feed.producer.socket;

import com.github.avrilfanomar.news.feed.core.properties.AbstractSocketConfig;
import com.github.avrilfanomar.news.feed.producer.MessageProducer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class DefaultSocketMessageProducer extends AbstractSocketConfig implements SocketMessageSender {

    private static final Logger LOGGER = Logger.getLogger(DefaultSocketMessageProducer.class.getName());

    private final long sleepMilliseconds;
    private final MessageProducer messageProducer;
    private final Charset charset;
    private final AtomicLong counter;
    private final long counterThreshold;


    public DefaultSocketMessageProducer(Properties properties, MessageProducer messageProducer, AtomicLong counter) {
        super(properties);
        this.sleepMilliseconds = Long.parseLong(properties.getProperty("producer.sleep.milliseconds"));
        this.counterThreshold = Long.parseLong(properties.getProperty("producer.frequency.per.second"));
        this.messageProducer = messageProducer;
        this.charset = Charset.forName(properties.getProperty("charset"));
        this.counter = counter;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public Void call() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);

        LOGGER.info("Connecting to Server...");
        try (SocketChannel socketClient = SocketChannel.open(socketAddress)) {
            LOGGER.info("Socket connected successfully");
            for (;;) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (counter.incrementAndGet() <= counterThreshold) {
                    ByteBuffer buffer = ByteBuffer.wrap(messageProducer.generate().getBytes(charset));
                    int written = socketClient.write(buffer);
                    if (written < buffer.position()) {
                        LOGGER.warning("Message not sent, exiting");
                        break;
                    }
                } else {
                    counter.decrementAndGet();
                    if (sleepMilliseconds > 0) {
                        Thread.sleep(sleepMilliseconds);
                    } else {
                        Thread.yield();
                    }
                }
            }
        }
        return null;
    }
}
