package ru.redenergy.flexy.config;

import ru.redenergy.flexy.annotation.Command;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.util.List;

/**
 * MethodConfiguration which additionally holds @Command
 */
public class CommandConfiguration extends MethodConfiguration {

    private final Command command;

    public CommandConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, Command command) {
        super(commandMethodHandle, commandParameters, annotations);
        this.command = command;
    }

    public CommandConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, Command command) {
        super(commandMethodHandle, commandParameters, annotations, availableFlags);
        this.command = command;
    }

    public CommandConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, List<String> availablePars, Command command) {
        super(commandMethodHandle, commandParameters, annotations, availableFlags, availablePars);
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
