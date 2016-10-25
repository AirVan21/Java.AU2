package ru.spbau.javacourse.ftp.commands;

import org.apache.commons.io.IOUtils;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestManager is a class for aggregation response waiting and handling logic
 */
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
     * Reads info from inputStream to outputFile
     * @param inputStream stream (source info provider)
     * @param outputFile file where info will be written
     * @throws IOException
     */
    public static void getFileResponse(DataInputStream inputStream, File outputFile) throws IOException {
        final long size = inputStream.readLong();
        final FileWriter writer = new FileWriter(outputFile);
        IOUtils.copyLarge(new InputStreamReader(inputStream), writer, 0, size);
        writer.close();
    }
}
