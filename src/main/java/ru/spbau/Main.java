package ru.spbau;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 *  Main class
 *
 *  Main class is used as a simple "how to" example
 */
public class Main {
    private final static String TEST_DIR = "src/test/resources/";

    public static void main(String[] args) {
        Path path = Paths.get(TEST_DIR);
        Shell shell = new Shell(path);
        shell.execute(Arrays.asList("git init".split(" ")));
    }
}
