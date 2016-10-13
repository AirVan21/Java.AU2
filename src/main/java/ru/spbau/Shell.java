package ru.spbau;

import ru.spbau.utility.FileManager;
import ru.spbau.utility.StatusManager;

import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class Shell {
    private final static String WRONG_COMMAND = "Command not found!";
    private final static Logger logger = Logger.getLogger(Shell.class.getName());
    private final static VCS vcs = new VCS(FileManager.getPath());

    private enum Commands {
        STATUS("status")
        , LOG("log")
        , BRANCH("branch")
        , CHECKOUT("checkout")
        , COMMIT("commit")
        , MERGE("merge")
        , INIT("init")
        , ADD("add")
        , RESET("reset")
        , RM("rm")
        , CLEAN("clean")
        ;

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
            case INIT:
                initCommand(arguments);
                break;
            case ADD:
                addCommand(arguments);
                break;
            case RESET:
                resetCommand(arguments);
                break;
            case RM:
                rmCommand(arguments);
                break;
            case CLEAN:
                cleanCommand(arguments);
            default:
                break;
        }
    }

    private static void  statusCommand(List<String> arguments) {
    }

    private static void logCommand(List<String> arguments) {
        vcs.getLog();
    }

    private static void branchCommand(List<String> arguments) {

    }

    private static void checkoutCommand(List<String> arguments) {

    }

    private static void commitCommand(List<String> arguments) {

    }

    private static void mergeCommand(List<String> arguments) {

    }

    private static void initCommand(List<String> arguments) {

    }

    private static void addCommand(List<String> arguments) {

    }

    private static void resetCommand(List<String> arguments) {

    }

    private static void rmCommand(List<String> argumnets) {

    }

    private static void cleanCommand(List<String> arguments) {

    }
}
