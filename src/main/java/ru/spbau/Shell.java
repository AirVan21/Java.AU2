package ru.spbau;

import ru.spbau.utility.GlobalLogger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shell - is a simple class for parsing input and printing results
 */
public class Shell {
    /**
     * Key string constants
     */
    private final static String KEY_WORD = "git";
    private final static int MINIMAL_INPUT_SIZE = Arrays.asList("git", "command").size();
    /**
     * VCS object which performs all VCS logic
     */
    private final VCS vcs;

    /**
     * Commands enum represents acceptable git commands
     */
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

    /**
     * Executes command parsing input values
     * @param input list of word which may be commands
     */
    public void execute(List<String> input) {
        if (input.size() < MINIMAL_INPUT_SIZE) {
            GlobalLogger.log("Please, input: 'git COMMAND_NAME [ARGS]'");
            return;
        }

        final String git = input.get(0);
        if (!git.toLowerCase().equals(KEY_WORD)) {
            GlobalLogger.log("Please, type  a 'git' before command!");
            return;
        }

        final String commandName = input.get(1);
        final List<String> arguments = new ArrayList<>();
        for (int i = 2; i < input.size(); i++) {
            arguments.add(input.get(i));
        }

        try {
            processCommand(Commands.valueOf(commandName.toLowerCase()), input);
        } catch (IllegalArgumentException e) {
            GlobalLogger.log("Specified command is not supported!");
        }
    }
    
    private void processCommand(Commands command, List<String> arguments) {
        if (command != Commands.INIT && !vcs.isValid()) {
            GlobalLogger.log("Please, init your repo first!");
            return;
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
                GlobalLogger.log("Truing to process wrong command!");
                break;
        }
    }

    /**
     * git status
     */
    private void statusCommand() {
        System.out.println(vcs.getStatus());
    }

    /**
     * git log
     */
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
        // git branch
        if (arguments.isEmpty()) {
            System.out.println(vcs.getBranches());
            return;
        }
        // git branch BRANCH_NAME
        if (arguments.size() == 1) {
            vcs.makeBranch(arguments.get(0));
            return;
        }
        // git branch -d BRANCH_NAME
        if (arguments.size() == 2 && arguments.get(0).equals("-d")) {
            // It's better to use Apache CLI, but is huge overkill for the task
            vcs.deleteBranch(arguments.get(1));
            return;
        }

        GlobalLogger.log("Wrong git branch command format!");
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

    /**
     * git merge BRANCH_NAME
     *
     * @param arguments list with branch name
     */
    private void mergeCommand(List<String> arguments) {
        if (arguments.isEmpty() || arguments.size() > 1) {
            GlobalLogger.log("Wrong git merge command format!");
            return;
        }
        vcs.makeMerge(arguments.get(0));
    }

    /**
     * git init
     */
    private void initCommand() {
        vcs.makeInit();
    }

    /**
     * git add FILE_NAME FILE_NAME ...
     *
     * @param arguments paths to files which should be added
     */
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

    /**
     * git rm FILE_NAME FILE_NAME ...
     *
     * @param arguments paths to files which should be deleted from track and repo
     */
    private void rmCommand(List<String> arguments) {
        if (arguments.isEmpty()) {
            GlobalLogger.log("Wrong git rm command format!");
            return;
        }
        vcs.makeRm(arguments);
    }

    /**
     * git clean
     */
    private void cleanCommand() {
        vcs.makeClean();
    }
}
