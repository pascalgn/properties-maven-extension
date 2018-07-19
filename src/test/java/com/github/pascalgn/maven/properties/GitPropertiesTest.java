package com.github.pascalgn.maven.properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitPropertiesTest {
    private static Logger logger = new ConsoleLogger(Logger.LEVEL_DEBUG, "");
    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private static File gitRepoFilePath;
    private static File cloneFilePath;

    @ClassRule
    public static TemporaryFolder workFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        gitRepoFilePath = workFolder.newFolder("GITREPO");
        logger.debug("Creating GIT repo at " + gitRepoFilePath.toString());
        Command.execute("git init --bare", gitRepoFilePath);
        Command.execute("git symbolic-ref HEAD refs/heads/develop", gitRepoFilePath);

        cloneFilePath = workFolder.newFolder("clone");
        logger.debug("Cloning GIT repo to " + cloneFilePath.toString());
        Command.execute("git clone " + gitRepoFilePath.getPath() + " .", cloneFilePath);
        Command.execute("echo \"First commit to create HEAD\" > abc.txt", cloneFilePath);
        Command.execute("git add abc.txt", cloneFilePath);
        Command.execute("git commit -m \"new file\"", cloneFilePath);
        Command.execute("git push", cloneFilePath);
    }

    @Test
    public void test01_NoTag_NoChanges() {
        logger.debug("Test1");
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("", properties.get("git.describe"));
        assertRegexp("", properties.get("git.describe.long"));
    }

    @Test
    public void test02_NoTag_ModifiedFile_NoAdd_NoCommit() throws Exception {
        logger.debug("Test2");
        Command.execute("echo \"\nNew line 1 added.\" >> abc.txt", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("", properties.get("git.describe"));
        assertRegexp("", properties.get("git.describe.long"));
    }

    @Test
    public void test03_NoTag_ModifiedFile_Add_NoCommit() throws Exception {
        logger.debug("Test3");
        Command.execute("git add abc.txt", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("", properties.get("git.describe"));
        assertRegexp("", properties.get("git.describe.long"));
    }

    @Test
    public void test04_NoTag_ModifiedFile_Add_Commit() throws Exception {
        logger.debug("Test4");
        Command.execute("git commit -m \"New line added\"", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("", properties.get("git.describe"));
        assertRegexp("", properties.get("git.describe.long"));
    }

    @Test
    public void test05_NoTag_Pushed() throws Exception {
        logger.debug("Test5");
        Command.execute("git push origin master", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("", properties.get("git.describe"));
        assertRegexp("", properties.get("git.describe.long"));
    }

    @Test
    public void test06_Tag_NotPushed_NoFileChanges() throws Exception {
        logger.debug("Test6");
        Command.execute("git tag -a 1.2.3.4 -m \"Tag added\"", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4", properties.get("git.describe"));
        assertRegexp("1.2.3.4-0-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test07_Tag_Pushed_NoFileChanges() throws Exception {
        logger.debug("Test7");
        Command.execute("git push --tags", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4", properties.get("git.describe"));
        assertRegexp("1.2.3.4-0-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test08_Tag_Pushed_FileChange_NoAdd_NoCommit() throws Exception {
        logger.debug("Test8");
        Command.execute("echo \"\nNew line 2 added.\" >> abc.txt", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4", properties.get("git.describe"));
        assertRegexp("1.2.3.4-0-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test09_Tag_Pushed_FileChange_Add_NoCommit() throws Exception {
        logger.debug("Test9");
        Command.execute("git add abc.txt", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4", properties.get("git.describe"));
        assertRegexp("1.2.3.4-0-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test10_Tag_Pushed_FileChange_Add_Commit() throws Exception {
        logger.debug("Test10");
        Command.execute("git commit -m \"Line added\"", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4-1-[a-g0-9]{8}", properties.get("git.describe"));
        assertRegexp("1.2.3.4-1-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test11_Tag_Pushed_File_Pushed() throws Exception {
        logger.debug("Test11");
        Command.execute("git push origin master", cloneFilePath);
        Map<String, String> properties = new GitPropertiesExtend(logger, cloneFilePath).getProperties();
        assertRegexp("1.2.3.4-1-[a-g0-9]{8}", properties.get("git.describe"));
        assertRegexp("1.2.3.4-1-[a-g0-9]{8}", properties.get("git.describe.long"));
    }

    @Test
    public void test99_PropertiesReturned() {
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
        assertNotEmpty(properties.get("git.build.datetime.simple"));
        assertNotEmpty(properties.get("git.describe"));
        assertNotEmpty(properties.get("git.describe.long"));
    }

    private static void assertNotEmpty(String str) {
        assertNotNull(str);
        assertFalse(str.isEmpty());
        assertFalse(str.trim().isEmpty());
    }

    private static void assertRegexp(String expected, String actual) {
        logger.debug("assertDescribe: exp=" + expected + ",act=" + actual);
        if (expected == null) {
            assertNull(actual);
        } else {
            if (actual == null) {
                fail("Regexp match inconsistency, expected is '" + expected + "' but actual is null");
            } else {
                assertTrue("Regexp mismatch for expected='" + expected + "' and actual='" + actual + "'",
                        actual.matches(expected));
            }
        }
    }

    /**
     * Extend GitProperties class
     */
    class GitPropertiesExtend extends GitProperties {
        private File gitFolder;

        public GitPropertiesExtend(Logger logger, File gitFolder) {
            super(logger);
            this.gitFolder = gitFolder;
        }

        @Override
        protected Repository getRepository() throws IOException {
            Repository repository = new FileRepositoryBuilder().setWorkTree(gitFolder).readEnvironment()
                    .setMustExist(true).build();
            return repository;
        }
    }

    /**
     * Command execute helper
     */
    private static class Command {
        public static int execute(String commandStr, File folder) throws IOException, InterruptedException {
            logger.debug("Executing command '" + commandStr.replace("\n", "\\n") + "'");
            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command("cmd.exe", "/c", commandStr);
            } else {
                builder.command("sh", "-c", commandStr);
            }
            builder.directory(folder);
            Process process = builder.start();
            Executors.newSingleThreadExecutor().submit(new StreamToLogger(process.getInputStream()));
            int rc = process.waitFor();
            logger.debug("Command returned=" + rc);
            return rc;
        }
    }

    /**
     * Directs input stream to log
     */
    private static class StreamToLogger implements Runnable {
        private InputStream inputStream;

        public StreamToLogger(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(new Consumer<String>() {
                @Override
                public void accept(String t) {
                    logger.debug("> " + t.toString());
                }
            });
        }
    }
}