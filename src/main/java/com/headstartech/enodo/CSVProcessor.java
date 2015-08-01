package com.headstartech.enodo;

import com.google.common.base.Splitter;
import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
class CSVProcessor implements LineProcessor<Object> {

    private final Splitter splitter;
    private final CSVAnalyzer analyzer;

    public CSVProcessor(char separator, CSVAnalyzer analyzer) {
        this.splitter = Splitter.on(separator);
        this.analyzer = analyzer;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        List<String> fields = splitter.splitToList(line);
        analyzer.processRow(fields);
        return true;
    }

    @Override
    public Object getResult() {
        return null;
    }
}
