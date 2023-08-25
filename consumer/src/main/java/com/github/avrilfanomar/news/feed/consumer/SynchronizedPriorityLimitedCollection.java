package com.github.avrilfanomar.news.feed.consumer;

import com.github.avrilfanomar.news.feed.core.Prioritized;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SynchronizedPriorityLimitedCollection<E extends Prioritized<E>> {

    private final int size;
    private final TreeSet<E> elements;

    public SynchronizedPriorityLimitedCollection(int size) {
        this.size = size;
        this.elements = new TreeSet<>(Comparable::compareTo);
    }

    public synchronized void offer(E item) {
        elements.add(item);
        if (elements.size() > size) {
            elements.pollFirst();
        }
    }

    public synchronized List<E> getAllAndReset() {
        final List<E> list = new ArrayList<>(elements);
        elements.clear();
        return list;
    }
}
