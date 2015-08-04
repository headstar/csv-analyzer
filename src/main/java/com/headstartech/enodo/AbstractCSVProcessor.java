package com.headstartech.enodo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

/**
 * Convenience base class for a {@link CSVProcessor}.
 */
public abstract class AbstractCSVProcessor implements CSVProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private PrintWriter out;

    @Override
    public void setOutputWriter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void afterLastRow() {
        // do nothing
    }

    protected PrintWriter getOutputWriter() {
        return out;
    }

    protected Logger getLogger() { return logger; }
}
