package ru.spbau.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by airvan21 on 27.09.16.
 */
public class FileManager {
    private static String getPath() {
        return System.getProperty("user.dir");
    }

    private static List<String> getFiles(String path) {
        List<String> fileNames = FileUtils.listFiles(new File(getPath()), null, true)
                .stream()
                .map(file -> file.getName())
                .collect(Collectors.toList());

        return fileNames;
    }
}
