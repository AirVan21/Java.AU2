package ru.spbau.javacourse.torrent;

import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Starts Torrent Client
 */
public class RunClient {

    private static String getHelp() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Usage: ");
        sb.append("list | ");
        sb.append("add <file path> | ");
        sb.append("get <file id> | ");
        sb.append("exit");

        return sb.toString();
    }

    public static void main(String[] args)  {
        if (args.length < 1) {
            System.out.println("Please, enter argument!");
            System.out.println("Usage: <client port>");
            return;
        }

        short port = Short.parseShort(args[0]);
        final Client client = new Client(GlobalConstants.DEFAULT_HOST,  port);
        try {
            client.connectToServer();
        } catch (IOException e) {
            System.out.println("Couldn't connect to tracker!");
            System.out.println(e.getMessage());
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] input = line.split(" ");
                if (input.length < 1) {
                    System.out.println(getHelp());
                    continue;
                }

                final String command = input[0];
                try {
                    switch (command) {
                        case "list":
                            Optional<List<SimpleFileRecord>> result = client.doList();
                            if (result.isPresent()) {
                                final StringBuilder sb = new StringBuilder();
                                for (SimpleFileRecord record : result.get()) {
                                    sb.append(record.getId()).append(" : ");
                                    sb.append(record.getName()).append(" (size = ").append(record.getSize()).append(" bytes)");
                                }
                                System.out.println(sb.toString());
                            } else {
                                System.out.println("list command failed!");
                            }
                            break;
                        case "add":
                            if (input.length < 2) {
                                System.out.println("Invalid add command format!");
                                System.out.println(getHelp());
                                continue;
                            }
                            final String path = input[1];
                            client.doUpload(path);
                            break;
                        case "get":
                            if (input.length < 2) {
                                System.out.println("Invalid get command format!");
                                System.out.println(getHelp());
                                continue;
                            }
                            final int fileId = Integer.parseInt(input[1]);
                            break;
                        case "exit":
                            client.disconnectFromServer();
                            return;
                        default:
                            System.out.println(getHelp());
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Fail happened: " + e.getMessage());
                }
            }
        }
    }
}
