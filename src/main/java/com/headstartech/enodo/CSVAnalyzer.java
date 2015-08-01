package com.headstartech.enodo;

import java.io.PrintWriter;
import java.util.List;

/**
 * Interface to be implemented in a Groovy script.
 */
public interface CSVAnalyzer {

    /**
     * Sets the writer to use when writing the output result. The calling application will flush and close the writer.
     * @param out
     */
    void setOutputWriter(PrintWriter out);

    /**
     * Called for every row in the input enodo files.
     *
     * @param fields
     * @return <code>true</code> to continue processing, <code>false</code> to stop
     */
    boolean processRow(List<String> fields);

    /**
     * Called when all rows have been read or {@link #processRow(List)} returned <code>false</code>.
     */
    void afterLastRow();
}
