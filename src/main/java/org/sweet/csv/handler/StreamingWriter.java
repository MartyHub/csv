package org.sweet.csv.handler;

public interface StreamingWriter<T> {

    boolean isFirstLineHeader();

    void setFirstLineHeader(final boolean firstLineHeader);

    void write(T... items);

    void close();
}