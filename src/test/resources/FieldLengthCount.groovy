import com.headstartech.enodo.AbstractCSVProcessor
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class FieldLengthCount extends AbstractCSVProcessor {

    private DescriptiveStatistics stats = new DescriptiveStatistics();

    /**
     * Called for every row in the input file(s).
     *
     * @param fields
     * @return <code>true</code> to continue processing, <code>false</code> to stop
     */
    @Override
    boolean processRow(List<String> fields) {
        for (String field : fields) {
            stats.addValue(field.length());
        }
        return true;
    }

    /**
     * Called when all rows have been read or {@link #processRow(List)} returned <code>false</code>.
     */
    @Override
    void afterLastRow() {
        getOutputWriter().println(String.format("N: %d", stats.getN()));
        getOutputWriter().println(String.format("Min: %.2f", stats.getMin()));
        getOutputWriter().println(String.format("Max: %.2f", stats.getMax()));
        getOutputWriter().println(String.format("Mean: %.2f", stats.getMean()));
    }
}
