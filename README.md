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
      boolean processRow(List<String> fields);
          for (String field : fields) {
              stats.addValue(field.length());
          }
          return true;
      }
  
      @Override
      void afterLastRow() {
          getOutputWriter().println(String.format("N: %d", stats.getN()));
          getOutputWriter().println(String.format("Min: %.2f", stats.getMin()));
          getOutputWriter().println(String.format("Max: %.2f", stats.getMax()));
          getOutputWriter().println(String.format("Mean: %.2f", stats.getMean()));
      }
    }

 
  
Run the utility:

`$ java -jar csv-analyzer-1.0.0.jar -o /tmp/fieldlength.out -s FieldLengthStats.groovy -i example.csv`
  
The result is written to `/tmp/fieldlength.out`:

    N: 9
    Min: 3.00
    Max: 5.00
    Mean: 3.89

## Misc
### Wildcards 
Wildcards are supported to read multiple csv files. 

`$ java -jar csv-analyzer-1.0.0.jar -o /tmp/fieldlength.out -s FieldLengthStats.groovy -i "*.csv"`

**Note the `"` around the input file argument!**
