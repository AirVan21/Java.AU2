package ru.spbau.javacourse.ftp.server;

import lombok.extern.java.Log;
import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.utils.FileManager;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * HandleTask is a class which handles Client request (and executes it)
 */
@Log
public class HandleTask implements Runnable {
    private final Socket taskSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public HandleTask(Socket connection) throws IOException {
        taskSocket = connection;
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    /**
     * Parses and executes client request
     */
    @Override
    public void run() {
        try {
            while (!taskSocket.isClosed()) {
                final int requestId = input.readInt();
                executeRequest(requestId);
            }
        }
        catch (IOException e) {
            // skip
        } finally {
            try {
                taskSocket.close();
            } catch (IOException e) {
               log.info(e.getMessage());
            }
        }
    }

    /**
     * Chooses between LIST and GET requests and executes
     * @param requestId id of request
     * @throws IOException
     */
    private void executeRequest(int requestId) throws IOException {
        switch (requestId) {
            case Request.GET_LIST_REQUEST:
                handleGetListRequest();
                break;
            case Request.GET_FILE_REQUEST:
                handleGetFileRequest();
                break;
            default:
                log.info("Invalid command!");
                taskSocket.close();
        }
    }

    /**
     * Writes "ls" info about selected directory to output stream
     * @throws IOException
     */
    private void handleGetListRequest() throws IOException {
        final String path = input.readUTF();
        final File filePath = new File(path);
        final File[] files = filePath.listFiles();
        if (!filePath.exists() || files == null) {
            output.writeInt(0);
            output.flush();
            return;
        }

        final List<File> filesList = Arrays.asList(files);
        output.writeInt(filesList.size());
        for (File file : filesList) {
            output.writeUTF(file.getName());
            output.writeBoolean(file.isDirectory());
        }
        output.flush();
    }

    /**
     * Writes selected file to output stream
     * @throws IOException
     */
    private void handleGetFileRequest() throws IOException {
        final String path = input.readUTF();
        final File file = new File(path);
        FileManager.writeFileToOutputStream(output, file);
        output.flush();
    }
}
