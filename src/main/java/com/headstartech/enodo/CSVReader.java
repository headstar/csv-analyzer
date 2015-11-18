package com.headstartech.enodo;

import com.google.common.base.Splitter;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
class CSVReader implements LineProcessor<Object> {

    private final Splitter splitter;
    private final CSVProcessor processor;

    public CSVReader(Splitter splitter, CSVProcessor processor) {
        this.splitter = splitter;
        this.processor = processor;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        List<String> fields = splitter.splitToList(line);
        try {
            return processor.processRow(fields);
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getResult() {
        return null;
    }
}
