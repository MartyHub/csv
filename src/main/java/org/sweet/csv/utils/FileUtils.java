package org.sweet.csv.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private FileUtils() {
    }

    public static InputStream toBufferedInputStream(InputStream in) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        return in;
    }

    public static OutputStream toBufferedOutputStream(OutputStream out) {
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        return out;
    }
}
