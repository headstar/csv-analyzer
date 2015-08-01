package com.headstartech.enodo;

import groovy.lang.GroovyClassLoader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by per on 8/1/15.
 */
public class SampleTest {

    @Test
    public void testLoadingSampleGroovyScript() throws IOException, IllegalAccessException, InstantiationException {

        File groovyScript = new File("src/test/resources/FieldLengthCount.groovy");

        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyScript);
        Object groovyObject = clazz.newInstance();
        if(!(groovyObject instanceof CSVProcessor)) {
            fail("Should be a CSVProcessor");
        }

    }
}
