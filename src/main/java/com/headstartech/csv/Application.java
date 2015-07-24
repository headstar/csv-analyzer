package com.headstartech.csv;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ListIterator;


@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

	public void run(String... args) {

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
        formatter.printHelp("java -jar cdr-analyzer.jar", options);
    }

    private void processFile(File f, Charset charset) throws IOException {
        Files.readLines(f, charset, new CDRProcessor());
    }

    private static class CDRProcessor implements LineProcessor<Statistics> {

        private final Splitter splitter;

        public CDRProcessor() {
            splitter = Splitter.on(',');
        }

        @Override
        public boolean processLine(String line) throws IOException {
            List<String> fields = splitter.splitToList(line);

            return true;
        }

        @Override
        public Statistics getResult() {
            return null;
        }
    }


    private static class Statistics {

    }

}
