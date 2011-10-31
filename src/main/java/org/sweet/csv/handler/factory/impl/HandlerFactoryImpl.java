package org.sweet.csv.handler.factory.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.sweet.csv.handler.Handler;
import org.sweet.csv.handler.HandlerException;
import org.sweet.csv.handler.StreamingParser;
import org.sweet.csv.handler.StreamingWriter;
import org.sweet.csv.handler.factory.HandlerFactory;
import org.sweet.csv.handler.impl.CsvHandler;
import org.sweet.csv.handler.impl.CsvStreamingParser;
import org.sweet.csv.handler.impl.CsvStreamingWriter;
import org.sweet.csv.handler.impl.ExcelHandler;
import org.sweet.csv.mapper.MappedBean;
import org.sweet.csv.utils.MediaType;


@Component
public class HandlerFactoryImpl implements HandlerFactory, ApplicationContextAware {

    private final Map<Class<?>, MappedBean<?>> mappedBeans = new HashMap<Class<?>, MappedBean<?>>();

    @SuppressWarnings("rawtypes")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MappedBean> mappedBeans = applicationContext.getBeansOfType(MappedBean.class);

        for (MappedBean<?> mappedBean : mappedBeans.values()) {
            this.mappedBeans.put(mappedBean.getType(), mappedBean);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Handler<T> createHandler(Class<T> type, MediaType mediaType) {
        if (mediaType == null) {
            mediaType = MediaType.EXCEL;
        }

        MappedBean<T> mappedBean = (MappedBean<T>) mappedBeans.get(type);

        if (mappedBean == null) {
            throw new IllegalArgumentException("Don't know how to handle <" + type + ">");
        }

        if (MediaType.CSV.equals(mediaType)) {
            return new CsvHandler<T>(mappedBean);
        } else if (MediaType.EXCEL.equals(mediaType)) {
            return new ExcelHandler<T>(mappedBean);
        }

        throw new IllegalArgumentException("Don't know how to handle <" + mediaType + ">");
    }

    public <T> StreamingParser<T> createStreamingParser(Class<T> type, MediaType mediaType, File file) {
        InputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(file));

            return createStreamingParser(type, mediaType, is);
        } catch (FileNotFoundException e) {
            throw new HandlerException("Failed to find file <" + file.getAbsolutePath() + ">", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> StreamingParser<T> createStreamingParser(Class<T> type, MediaType mediaType, InputStream is) {
        if (mediaType == null) {
            mediaType = MediaType.EXCEL;
        }

        MappedBean<T> mappedBean = (MappedBean<T>) mappedBeans.get(type);

        if (mappedBean == null) {
            throw new IllegalArgumentException("Don't know how to handle <" + type + ">");
        }

        if (MediaType.CSV.equals(mediaType)) {
            return new CsvStreamingParser<T>(mappedBean, is);
        }

        throw new IllegalArgumentException("Don't know how to handle <" + mediaType + ">");
    }

    public <T> StreamingWriter<T> createStreamingWriter(Class<T> type, MediaType mediaType, File file) {
        try {
            return createStreamingWriter(type, mediaType, new BufferedOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            throw new HandlerException("Failed to find file <" + file.getAbsolutePath() + ">", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> StreamingWriter<T> createStreamingWriter(Class<T> type, MediaType mediaType, OutputStream os) {
        if (mediaType == null) {
            mediaType = MediaType.EXCEL;
        }

        MappedBean<T> mappedBean = (MappedBean<T>) mappedBeans.get(type);

        if (mappedBean == null) {
            throw new IllegalArgumentException("Don't know how to handle <" + type + ">");
        }

        if (MediaType.CSV.equals(mediaType)) {
            return new CsvStreamingWriter<T>(mappedBean, os);
        }

        throw new IllegalArgumentException("Don't know how to handle <" + mediaType + ">");
    }
}
