package com.headstartech.enodo;

import java.io.PrintWriter;

/**
 * Convenience base class for a {@link CSVProcessor}.
 */
public abstract class AbstractCSVProcessor implements CSVProcessor {

    private PrintWriter out;

    @Override
    public void setOutputWriter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void afterLastRow() {
        // do nothing
    }

    @Override
    public Character separator() {
        return ',';
    }

    protected PrintWriter getOutputWriter() {
        return out;
    }
}
