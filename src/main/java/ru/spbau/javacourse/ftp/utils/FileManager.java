package ru.spbau.javacourse.ftp.utils;

import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * FileManager class - IOUtils wrapper class which provides functionality for IO operations
 */
@Log
public class FileManager {
    public static void writeFileToOutputStream(DataOutputStream output, File file)  {
        if (!file.exists() || file.isDirectory()) {
            // request with zero size
            try {
                output.writeLong(0);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
            return;
        }

        // try-with-resources
        try (final FileInputStream input = new FileInputStream(file)) {
            try {
                output.writeLong(file.length());
                IOUtils.copyLarge(input, output);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }
}
