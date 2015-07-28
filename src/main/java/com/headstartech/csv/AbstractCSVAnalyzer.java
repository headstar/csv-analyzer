package com.headstartech.csv;

import java.io.PrintWriter;

/**
 * Convenience base class for a {@link CSVAnalyzer}.
 */
public abstract class AbstractCSVAnalyzer implements CSVAnalyzer {

    private PrintWriter out;

    @Override
    public void setOutputWriter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void lastFileProcessed() {
        // do nothing
    }

    protected PrintWriter getOutputWriter() {
        return out;
    }
}
