import com.headstartech.csv.CSVAnalyzer
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class PCRFSessionDuration implements CSVAnalyzer {

    private DescriptiveStatistics stats = new DescriptiveStatistics();

    @Override
    void process(List<String> fields) {
        String event = fields.get(2);
        if("2".equals(event)) {
            stats.addValue(Long.valueOf(fields.get(34)));
        }
    }

    @Override
    void printResult(PrintWriter out) {
        out.println(String.format("N: %d", stats.getN()));
        out.println(String.format("Min: %f", stats.getMin()));
        out.println(String.format("Max: %f", stats.getMax()));
        out.println(String.format("Mean: %f", stats.getMean()));

        for (double d = 30; d < 100; d += 5) {
            out.println(String.format("Percentile %f: %f", d, stats.getPercentile(d)));
        }
    }
}