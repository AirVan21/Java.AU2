package ru.spbau;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.StringDatatypeValidator;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.entity.Branch;
import ru.spbau.utility.FileManager;
import ru.spbau.utility.GlobalLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * VCSTest is a class for testing VCS functionality
 */
public class VCSTest {
    /**
     * Root directory
     */
    private final static String TEST_DIR = "src/test/resources/";
    /**
     * Files which are available for testing
     */
    private final static String TEST_FILE_MAIN ="src/test/resources/main.txt";
    private final static String TEST_FILE_A = "src/test/resources/shared/a.txt";
    private final static String TEST_FILE_B = "src/test/resources/shared/b.txt";
    private final static String TEST_FILE_C = "src/test/resources/shared/c.txt";
    /**
     * Content of available files
     */
    private final static String TEST_FILE_MAIN_TEXT = "hello world";
    private final static String TEST_FILE_A_TEXT = "class a";
    /**
     * Main testing object
     */
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
        String status = vcs.getStatus();

        assertTrue(status.contains(TEST_FILE_A));
        assertTrue(status.contains(TEST_FILE_MAIN));
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

    @Test
    public void testMakeClean() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_A, TEST_FILE_B, TEST_FILE_C));

        // TEST_FILE_MAIN is in dir but not tracked (will be deleted)
        Optional<java.io.File> mainFile = FileManager.getFile(TEST_FILE_MAIN);
        assertTrue(mainFile.isPresent());

        vcs.makeClean();
        mainFile = FileManager.getFile(TEST_FILE_MAIN);
        assertFalse(mainFile.isPresent());

        recoverMainFile();
    }

    @Test
    public void testMakeRm() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_A, TEST_FILE_MAIN));

        Optional<java.io.File> aFile = FileManager.getFile(TEST_FILE_A);
        Optional<java.io.File> mainFile = FileManager.getFile(TEST_FILE_MAIN);
        assertTrue(aFile.isPresent());
        assertTrue(mainFile.isPresent());

        vcs.makeCommit("Added two files!");
        // Check that changes are applied
        assertTrue(vcs.getStatus().isEmpty());

        // Removes files from track
        vcs.makeRm(Arrays.asList(TEST_FILE_A, TEST_FILE_MAIN));
        final String status = vcs.getStatus();

        assertFalse(status.isEmpty());
        assertTrue(status.contains("deleted: " + TEST_FILE_MAIN));
        assertTrue(status.contains("deleted: " + TEST_FILE_A));

        aFile = FileManager.getFile(TEST_FILE_A);
        mainFile = FileManager.getFile(TEST_FILE_MAIN);
        assertFalse(aFile.isPresent());
        assertFalse(mainFile.isPresent());

        recoverMainFile();
        recoverAFile();
    }

    @Test
    public void testMakeReset() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        vcs.makeCommit("Added main file!");

        editMainFile();
        String status = vcs.getStatus();
        assertTrue(status.contains("modified: " + TEST_FILE_MAIN));

        vcs.makeReset(Arrays.asList(TEST_FILE_MAIN));
        status = vcs.getStatus();
        // Check that changes are unrolled
        assertTrue(status.isEmpty());

        Optional<String> fileText = FileManager.readFile(TEST_FILE_MAIN);
        assertTrue(fileText.isPresent());
        assertEquals(TEST_FILE_MAIN_TEXT, fileText.get());
    }

    @Test
    public void testMakeBranch() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        vcs.makeCommit("Added main file!");

        final String branchName = "other";
        vcs.makeBranch(branchName);

        final String branchNames = vcs.getBranches();
        assertTrue(branchNames.contains(branchName));

        final String branchLog = vcs.getLog();
        assertTrue(branchLog.contains("Added main file!"));
    }

    @Test
    public void testMakeCheckout() {
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        vcs.makeCommit("Added main file!");

        final String branchName = "other";
        vcs.makeBranch(branchName);

        // change main.txt in a branch
        editMainFile();
        final Optional<String> changedText = FileManager.readFile(TEST_FILE_MAIN);
        vcs.makeAdd(Arrays.asList(TEST_FILE_MAIN));
        vcs.makeCommit("Changed main.txt");
        assertNotEquals(TEST_FILE_MAIN_TEXT, changedText.get());

        vcs.makeCheckout("master");
        final Optional<String> sourceText = FileManager.readFile(TEST_FILE_MAIN);
        assertNotEquals(changedText.get(), sourceText.get());
        assertEquals(TEST_FILE_MAIN_TEXT, sourceText.get());
    }

    private final void recoverMainFile() {
        java.io.File mainFile = new java.io.File(TEST_FILE_MAIN);
        try {
            FileUtils.write(mainFile, TEST_FILE_MAIN_TEXT, StandardCharsets.UTF_8);
        } catch (IOException e) {
            GlobalLogger.log("Couldn't create file " + TEST_FILE_MAIN);
        }
    }

    private final void recoverAFile() {
        java.io.File mainFile = new java.io.File(TEST_FILE_A);
        try {
            FileUtils.write(mainFile, TEST_FILE_A_TEXT, StandardCharsets.UTF_8);
        } catch (IOException e) {
            GlobalLogger.log("Couldn't create file " + TEST_FILE_A);
        }
    }

    private final void editMainFile() {
        java.io.File mainFile = new java.io.File(TEST_FILE_MAIN);
        try {
            FileUtils.write(mainFile, TEST_FILE_A_TEXT, StandardCharsets.UTF_8);
        } catch (IOException e) {
            GlobalLogger.log("Couldn't create file " + TEST_FILE_MAIN);
        }
    }

    @After
    public void tearDown() {
        vcs.dropVCSInfo();
    }
}