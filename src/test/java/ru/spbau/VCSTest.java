package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.File;
import ru.spbau.utility.FileManager;
import ru.spbau.utility.GlobalLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * VCSTest is a class for testing VCS functionality
 */
public class VCSTest {
    private final static String TEST_DIR = "src/test/resources/";
    private final static String TEST_FILE_MAIN ="src/test/resources/main.txt";
    private final static String TEST_FILE_A = "src/test/resources/shared/a.txt";
    private final static String TEST_FILE_MAIN_TEXT = "hello world";
    private final static VCS vcs = new VCS(Paths.get(TEST_DIR));

    @Before
    public void setUp() {
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

    }

    @Test
    public void testGetBranches()  {

    }

    @Test
    public void testMakeAdd()  {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN, TEST_FILE_A));
        assertTrue(vcs.getStatus().contains(TEST_FILE_A));
        assertTrue(vcs.getStatus().contains(TEST_FILE_MAIN));
    }

    @Test
    public void testMakeCommit()  {

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