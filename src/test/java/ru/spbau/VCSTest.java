package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.entity.Branch;
import ru.spbau.utility.GlobalLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * VCSTest is a class for testing VCS functionality
 */
public class VCSTest {
    private final static String TEST_DIR = "src/test/resources/";
    private final static String TEST_FILE_MAIN ="src/test/resources/main.txt";
    private final static String TEST_FILE_A = "src/test/resources/shared/a.txt";
    private final static String TEST_FILE_MAIN_TEXT = "hello world";
    private VCS vcs;

    @Before
    public void setUp() {
        vcs = new VCS(Paths.get(TEST_DIR));
        vcs.makeInit();
    }

    @Test
    public void testMakeInit() throws Exception {
        // init was already done
        final Branch branch = vcs.getBranch();
        assertTrue(branch.isActive());
        assertEquals("master", branch.getName());
    }

    @Test
    public void testGetStatus() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));

        String status = vcs.getStatus();
        assertTrue(status.contains(TEST_FILE_MAIN));
        assertTrue(status.contains("new file"));

        vcs.makeCommit("Added file!");
        status = vcs.getStatus();
        assertTrue(status.isEmpty());

        vcs.makeRm(Arrays.asList(TEST_FILE_MAIN));

        status = vcs.getStatus();
        assertTrue(status.contains(TEST_FILE_MAIN));
        assertTrue(status.contains("deleted"));

        recoverMainFile();
    }

    @Test
    public void testGetLog() {
        final String initMsg = "Initialization commit";
        final String addMsg = "Added file!";

        String log = vcs.getLog();
        assertTrue(log.contains(initMsg));

        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        vcs.makeCommit(addMsg);

        log = vcs.getLog();
        assertTrue(log.contains(initMsg));
        assertTrue(log.contains(addMsg));
    }

    @Test
    public void testGetBranches() {
        final String masterName = "master";
        final String childName = "child";

        String branchesString = vcs.getBranches();
        assertTrue(branchesString.contains(masterName));

        vcs.makeBranch(childName);
        branchesString = vcs.getBranches();
        assertTrue(branchesString.contains(masterName));
        assertTrue(branchesString.contains(childName));
    }

    @Test
    public void testMakeAdd() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN, TEST_FILE_A));

        assertTrue(vcs.getStatus().contains(TEST_FILE_A));
        assertTrue(vcs.getStatus().contains(TEST_FILE_MAIN));
    }

    @Test
    public void testMakeCommit() {
        final String addMsg = "Added file!";

        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        String status = vcs.getStatus();
        assertTrue(status.contains(TEST_FILE_MAIN));
        assertTrue(status.contains("new file"));

        vcs.makeCommit(addMsg);
        status = vcs.getStatus();
        assertTrue(status.isEmpty());

        String log = vcs.getLog();
        assertTrue(log.contains(addMsg));
    }

    private final void recoverMainFile() {
        java.io.File mainFile = new java.io.File(TEST_FILE_MAIN);
        try {
            FileUtils.write(mainFile, TEST_FILE_MAIN_TEXT, StandardCharsets.UTF_8);
        } catch (IOException e) {
            GlobalLogger.log("Couldn't create file " + TEST_FILE_MAIN);
        }
    }

    @After
    public void tearDown() {
        vcs.dropVCSInfo();
    }
}