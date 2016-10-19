package ru.spbau.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.db.entity.Branch;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * DataBaseTest class is c class for testing VCS interaction with Morphia
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

    @Test
    public void getActiveBranch() {
        Optional<Branch> branch = database.getActiveBranch();
        assertFalse(branch.isPresent());

        final String branchName = "testBranch";
        final boolean active = true;
        database.createBranch(branchName, active);
        branch = database.getActiveBranch();

        assertTrue(branch.isPresent());
        assertEquals(branchName, branch.get().getName());
    }

    @Test
    public void deactivateBranch() {
        final String branchName = "testBranch";
        final boolean active = true;
        Branch branch = database.createBranch(branchName, active);

        assertTrue(branch.isActive());
        database.deactivateBranch();

        Optional<Branch> dbBranch = database.getBranch(branchName);
        assertTrue(dbBranch.isPresent());
        assertFalse(dbBranch.get().isActive());
    }

    @Test
    public void getBranch() {
        final String branchName = "testBranch";
        final boolean active = true;
        database.createBranch(branchName, active);

        final Optional<Branch> branch = database.getBranch(branchName);
        assertTrue(branch.isPresent());
        assertTrue(branch.get().isActive());
        assertEquals(branchName, branch.get().getName());
    }

    @Test
    public void getBranches() {
        final String branchNameOne = "testBranchOne";
        final String branchNameTwo = "testBranchTwo";

        database.createBranch(branchNameOne, false);
        database.createBranch(branchNameTwo, false);

        final Set<Branch> branches = database.getBranches();
        assertEquals(2, branches.size());
    }

    @Test
    public void closeBranch() {

    }

    @After
    public void tearDown() {
        database.dropDatabase();
    }
}