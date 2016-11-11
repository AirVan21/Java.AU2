package ru.spbau.javacourse.ftp.commands;

import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestManager is a class for aggregation response waiting and handling logic
 */
@Log
public class RequestManager {
    /**
     * Reads info about files and directories from inputStream
     * @param inputStream stream with info
     * @return list on FolderEntities
     * @throws IOException
     */
    public static List<FolderEntity> getListResponse(DataInputStream inputStream) throws IOException {
        List<FolderEntity> result = new ArrayList<>();
        final int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            final String name = inputStream.readUTF();
            final boolean isFolder = inputStream.readBoolean();
            result.add(new FolderEntity(name, isFolder));
        }

        return result;
    }

    /**
     * Reads info from input to outputFile
     * @param input stream (source info provider)
     * @param outputFile file where info will be written
     */
    public static void getFileResponse(DataInputStream input, File outputFile) {
        final long size;
        try {
            size = input.readLong();
        } catch (IOException e) {
            log.info("Couldn't read file size!");
            return;
        }
        // try-with-resources
        try (final FileOutputStream output = new FileOutputStream(outputFile)) {
            try {
                IOUtils.copyLarge(input, output, 0, size);
            } catch (IOException e) {
                log.info("Couldn't copyLarge!");
            }
        } catch (IOException e) {
            log.info("Couldn't create file output stream!");
        }
    }
}
