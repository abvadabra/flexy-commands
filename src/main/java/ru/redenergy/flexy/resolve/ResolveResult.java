package ru.redenergy.flexy.resolve;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains result of command resolve
 */
public class ResolveResult {

    /**
     * Wheter or not it was successfully
     */
    private final boolean success;

    /**
     * Arguments, extracted from arguments, may be empty
     */
    private final Map<String, String> arguments;

    private final List<String> foundFlags;

    private final Map<String, String> parameters;

    /**
     * Arguments value will be emtpy map
     */
    public ResolveResult(boolean success){
        this(success, new HashMap<String, String>());
    }

    public ResolveResult(boolean success, Map<String, String> arguments) {
        this(success, arguments, Collections.<String>emptyList());
    }

    public ResolveResult(boolean success, Map<String, String> arguments, List<String> foundFlags) {
        this(success, arguments, new HashMap<String, String>(), foundFlags);
    }

    public ResolveResult(boolean success, Map<String, String> arguments, Map<String, String> parameters, List<String> foundFlags){
        this.success = success;
        this.arguments = arguments;
        this.parameters = parameters;
        this.foundFlags = foundFlags;
    }

    public List<String> getFoundFlags() {
        return foundFlags;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
