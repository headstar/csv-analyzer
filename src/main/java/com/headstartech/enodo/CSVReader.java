package com.headstartech.enodo;

import com.google.common.base.Splitter;
import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
class CSVReader implements LineProcessor<Object> {

    private final Splitter splitter;
    private final CSVProcessor processor;

    public CSVReader(CSVProcessor processor) {
        this.splitter = Splitter.on(processor.separator());
        this.processor = processor;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        List<String> fields = splitter.splitToList(line);
        return processor.processRow(fields);
    }

    @Override
    public Object getResult() {
        return null;
    }
}
