package ru.redenergy.flexy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about one command entry in command set
 */
public class CommandConfiguration {

    /**
     * Method, to which this config related
     */
    private final Method commandMethod;

    /**
     * Types of commandParameters in command
     */
    private final Class[] commandParameters;

    /**
     * Annotation for each type in commandParameters
     */
    private final Annotation[][] annotations;

    private final List<String> availableFlags;

    private final List<String> availableParameters;

    public CommandConfiguration(Method commandMethod, Class[] commandParameters, Annotation[][] annotations) {
        this(commandMethod, commandParameters, annotations, new ArrayList<String>());
    }

    public CommandConfiguration(Method commandMethod, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags) {
        this(commandMethod, commandParameters, annotations, availableFlags, new ArrayList<String>());
    }

    public CommandConfiguration(Method commandMethod, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, List<String> availablePars) {
        this.commandMethod = commandMethod;
        this.commandParameters = commandParameters;
        this.annotations = annotations;
        this.availableFlags = availableFlags;
        this.availableParameters = availablePars;
    }

    public Method getCommandMethod() {
        return commandMethod;
    }

    public Class[] getCommandParameters() {
        return commandParameters;
    }

    public Annotation[][] getAnnotations() {
        return annotations;
    }

    public List<String> getAvailableFlags() {
        return availableFlags;
    }

    public List<String> getAvailableParameters() {
        return availableParameters;
    }
}
