package ru.spbau.javacourse.ftp.utils;

import org.apache.commons.io.IOUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
}
