package com.headstartech.csv;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
interface CSVAnalyzer {

    void process(List<String> fields);

    void printResult(PrintWriter out);
}
