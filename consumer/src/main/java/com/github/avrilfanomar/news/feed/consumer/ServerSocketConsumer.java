package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.consumer.processor.MessageProcessor;
import com.github.avrilfanomar.news.feed.core.properties.AbstractSocketConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ServerSocketConsumer extends AbstractSocketConfig {
    private static final Logger LOGGER = Logger.getLogger(ServerSocketConsumer.class.getName());

    private final MessageProcessor messageProcessor;
    private final ConcurrentHashMap<SelectionKey, ByteBuffer> channelBuffers = new ConcurrentHashMap<>();
    private final int bufferSize;

    public ServerSocketConsumer(Properties properties, MessageProcessor messageProcessor) {
        super(properties);
        this.messageProcessor = messageProcessor;
        this.bufferSize = Integer.parseInt(properties.getProperty("buffer.size"));
    }

    public void start() throws IOException, InterruptedException {
        try (Selector selector = Selector.open()) {
            try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
                serverChannel.bind(new InetSocketAddress(host, port));
                serverChannel.configureBlocking(false);
                serverChannel.register(selector, serverChannel.validOps());
                acceptMessages(selector, serverChannel);
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void acceptMessages(Selector selector, ServerSocketChannel channel) throws IOException, InterruptedException {
        LOGGER.info("Ready to accept messages");
        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            selector.select();//blocking selection
            processKeys(selector, channel);
        }
    }

    private void processKeys(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            final SelectionKey key = keyIterator.next();
            keyIterator.remove();//each key is processed only once

            if (!key.isValid()) {
                LOGGER.warning("Invalid key");
                continue;
            }
            if (key.isAcceptable()) {
                acceptNewSocket(selector, serverSocketChannel);
            } else if (key.isReadable()) {
                readMessage(key);
            } else {
                LOGGER.warning("Closing non acceptable nor readable key channel");
                key.channel().close();
                key.cancel();
            }
        }
    }

    private void acceptNewSocket(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel newSocket = serverChannel.accept();
        newSocket.configureBlocking(false);
        newSocket.register(selector, SelectionKey.OP_READ);
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = channelBuffers.computeIfAbsent(key, addr -> ByteBuffer.allocate(bufferSize));
        final int numRead = channel.read(buffer);
        if (numRead == -1) {
            LOGGER.fine("Socket closed by " + channel.getRemoteAddress());
            channelBuffers.remove(key);
            channel.close();
            key.cancel();
        } else if (buffer.position() > 0) {
            messageProcessor.process(buffer);
        }
    }
}
