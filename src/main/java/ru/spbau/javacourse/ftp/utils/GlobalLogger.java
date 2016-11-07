package ru.spbau.javacourse.ftp.utils;

import java.util.logging.Logger;

/**
 * GlobalLogger is a class for convenient wrong behaviour logging
 */
public class GlobalLogger {
    private static final Logger LOGGER = Logger.getLogger(GlobalLogger.class.getName());

    /**
     * Logs a message to LOGGER object
     * @param message - info that should be logged
     */
    public synchronized static void log(String location, String message) {
        LOGGER.info(location + ": " + message);
    }
}
