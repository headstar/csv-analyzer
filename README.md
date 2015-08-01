# enodo
Utility tool to perform ad-hoc analysis on csv files when `grep`, `sed`, `awk` and `cut` aren't enough.

## Prerequisites
* Java 6 (or later)

## Build
* `$ git clone https://github.com/headstar/enodo.git`
* `$ cd enodo`
* `$ ./gradlew build`

## Example

Example csv file:

    The,quick,brown
    fox,jumps
    over,the,lazy,dog


To generate field length statistics, the following groovy script will do the trick:

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

 
  
Run the utility:

`$ java -jar enodo-1.0.0.jar -o /tmp/fieldlength.out -s FieldLengthStats.groovy -i example.csv`
  
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

### Statistics
[The Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/) is available on the classpath. The [statistics package](http://commons.apache.org/proper/commons-math/userguide/stat.html#a1.2_Descriptive_statistics) contains a number of useful functions:
* arithmetic and geometric means
* variance and standard deviation
* sum, product, log sum, sum of squared values
* minimum, maximum, median, and percentiles
* skewness and kurtosis
* first, second, third and fourth moments

