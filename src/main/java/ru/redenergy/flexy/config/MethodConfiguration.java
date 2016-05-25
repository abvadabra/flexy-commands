package ru.redenergy.flexy.config;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended version of MethodHandle which additionally
 * provides access to method parameters, annotation, also
 * flags and optional parameters which can be specified with @Flag and @Par.
 */
public class MethodConfiguration {

    /**
     * MethodHandle, to which this config related
     */
    private final MethodHandle commandMethodHandle;

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

    public MethodConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations) {
        this(commandMethodHandle, commandParameters, annotations, new ArrayList<String>());
    }

    public MethodConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags) {
        this(commandMethodHandle, commandParameters, annotations, availableFlags, new ArrayList<String>());
    }

    public MethodConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, List<String> availablePars) {
        this.commandMethodHandle = commandMethodHandle;
        this.commandParameters = commandParameters;
        this.annotations = annotations;
        this.availableFlags = availableFlags;
        this.availableParameters = availablePars;
    }

    public MethodHandle getMethod() {
        return commandMethodHandle;
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
