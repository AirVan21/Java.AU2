package ru.spbau;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class Shell {
    private final static String WRONG_COMMAND = "Command not found!";
    private final static String KEY_WORD = "git";
    private final static Logger logger = Logger.getLogger(Shell.class.getName());
    private final VCS vcs;

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

    public Shell(Path path) {
        vcs = new VCS(path);
    }
    
    public void execute(List<String> input) {
        if (input.size() < 2) {
            return;
        }

        final String git = input.get(0);
        if (!git.toLowerCase().equals("git")) {
            return;
        }

        String name = input.get(1);
        try {
            processCommand(Commands.valueOf(name.toLowerCase()), input);
        } catch (IllegalArgumentException e) {
            logger.info(WRONG_COMMAND);
        }
    }
    
    private void processCommand(Commands command, List<String> arguments) {
        switch (command) {
            case STATUS:
                statusCommand();
                break;
            case LOG:
                logCommand();
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

    private void statusCommand() {
        System.out.println(vcs.getStatus());
    }

    private void logCommand() {
        System.out.println(vcs.getLog());
    }

    /**
     * git branch
     * git branch BRANCH_NAME
     * git branch -d BRANCH_NAME
     */
    private void branchCommand(List<String> arguments) {
        // Use Apache Commons for argument parsing
        System.out.println(vcs.getBranches());
    }

    private void checkoutCommand(List<String> arguments) {

    }

    private void commitCommand(List<String> arguments) {

    }

    private void mergeCommand(List<String> arguments) {

    }

    private void initCommand(List<String> arguments) {
        vcs.makeInit();
    }

    private void addCommand(List<String> arguments) {

    }

    private void resetCommand(List<String> arguments) {

    }

    private void rmCommand(List<String> argumnets) {

    }

    private void cleanCommand(List<String> arguments) {

    }
}
