package org.sweet.csv.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sweet.csv.mapper.MappedBean;
import org.sweet.csv.mapper.MappedProperty;
import org.sweet.csv.utils.FileUtils;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

public class CsvHandler<T> extends AbstractHandler<T> {

    public CsvHandler(MappedBean<T> mappedBean) {
        super(mappedBean);
    }

    @Override
    protected void doParse(InputStream is, List<T> result) throws IOException {
        CSVParse parser = new CSVParser(FileUtils.toBufferedInputStream(is));
        String[] line = parser.getLine();
        MappedBean<T> mappedBean = this.mappedBean;

        if (hasHeader && line != null) {
            mappedBean = mappedBean.withHeaders(line);
            line = parser.getLine();
        }

        while (line != null) {
            if (!isBlank(line)) {
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

                result.add(bean);
            }

            line = parser.getLine();
        }
    }

    public void write(Iterable<T> iterable, OutputStream out) {
        CSVPrint printer = new CSVPrinter(FileUtils.toBufferedOutputStream(out));

        if (hasHeader) {
            printer.println(mappedBean.getHeaders());
        }

        for (T bean : iterable) {
            if (bean != null) {
                for (MappedProperty<T> mappedProperty : mappedBean) {
                    final String value = mappedProperty.asString(bean);

                    printer.print(value);
                }

                printer.println();
            }
        }

        try {
            printer.flush();
        } catch (IOException e) {
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
