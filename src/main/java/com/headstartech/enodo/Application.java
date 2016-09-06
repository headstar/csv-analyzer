package com.headstartech.enodo;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;


@SpringBootApplication
public class Application implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .showBanner(false)
                .logStartupInfo(false)
                .sources(Application.class)
                .run(args);
    }

	public void run(String... args) throws IOException, IllegalAccessException, InstantiationException, ParseException {

        Options options1 = new Options();
        options1.addOption("h", "help", false, "Print this message");

        Options options2 = new Options();
        options2.addOption("h", "help", false, "Print this message");
        Option outputOption = Option.builder("o")
                .hasArg()
                .required(false)
                .desc("Output file (default stdout)")
                .build();
        options2.addOption(outputOption);
        Option scriptOption = Option.builder("s")
                .hasArg()
                .required()
                .desc("groovy script")
                .build();
        options2.addOption(scriptOption);
        Option inputOption = Option.builder("i")
                .hasArg()
                .required(false)
                .desc("input file/directory (default stdin)")
                .build();
        options2.addOption(inputOption);
        Option separatorOption = Option.builder("c")
                .hasArg()
                .required(false)
                .desc("String to use as separator (default ',')")
                .build();
        options2.addOption(separatorOption);

        CommandLineParser parser = new ExtendedPosixParser();
        CommandLine helpCommand = null;
        try {
            helpCommand = parser.parse(options1, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            printHelp(options2);
            System.exit(1);
        }

        if(helpCommand.getOptions().length > 0) {
            printHelp(options2);
            System.exit(0);
        }

        CommandLine cmd = null;
        try {
             cmd = parser.parse(options2, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            printHelp(options2);
            System.exit(1);
        }

        File groovyScript = new File(cmd.getOptionValue("s"));
        String separator = cmd.getOptionValue("c", ",");

        log.info("Loading Groovy script {}", groovyScript.getAbsolutePath());

        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyScript);
        Object groovyObject = clazz.newInstance();
        if(!(groovyObject instanceof CSVProcessor)) {
            log.info("Groovy class must implement {}", CSVProcessor.class.getSimpleName());
            return;
        }
        CSVProcessor processor = (CSVProcessor) groovyObject;


        PrintWriter pw = null;
        try {
            if(cmd.hasOption("o")) {
                File output = new File(cmd.getOptionValue("o"));
                log.info("Output written to {}", output.getAbsoluteFile());
                pw = new PrintWriter(output);
            } else {
                log.info("Output written to stdout");
                pw =  new PrintWriter(System.out);
            }
            try {
                processor.setOutputWriter(pw);
                processor.beforeFirstRow();

                Splitter splitter = null;
                if(!separator.isEmpty()) {
                    splitter = Splitter.on(separator);
                }
                CSVReader csvReader = new CSVReader(splitter, processor);

                String inputFilePath = cmd.getOptionValue("i");
                if(inputFilePath != null) {
                    List<File> inputFiles = collectFiles(inputFilePath);
                    if (inputFiles.isEmpty()) {
                        log.warn("No files matching {}", inputFilePath);
                    }
                    for (File inputFile : inputFiles) {
                        processFile(inputFile, Charset.defaultCharset(), csvReader);

                    }
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String line;
                    while((line = br.readLine()) != null) {
                        csvReader.processLine(line);
                    }
                }
                processor.afterLastRow();
            } catch (RuntimeException e) {
                log.error("Exception caught when processing, aborting...", e);
            }

        } finally {
            if(pw != null) {
                pw.flush();
                pw.close();
            }
        }
    }

    /**
     * Posix parser ignoring unknown options to let Spring boot handle them.
     *
     */
    private static class ExtendedPosixParser extends PosixParser {

        @Override
        protected void processOption(final String arg, final ListIterator iter) throws ParseException {
            boolean hasOption = getOptions().hasOption(arg);

            if (hasOption) {
                super.processOption(arg, iter);
            }
        }

    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
        formatter.printHelp("java -jar enodo-1.0.0.jar [options]", options);
    }

    private void processFile(File f, Charset charset, CSVReader processor) throws IOException {
        log.info("Processing {}", f.getAbsolutePath());
        Files.readLines(f, charset, processor);
    }

    private List<File> collectFiles(String path) {
        File f = new File(path);

        List<File> res = new ArrayList<File>();
        if(f.isDirectory()) {
            String[] files = f.list();
            for(String name : files) {
                res.add(new File(f, name));
            }
        } else {
            File parent = f.getParentFile();
            if(parent == null) {
                parent = new File(System.getProperty("user.dir"));
            }
            String[] matchingFilenames = parent.list(new RegexFileFilter(f.getName()));
            if(matchingFilenames != null) {
                for (String name : matchingFilenames) {
                    res.add(new File(f.getParentFile(), name));
                }
            }
        }

        Collections.sort(res);
        return res;
    }

    private static class RegexFileFilter implements FilenameFilter {

        private final Pattern pattern;

        public RegexFileFilter(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public boolean accept(File dir, String name) {
            return pattern.matcher(name).matches();
        }
    }

}
