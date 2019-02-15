package com.sipgate.siptest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void configurationFileGotAPassword() {
        Config config = Main.getConfig("/tmp/test.toml", false);

        assertEquals("s1pGAt3r0cK5", config.password);
    }

    @Before
    public void setUp() throws Exception {
        PrintWriter writer = new PrintWriter("/tmp/test.toml", "UTF-8");
        writer.println("password = 's1pGAt3r0cK5'");
        writer.close();
    }

    @After
    public void tearDown() throws Exception {
        File tempTestToml = new File("/tmp/test.toml");
        tempTestToml.delete();
    }
}