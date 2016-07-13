# enodo
Utility tool to perform ad-hoc analysis on csv files when `grep`, `sed`, `awk` and `cut` aren't enough.

## Prerequisites
* Java 6 (or later)

## Build
* Download ZIP or `$ git clone https://github.com/headstar/enodo.git` 
* `$ cd enodo`
* `$ ./gradlew build`

Resulting jar is found in `./build/libs`.

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

`$ java -jar enodo-1.0.0.jar -i /tmp/example.csv -s src/test/resources/FieldLengthCount.groovy`
  
The result written to `stdout` is:

    N: 9
    Min: 3.00
    Max: 5.00
    Mean: 3.89

## Misc

### Input
Input will be read from `stdin` if not specified. 

The example above can been run as:

`$ cat /tmp/example.csv | java -jar build/libs/enodo-1.0.0.jar -s src/test/resources/FieldLengthCount.groovy`

#### Regular expression filter
Regular expressions are supported when specifying the input.

Example:

If an example produces files with the pattern "myapp.yyyymmdd_hhmmss", the pattern below could be used to process all files written between 23 p.m and midnight in October 2015.

`$ java -jar enodo-1.0.0.jar -s FieldLengthStats.groovy -i "myapp.201510.._(23)+.*"`

**Note the `"` around the input file argument!**

### Output
Output will be written to `stdout` if not specified.

### Logging
The application log location is `/tmp/enodo.log`.

### Useful libraries available at runtime

#### [Guava](https://github.com/google/guava)
Useful if you want to do further splitting, joining etc.

#### [Joda Time](http://www.joda.org/joda-time/)
For easier date & time manipulation.

#### [The Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/)
The [statistics package](http://commons.apache.org/proper/commons-math/userguide/stat.html#a1.2_Descriptive_statistics) contains a number of useful functions:
* arithmetic and geometric means
* variance and standard deviation
* minimum, maximum, median, and percentiles
* ...and more
