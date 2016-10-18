package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.File;

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
    private final static String DB_NAME = "VSC_TEST";
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

    @After
    public void tearDown() {
        vcs.dropVCSInfo();
    }

}