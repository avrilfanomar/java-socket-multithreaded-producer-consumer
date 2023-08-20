package com.github.avrilfanomar.news.feed.core.properties;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;

public class PropertiesUtils {

    private PropertiesUtils() {
    }

    public static Properties loadProperties(ClassLoader classLoader) throws IOException, URISyntaxException {
        Properties properties = new Properties();
        URL commonFileResource = Objects.requireNonNull(classLoader.getResource("common.properties"));
        URL appFileResource = Objects.requireNonNull(classLoader.getResource("application.properties"));
        properties.load(Files.newInputStream(new File(commonFileResource.toURI()).toPath(), StandardOpenOption.READ));
        properties.load(Files.newInputStream(new File(appFileResource.toURI()).toPath(), StandardOpenOption.READ));
        return properties;
    }
}
