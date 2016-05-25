package ru.redenergy.flexy.config;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.util.List;

/**
 * MethodConfiguration which stores exception
 */
public class ExceptionHandlerConfiguration extends MethodConfiguration{

    private final Class<? extends Throwable> exception;

    public ExceptionHandlerConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, Class<? extends Throwable> exception) {
        super(commandMethodHandle, commandParameters, annotations);
        this.exception = exception;
    }

    public ExceptionHandlerConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, Class<? extends Throwable> exception) {
        super(commandMethodHandle, commandParameters, annotations, availableFlags);
        this.exception = exception;
    }

    public ExceptionHandlerConfiguration(MethodHandle commandMethodHandle, Class[] commandParameters, Annotation[][] annotations, List<String> availableFlags, List<String> availablePars, Class<? extends Throwable> exception) {
        super(commandMethodHandle, commandParameters, annotations, availableFlags, availablePars);
        this.exception = exception;
    }

    public Class<? extends Throwable> getException() {
        return exception;
    }
}
