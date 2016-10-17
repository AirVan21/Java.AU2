package ru.spbau.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FileManager class provides interface to FileSystem information
 */
public class FileManager {
    /**
     * Returns current path
     * @return path string
     */
    public static String getPath() {
        return System.getProperty("user.dir");
    }

    public static Set<String> listFiles(String path, boolean isRecursive) {
        return FileUtils.listFiles(new File(path), null, isRecursive)
                .stream()
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Returns file for specified path
     * @param filePath path string
     * @return file if it exists
     */
    public static Optional<File> getFile(String filePath) {
        File file;
        try {
            file = FileUtils.getFile(filePath);
        } catch (NullPointerException exc) {
            return Optional.empty();
        }

        return Optional.of(file);
    }

    /**
     * Reads file to string
     * @param file - text file
     * @return string with file content
     */
    public static Optional<String> readFile(File file) {
        String result;
        try {
            result = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException exc) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    /**
     * Deletes file quietly
     * @param filePath file path
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        FileUtils.deleteQuietly(file);
    }
}
