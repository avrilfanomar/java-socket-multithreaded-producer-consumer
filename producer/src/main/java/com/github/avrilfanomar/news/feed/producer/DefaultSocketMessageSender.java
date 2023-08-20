package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.properties.AbstractSocketConfig;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultSocketMessageSender extends AbstractSocketConfig implements SocketMessageSender {

    private static final Logger LOGGER = Logger.getLogger(DefaultSocketMessageSender.class.getName());

    private final long sleepMilliseconds;
    private final MessageProducer messageProducer;


    public DefaultSocketMessageSender(Properties properties, MessageProducer messageProducer) {
        super(properties);
        this.sleepMilliseconds = Long.parseLong(properties.getProperty("producer.sleep.milliseconds"));
        this.messageProducer = messageProducer;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public Object call() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);

        LOGGER.info("Connecting to Server...");
        try (SocketChannel socketClient = SocketChannel.open(socketAddress)) {
            LOGGER.info("Socket connected successfully");
            for (;;) {
                ByteBuffer buffer = ByteBuffer.wrap(messageProducer.produce().getBytes());
                int written = socketClient.write(buffer);
                buffer.clear();
                if (written < 1) {
                    LOGGER.warning("Message not sent, exiting");
                    break;
                }
                if (sleepMilliseconds > 0) {
                    Thread.sleep(sleepMilliseconds);
                }
            }
        }
        return 0;
    }

}
