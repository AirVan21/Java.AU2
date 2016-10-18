package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * VCSTest is a class for testing VCS functionality
 */
public class VCSTest {
    private final static String TEST_DIR = "src/test/resources/";
    private final static String DB_NAME = "VSC_TEST";
    private final static VCS vcs = new VCS(Paths.get(TEST_DIR));
    private DataBase database;

    @Before
    public void setUp() {
        database = new DataBase(DB_NAME);
    }

    @Test
    public void makeInit() throws Exception {
        vcs.makeInit();
        final Branch branch = vcs.getBranch();

        assertTrue(branch.isActive());
        assertEquals("master", branch.getName());
    }

    @Test
    public void getStatus() throws Exception {

    }

    @Test
    public void getLog() throws Exception {

    }

    @Test
    public void getBranches() throws Exception {

    }

    @After
    public void tearDown() {
        vcs.dropVCSInfo();
    }

}