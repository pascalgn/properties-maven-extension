package com.github.pascalgn.maven.properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Test;

public class GitPropertiesTest {
    @Test
    public void testPropertiesReturned() {
        Logger logger = new ConsoleLogger();
        Map<String, String> properties = new GitProperties(logger).getProperties();
        assertNotEmpty(properties.get("git.branch"));
        assertNotEmpty(properties.get("git.commit.id"));
        assertNotEmpty(properties.get("git.commit.id.abbrev"));
        assertNotEmpty(properties.get("git.count"));
        assertNotEmpty(properties.get("git.commit.color.value"));
        assertNotEmpty(properties.get("git.commit.color.name"));
        assertNotEmpty(properties.get("git.commit.color.lightness"));
        assertNotEmpty(properties.get("git.commit.color.foreground"));
        assertNotEmpty(properties.get("git.build.time"));

    }

    private static void assertNotEmpty(String str) {
        assertNotNull(str);
        assertFalse(str.isEmpty());
        assertFalse(str.trim().isEmpty());
    }
}
