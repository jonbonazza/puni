package com.github.exceptions;

/**
 *
 * Created by bonazza on 12/3/14.
 */
public class NoConfigurationException extends Exception {

    public NoConfigurationException() {
        super("No configuration file provided. Please call loadConfiguration() before starting the application.");
    }
}
