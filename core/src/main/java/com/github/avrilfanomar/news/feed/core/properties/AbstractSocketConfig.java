package com.github.avrilfanomar.news.feed.core.properties;

import java.util.Properties;

public abstract class AbstractSocketConfig {

    protected final String host;
    protected final int port;
    protected final int bufferSize;

    public AbstractSocketConfig(Properties properties) {
        this.host = properties.getProperty("host");
        this.port = Integer.parseInt(properties.getProperty("port"));
        this.bufferSize = Integer.parseInt(properties.getProperty("buffer.size"));
    }
}
