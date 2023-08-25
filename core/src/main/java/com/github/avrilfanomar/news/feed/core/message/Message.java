package com.github.avrilfanomar.news.feed.core.message;

import com.github.avrilfanomar.news.feed.core.Prioritized;

import java.util.Objects;

public class Message implements Prioritized<Message> {

    public static final String SEPARATOR = ";";
    public static final String EOM = "~";

    private final String headline;
    private final short priority;

    public Message(String message, short priority) {
        this.headline = message;
        this.priority = priority;
    }

    public String getHeadline() {
        return headline;
    }

    @Override
    public short getPriority() {
        return priority;
    }

    public String encode() {
        return headline + SEPARATOR + priority + EOM;
    }

    public static Message parseMessage(String message) {
        final int headlineEndIndex = message.lastIndexOf(SEPARATOR);
        final short priority = Short.parseShort(message.substring(headlineEndIndex + 1));
        return new Message(message.substring(0, headlineEndIndex), priority);
    }

    @Override
    public int compareTo(Message o) {
        int compare = Short.compare(priority, o.priority);
        if (compare == 0) {
            return headline.compareTo(o.headline);
        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return priority == message.priority && Objects.equals(headline, message.headline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headline);
    }
}
