package ru.redenergy.rebin.resolve;

import java.util.HashMap;
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

    /**
     * Arguments value will be emtpy map
     */
    public ResolveResult(boolean success){
        this.success = success;
        this.arguments = new HashMap<>();
    }

    public ResolveResult(boolean success, Map<String, String> arguments) {
        this.success = success;
        this.arguments = arguments;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }
}
