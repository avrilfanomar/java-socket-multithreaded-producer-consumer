package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.properties.AbstractSocketConfig;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultSocketMessageProducer extends AbstractSocketConfig implements SocketMessageSender {

    private static final Logger LOGGER = Logger.getLogger(DefaultSocketMessageProducer.class.getName());

    private final long sleepMilliseconds;
    private final MessageProducer messageProducer;
    private final Charset charset;


    public DefaultSocketMessageProducer(Properties properties, MessageProducer messageProducer) {
        super(properties);
        this.sleepMilliseconds = Long.parseLong(properties.getProperty("producer.sleep.milliseconds"));
        this.messageProducer = messageProducer;
        this.charset = Charset.forName(properties.getProperty("charset"));
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
                ByteBuffer buffer = ByteBuffer.wrap(messageProducer.produce().getBytes(charset));
                int written = socketClient.write(buffer);
                if (written < buffer.position()) {
                    LOGGER.warning("Message not sent, exiting");
                    break;
                }
                if (sleepMilliseconds > 0) {
                    Thread.sleep(sleepMilliseconds);
                }
            }
        }
        return null;
    }
}
