package ru.spbau.javacourse.ftp.server;

import org.apache.commons.io.FileUtils;
import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.utils.FileManager;
import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;

public class HandleTask implements Runnable {
    private final Socket taskSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public HandleTask(Socket connection) {
        taskSocket = connection;
    }

    public void Initialize() throws IOException {
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (!taskSocket.isClosed() && taskSocket.isConnected()) {
                final int requestId = input.readInt();
                executeRequest(requestId);
            }
        }
        catch (IOException e) {
            GlobalLogger.log(e.getMessage());
        } finally {
            try {
                taskSocket.close();
            } catch (IOException e) {
                GlobalLogger.log(e.getMessage());
            }
        }
    }

    private void executeRequest(int requestId) throws IOException {
        switch (requestId) {
            case Request.GET_LIST_REQUEST:
                handleGetListRequest();
                break;
            case Request.GET_FILE_REQUEST:
                handleGetFileRequest();
                break;
            default:
                GlobalLogger.log("Invalid command!");
        }
    }

    private void handleGetListRequest() throws IOException {
        final String path = input.readUTF();
        final Set<File> fileNames = FileUtils.listFilesAndDirs(new File(path), null, null)
                .stream()
                .collect(Collectors.toSet());

        output.writeInt(fileNames.size());
        for (File file : fileNames) {
            output.writeUTF(file.getName());
            output.writeBoolean(file.isDirectory());
        }
        output.flush();
    }

    private void handleGetFileRequest() throws IOException {
        String path = input.readUTF();
        final File file = new File(path);
        FileManager.writeFileToOutputStream(output, file);
        output.flush();
    }
}
