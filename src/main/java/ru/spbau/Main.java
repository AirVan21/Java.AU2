package ru.spbau;

import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class Main {
    public static void main(String[] args) {
        List<String> input = Arrays.asList("git", "rm");
        Shell.execute(input);
    }
}
