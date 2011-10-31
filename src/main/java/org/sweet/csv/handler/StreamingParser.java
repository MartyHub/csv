package org.sweet.csv.handler;

public interface StreamingParser<T> extends Iterable<T> {

    boolean isFirstLineHeader();

    void setFirstLineHeader(final boolean firstLineHeader);
}