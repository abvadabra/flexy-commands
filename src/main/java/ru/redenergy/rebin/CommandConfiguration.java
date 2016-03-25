package ru.redenergy.rebin;

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
     * Types of parameters in command
     */
    private final Class[] parameters;

    /**
     * Annotation for each type in parameters
     */
    private final Annotation[][] annotations;

    private final List<String> availableFlags;

    public CommandConfiguration(Method commandMethod, Class[] parameters, Annotation[][] annotations) {
        this(commandMethod, parameters, annotations, new ArrayList<String>());
    }

    public CommandConfiguration(Method commandMethod, Class[] parameters, Annotation[][] annotations, List<String> availableFlags) {
        this.commandMethod = commandMethod;
        this.parameters = parameters;
        this.annotations = annotations;
        this.availableFlags = availableFlags;
    }

    public Method getCommandMethod() {
        return commandMethod;
    }

    public Class[] getParameters() {
        return parameters;
    }

    public Annotation[][] getAnnotations() {
        return annotations;
    }

    public List<String> getAvailableFlags() {
        return availableFlags;
    }
}
