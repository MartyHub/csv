package org.sweet.csv.handler;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Handler<T> {

    boolean isFirstLineHeader();

    void setFirstLineHeader(final boolean firstLineHeader);

    List<T> parse(File file);

    List<T> parse(InputStream is);

    void write(Iterable<T> iterable, File file);

    void write(Iterable<T> iterable, OutputStream os);
}