package ru.spbau.javacourse.ftp.server;

import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.utils.FileManager;
import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * HandleTask is a class which handles Client request (and executes it)
 */
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
            GlobalLogger.log(HandleTask.class.getName(), e.getMessage());
        } finally {
            try {
                taskSocket.close();
            } catch (IOException e) {
                GlobalLogger.log(getClass().getName(), e.getMessage());
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
                GlobalLogger.log(getClass().getName(), "Invalid command!");
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
        if (!filePath.exists()) {
            output.writeInt(0);
            output.flush();
            return;
        }
        // TODO: might be null
        final List<File> files = Arrays.asList(filePath.listFiles());
        output.writeInt(files.size());
        for (File file : files) {
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
        String path = input.readUTF();
        final File file = new File(path);
        FileManager.writeFileToOutputStream(output, file);
        output.flush();
    }
}
