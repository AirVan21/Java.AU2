package ru.spbau;

import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class Shell {
    private final static String WRONG_COMMAND = "Command not found!";
    private final static Logger logger = Logger.getLogger(Shell.class.getName());

    private enum Commands {
        STATUS("status")
        , LOG("log")
        , BRANCH("branch")
        , CHECKOUT("checkout")
        , COMMIT("commit")
        , MERGE("merge");
        
        private final String source;

        Commands(final String input) {
            source = input;
        }
    }
    
    public static void execute(List<String> input) {
        if (input.isEmpty()) {
            return;
        }
        String name = input.remove(0);

        try {
            processCommand(Commands.valueOf(name), input);
        } catch (IllegalArgumentException e) {
            logger.info(WRONG_COMMAND);
        }
    }
    
    private static void processCommand(Commands command, List<String> arguments) {
        switch (command) {
            case STATUS:
                statusCommand(arguments);
                break;
            case LOG:
                logCommand(arguments);
                break;
            case BRANCH:
                branchCommand(arguments);
                break;
            case CHECKOUT:
                checkoutCommand(arguments);
                break;
            case COMMIT:
                commitCommand(arguments);
                break;
            case MERGE:
                mergeCommand(arguments);
                break;
            default:
                break;
        }
    }

    private static void  statusCommand(List<String> arguments) {

    }

    private static void logCommand(List<String> arguments) {

    }

    private static void branchCommand(List<String> arguments) {

    }

    private static void checkoutCommand(List<String> arguments) {

    }

    private static void commitCommand(List<String> arguments) {

    }

    private static void mergeCommand(List<String> arguments) {

    }
}
