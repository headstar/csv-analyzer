package com.headstartech.csv;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
public interface CSVAnalyzer {

    void setOutputWriter(PrintWriter out);

    void process(List<String> fields);

    void lastFileProcessed();
}
