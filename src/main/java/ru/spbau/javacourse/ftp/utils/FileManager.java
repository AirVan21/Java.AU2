package ru.spbau.javacourse.ftp.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * FileManager class - IOUtils wrapper class which provides functionality for IO operations
 */
public class FileManager {
    public static void writeFileToOutputStream(DataOutputStream output, File file)  {
        if (!file.exists() || file.isDirectory()) {
            // request with zero size
            try {
                output.writeLong(0);
            } catch (IOException e) {
                GlobalLogger.log(FileManager.class.getName(), e.getMessage());
            }
            return;
        }

        // try-with-resources
        try (final FileInputStream input = new FileInputStream(file)) {
            try {
                output.writeLong(file.length());
                IOUtils.copyLarge(input, output);
            } catch (IOException e) {
                GlobalLogger.log(FileManager.class.getName(), e.getMessage());
            }
        } catch (IOException e) {
            GlobalLogger.log(FileManager.class.getName(), e.getMessage());
        }
    }

    /**
     * Returns file for specified path
     * @param filePath path string
     * @return file if it exists
     */
    public static Optional<File> getFile(String filePath) {
        final Path path = Paths.get(filePath);
        return Files.exists(path) ? Optional.of(new File(filePath)) : Optional.empty();
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
     * Reads file in string if file exists
     * @param path path to file
     * @return Optional with file text if file exist, Optional empty elsewhere
     */
    public static Optional<String> readFile(String path) {
        Optional<File> file = getFile(path);
        return file.isPresent() ? readFile(file.get()) : Optional.empty();
    }
}
