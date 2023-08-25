package com.github.avrilfanomar.news.feed.core;

public interface Prioritized<T> extends Comparable<T> {

    short getPriority();
}
