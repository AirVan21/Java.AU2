package ru.spbau.javacourse.torrent;

import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.IOException;
import java.util.Scanner;

public class RunClient {

    private static String getHelp() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HOWTO:").append("\n");
        sb.append("list |");
        sb.append("add <file path> |");
        sb.append("get <file id> |");
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
                            System.out.println(command);
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
                            System.out.println(command);
                            break;
                        case "exit":
                            System.out.println(command);
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
