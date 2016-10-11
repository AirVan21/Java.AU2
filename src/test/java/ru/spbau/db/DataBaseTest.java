package ru.spbau.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.entity.Branch;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * k
 */
public class DataBaseTest {
    private final static String DB_NAME = "VSC_TEST";
    private  DataBase database;

    @Before
    public void setUp() {
        database = new DataBase(DB_NAME);
    }

    @Test
    public void createBranch() {
        final String branchName = "testBranch";
        final boolean active = true;
        database.createBranch(branchName, active);

        final Optional<Branch> branch = database.getBranch(branchName);
        assertTrue(branch.isPresent());
        assertEquals(branchName, branch.get().getName());
        assertTrue(branch.get().isActive());
    }

//    @Test
//    public void getActiveBranch() {
//
//    }
//
//    @Test
//    public void deactivateBranch() {
//
//    }
//
//    @Test
//    public void getBranch() {
//
//    }
//
//    @Test
//    public void closeBranch() {
//
//    }
//
//    @Test
//    public void makeCommit() {
//
//    }
//
//    @Test
//    public void getCommit()  {
//
//    }
//
//    @Test
//    public void getLog() {
//
//    }
//
    @After
    public void tearDown() {
        database.dropDatabase();
    }
}