package ru.spbau;

import ru.spbau.utility.GlobalLogger;

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
        if (!git.toLowerCase().equals(KEY_WORD)) {
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
        if (command != Commands.INIT && !vcs.isValid()) {

        }

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
                initCommand();
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
                cleanCommand();
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
     *
     * @param arguments
     */
    private void branchCommand(List<String> arguments) {
        if (arguments.isEmpty()) {
            System.out.println(vcs.getBranches());
        }
    }

    /**
     * git checkout BRANCH_NAME
     *
     * @param arguments list with one argument (BRANCH_NAME)
     */
    private void checkoutCommand(List<String> arguments) {
        if (arguments.isEmpty() || arguments.size() > 1) {
            GlobalLogger.log("Wrong git commit command format!");
            return;
        }
        vcs.makeCheckout(arguments.get(0));
    }

    /**
     * git commit COMMIT_MESSAGE
     *
     * @param arguments list with commit command arguments
     */
    private void commitCommand(List<String> arguments) {
        if (arguments.isEmpty() || arguments.size() > 1) {
            GlobalLogger.log("Wrong git commit command format!");
            return;
        }
        vcs.makeCommit(arguments.get(0));
    }

    private void mergeCommand(List<String> arguments) {
        if (arguments.isEmpty() || arguments.size() > 1) {
            GlobalLogger.log("Wrong git merge command format!");
            return;
        }
    }

    private void initCommand() {
        vcs.makeInit();
    }

    private void addCommand(List<String> arguments) {
        if (arguments.isEmpty()) {
            GlobalLogger.log("Wrong git add command format!");
            return;
        }
        vcs.makeAdd(arguments);
    }

    private void resetCommand(List<String> arguments) {
        if (arguments.isEmpty()) {
            GlobalLogger.log("Wrong git reset command format!");
            return;
        }
        vcs.makeReset(arguments);
    }

    private void rmCommand(List<String> arguments) {
        if (arguments.isEmpty()) {
            GlobalLogger.log("Wrong git rm command format!");
            return;
        }
        vcs.makeRm(arguments);
    }

    private void cleanCommand() {
        vcs.makeClean();
    }
}
