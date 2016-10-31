package ru.spbau.javacourse.ftp.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * FileManager class - IOUtils wrapper class which provides functionality for IO operations
 */
public class FileManager {
    public static void writeFileToOutputStream(DataOutputStream output, File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            // request with zero size
            output.writeLong(0);
            return;
        }

        output.writeLong(file.length());
        IOUtils.copyLarge(new FileInputStream(file), output);
    }

    /**
     * Returns file for specified path
     * @param filePath path string
     * @return file if it exists
     */
    public static Optional<File> getFile(String filePath) {
        File file;
        try {
            file = new File(filePath);
        } catch (NullPointerException exc) {
            return Optional.empty();
        }

        return file.exists() ? Optional.of(file) : Optional.empty();
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

    /**
     * Deletes file quietly
     * @param filePath file path
     */
    public static void deleteFile(String filePath) {
        final File file = new File(filePath);
        FileUtils.deleteQuietly(file);
    }
}
