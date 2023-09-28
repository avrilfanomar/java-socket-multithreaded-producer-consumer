package com.github.avrilfanomar.news.feed.producer;

import com.github.avrilfanomar.news.feed.core.domain.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultMessageProducer implements MessageProducer {

    private final List<String> words;
    private final short minWords;
    private final short maxWords;
    private final short minPriority;
    private final int priorityRandomBase;
    private final Random random = new Random();
    private final Map<Short, Integer> priorityThresholdsMap = new HashMap<>();

    public DefaultMessageProducer(Properties properties) {
        this.words = Stream.of(properties.getProperty("words").split(",")).collect(Collectors.toList());
        this.minWords = Short.parseShort(properties.getProperty("words.quantity.min"));
        this.maxWords = Short.parseShort(properties.getProperty("words.quantity.max"));
        this.minPriority = Short.parseShort(properties.getProperty("priority.min"));
        short maxPriority = Short.parseShort(properties.getProperty("priority.max"));
        short priorityGenerationFactor = Short.parseShort(properties.getProperty("priority.generation.factor"));
        this.priorityRandomBase = (int) Math.pow(priorityGenerationFactor, maxPriority - minPriority + 1);
        int base = priorityRandomBase;
        for (short priority = minPriority; priority <= maxPriority; priority++) {
            priorityThresholdsMap.put(priority, base - (int) Math.pow(priorityGenerationFactor, maxPriority - priority));
            base /= priorityGenerationFactor;
        }
    }

    @Override
    public String generate() {
        final Message message = new Message(generateNextHeadline(), generateNextPriority());
        return message.encode();
    }

    private String generateNextHeadline() {
        int wordCount = random.nextInt(maxWords - minWords + 1) + minWords;
        return Stream.generate(() -> words.get(random.nextInt(words.size()))).limit(wordCount).collect(Collectors.joining(" "));
    }

    private short generateNextPriority() {
        int randomInt = random.nextInt(priorityRandomBase);
        short priority = minPriority;
        while (true) {
            int threshold = priorityThresholdsMap.get(priority);
            if (!(randomInt > threshold)) {
                break;
            }
            randomInt -= threshold;
            priority++;
        }
        return priority;
    }
}
