package org.sweet.csv.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.sweet.csv.handler.HandlerException;
import org.sweet.csv.handler.StreamingParser;
import org.sweet.csv.mapper.MappedBean;


public class CsvStreamingParser<T> implements StreamingParser<T> {

    private final MappedBean<T> mappedBean;

    private final InputStream is;

    private boolean firstLineHeader = true;

    public CsvStreamingParser(MappedBean<T> mappedBean, InputStream is) {
        this.mappedBean = mappedBean;
        this.is = is;
    }

    public boolean isFirstLineHeader() {
        return firstLineHeader;
    }

    public void setFirstLineHeader(final boolean firstLineHeader) {
        this.firstLineHeader = firstLineHeader;
    }

    public Iterator<T> iterator() {
        try {
            return new CsvIterator<T>(is, firstLineHeader, mappedBean);
        } catch (IOException e) {
            throw new HandlerException("Failed to parse stream", e);
        }
    }
}
