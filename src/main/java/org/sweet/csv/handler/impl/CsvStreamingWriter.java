package org.sweet.csv.handler.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.sweet.csv.handler.StreamingWriter;
import org.sweet.csv.mapper.MappedBean;
import org.sweet.csv.mapper.MappedProperty;
import org.sweet.csv.utils.FileUtils;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

public class CsvStreamingWriter<T> implements StreamingWriter<T> {

    private final MappedBean<T> mappedBean;

    private final CSVPrint printer;

    private boolean firstLineHeader = true;

    private boolean firstItem = true;

    public CsvStreamingWriter(MappedBean<T> mappedBean, OutputStream os) {
        this.mappedBean = mappedBean;
        this.printer = new CSVPrinter(FileUtils.toBufferedOutputStream(os));
    }

    public boolean isFirstLineHeader() {
        return firstLineHeader;
    }

    public void setFirstLineHeader(final boolean firstLineHeader) {
        this.firstLineHeader = firstLineHeader;
    }

    public void write(T... items) {
        if (firstItem) {
            if (firstLineHeader) {
                printer.println(mappedBean.getHeaders());
            }

            firstItem = false;
        }

        for (T bean : items) {
            if (bean != null) {
                for (MappedProperty<T> mappedProperty : mappedBean) {
                    final String value = mappedProperty.asString(bean);

                    printer.print(value);
                }

                printer.println();
            }
        }
    }

    public void close() {
        if (printer != null) {
            try {
                printer.close();
            } catch (IOException e) {
            }
        }
    }
}
