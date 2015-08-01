package com.headstartech.enodo;

import com.google.common.io.Files;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.cli.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


@SpringBootApplication
public class Application implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    private static final String OUTPUT_FILE_DEFAULT = "/tmp/enodo-analyzer-report.log";

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .showBanner(false)
                .logStartupInfo(false)
                .sources(Application.class)
                .run(args);
    }

	public void run(String... args) throws IOException, IllegalAccessException, InstantiationException, ParseException {

        Options options = new Options();
        options.addOption("h", false, "Print this message");
        Option outputOption = Option.builder("o")
                .hasArg()
                .required(false)
                .desc("Output file")
                .build();
        options.addOption(outputOption);
        Option scriptOption = Option.builder("s")
                .hasArg()
                .required()
                .desc("groovy script")
                .build();
        options.addOption(scriptOption);
        Option inputOption = Option.builder("i")
                .hasArg()
                .required()
                .desc("input file/directory (filename wildcards accepted)")
                .build();
        options.addOption(inputOption);
        Option separatorOption = Option.builder("c")
                .hasArg()
                .required(false)
                .desc("String to use as separator (default ','")
                .build();
        options.addOption(separatorOption);


        CommandLineParser parser = new ExtendedPosixParser();
        CommandLine cmd = null;
        try {
             cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            printHelp(options);
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }

        File output = new File(cmd.getOptionValue("o", String.format("%s%senodo.out", System.getProperty("java.io.tmpdir"), System.getProperty("file.separator"))));
        File groovyScript = new File(cmd.getOptionValue("s"));
        String inputFilePath = cmd.getOptionValue("i");
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

        List<File> inputFiles = collectFiles(inputFilePath);

        if(inputFiles.isEmpty()) {
            log.warn("No files matching {}", inputFilePath);
            System.exit(0);
        }

        log.info("Output written to {}", output.getAbsoluteFile());
        PrintWriter pw = new PrintWriter(output);
        try {
            processor.setOutputWriter(pw);
            for (File inputFile : inputFiles) {
                processFile(inputFile, Charset.defaultCharset(), new CSVReader(separator, processor));
            }
            processor.afterLastRow();
        } finally {
            pw.flush();
            pw.close();
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
        formatter.printHelp("java -jar enodo-analyzer.jar [options]", options);
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
            String[] matchingFilenames = parent.list(new WildcardFileFilter(f.getName()));
            if(matchingFilenames != null) {
                for (String name : matchingFilenames) {
                    res.add(new File(f.getParentFile(), name));
                }
            }
        }

        Collections.sort(res);
        return res;
    }


}
