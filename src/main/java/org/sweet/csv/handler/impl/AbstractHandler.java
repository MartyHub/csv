package org.sweet.csv.handler.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sweet.csv.handler.Handler;
import org.sweet.csv.handler.HandlerException;
import org.sweet.csv.mapper.MappedBean;


public abstract class AbstractHandler<T> implements Handler<T> {

    protected final MappedBean<T> mappedBean;

    protected boolean hasHeader = true;

    public AbstractHandler(MappedBean<T> mappedBean) {
        this.mappedBean = mappedBean;
    }

    public final boolean isFirstLineHeader() {
        return hasHeader;
    }

    public final void setFirstLineHeader(boolean firstLineHeader) {
        this.hasHeader = firstLineHeader;
    }

    public List<T> parse(File file) {
        InputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(file));

            return parse(is);
        } catch (FileNotFoundException e) {
            throw new HandlerException("Failed to find file <" + file.getAbsolutePath() + ">", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public List<T> parse(InputStream is) {
        try {
            List<T> result = new LinkedList<T>();

            doParse(is, result);

            return result;
        } catch (IOException e) {
            throw new HandlerException("Failed to parse stream", e);
        }
    }

    public void write(Iterable<T> iterable, File file) {
        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));

            write(iterable, os);
        } catch (FileNotFoundException e) {
            throw new HandlerException("Failed to find file <" + file.getAbsolutePath() + ">", e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    protected abstract void doParse(InputStream is, List<T> result) throws IOException;
}
