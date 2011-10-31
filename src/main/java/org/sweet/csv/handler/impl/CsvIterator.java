package org.sweet.csv.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.sweet.csv.handler.HandlerException;
import org.sweet.csv.mapper.MappedBean;
import org.sweet.csv.mapper.MappedProperty;
import org.sweet.csv.utils.FileUtils;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;

public class CsvIterator<T> implements Iterator<T> {

    private final CSVParse parser;

    private final MappedBean<T> mappedBean;

    private String[] line;

    public CsvIterator(InputStream is, final boolean hasHeader, MappedBean<T> mappedBean) throws IOException {
        this.parser = new CSVParser(FileUtils.toBufferedInputStream(is));

        readNextLine();

        if (hasHeader && line != null) {
            mappedBean = mappedBean.withHeaders(line);

            readNextLine();
        }

        this.mappedBean = mappedBean;
    }

    public boolean hasNext() {
        return line != null;
    }

    public T next() {
        final T bean = mappedBean.newInstance();
        final int length = line.length;
        int i = 0;

        for (MappedProperty<T> mappedProperty : mappedBean) {
            mappedProperty.setValueFromString(bean, line[i]);

            ++i;

            if (i >= length) {
                break;
            }
        }

        readNextLine();

        return bean;
    }

    public void remove() {
        throw new NotImplementedException("Can't remove element from parsing");
    }

    private void readNextLine() {
        try {
            line = parser.getLine();

            while (line != null && isBlank(line)) {
                line = parser.getLine();
            }
        } catch (IOException e) {
            throw new HandlerException("Failed to parse stream", e);
        }
    }

    private boolean isBlank(String[] line) {
        for (String s : line) {
            if (StringUtils.trimToNull(s) != null) {
                return false;
            }
        }

        return true;
    }
}
