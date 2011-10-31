package org.sweet.csv.handler.factory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.sweet.csv.handler.Handler;
import org.sweet.csv.handler.StreamingParser;
import org.sweet.csv.handler.StreamingWriter;
import org.sweet.csv.utils.MediaType;


public interface HandlerFactory {

    <T> Handler<T> createHandler(Class<T> type, MediaType mediaType);

    <T> StreamingParser<T> createStreamingParser(Class<T> type, MediaType mediaType, File file);

    <T> StreamingParser<T> createStreamingParser(Class<T> type, MediaType mediaType, InputStream is);

    <T> StreamingWriter<T> createStreamingWriter(Class<T> type, MediaType mediaType, File file);

    <T> StreamingWriter<T> createStreamingWriter(Class<T> type, MediaType mediaType, OutputStream os);
}
