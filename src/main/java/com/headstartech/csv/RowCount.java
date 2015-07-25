package com.headstartech.csv;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by per on 7/24/15.
 */
public class RowCount implements CSVAnalyzer {

    int count;

    @Override
    public void process(List<String> fields) {
        count++;
    }

    @Override
    public void printResult(PrintWriter out) {
        out.println(count);
    }
}
