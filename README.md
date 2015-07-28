# CSV analyzer
Utility tool to perform ad-hoc analysis on csv files when `grep`, `sed`, `awk` and `cut` aren't enough.

## Prerequisites
* Java 6 (or later)

## Example

Example csv file:

    The,quick,brown
    fox,jumps
    over,the,lazy,dog


To generate field length statistics, the following groovy script will do the trick:

    import com.headstartech.csv.AbstractCSVAnalyzer
    import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
    
    public class FieldLengthCount extends AbstractCSVAnalyzer {
  
      private DescriptiveStatistics stats = new DescriptiveStatistics();
  
      @Override
      void process(List<String> fields) {
          for (String field : fields) {
              stats.addValue(field.length());
          }
      }
  
      @Override
      void lastFileProcessed() {
          getOutputWriter().println(String.format("N: %d", stats.getN()));
          getOutputWriter().println(String.format("Min: %.2f", stats.getMin()));
          getOutputWriter().println(String.format("Max: %.2f", stats.getMax()));
          getOutputWriter().println(String.format("Mean: %.2f", stats.getMean()));
  
          for (double d = 10; d < 100; d += 10) {
              getOutputWriter().println(String.format("Percentile %.0f: %.2f", d, stats.getPercentile(d)));
          }
      }
    }

 
  
Run the utility:

`$ java -jar csv-analyzer-1.0.0.jar -o /tmp/fieldlength.out -s FieldLengthCount.groovy -i example.csv`
  
The result is written to `/tmp/fieldlength.out`:

    N: 9
    Min: 3.00
    Max: 5.00
    Mean: 3.89
    Percentile 10: 3.00
    Percentile 20: 3.00
    Percentile 30: 3.00
    Percentile 40: 3.00
    Percentile 50: 4.00
    Percentile 60: 4.00
    Percentile 70: 5.00
    Percentile 80: 5.00
    Percentile 90: 5.00
