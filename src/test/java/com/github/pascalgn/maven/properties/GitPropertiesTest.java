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
        assertNotEmpty(properties.get("git.commit.color"));
        assertNotEmpty(properties.get("git.build.datetime.simple"));
        assertNotEmpty(properties.get("git.tag.last"));
        assertNotEmpty(properties.get("git.dir.git"));
        assertNotEmpty(properties.get("git.dir.worktree"));
    }

    private static void assertNotEmpty(String str) {
        assertNotNull(str);
        assertFalse(str.isEmpty());
        assertFalse(str.trim().isEmpty());
    }
}
