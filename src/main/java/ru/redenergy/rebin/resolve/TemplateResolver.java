package ru.redenergy.rebin.resolve;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class which is responsible for matching templates with commands and extracting values from command
 */
public class TemplateResolver {

    /**
     * Matches template with given argument lists and extracts values from args according to template
     * @param template - template of the command, all arguments must be putted between brackets e.g. "add {user}"
     * @param args - arguments received from user
     * @return Returns ResolveResult in which #isSuccess() returns wheter or not templates matches arguments and #getParameters() return map of values extracted from arguments
     */
    public ResolveResult resolve(String template, String[] args){
        if(template.isEmpty() && args.length == 0) return new ResolveResult(true);
        String[] pattern = template.split(" ");
        if(pattern.length != args.length) return new ResolveResult(false);

        Map<String, String> arguments = new HashMap<>();
        for(int i = 0; i < pattern.length; i++){
            String origin = pattern[i];
            String application = args[i];
            if(origin.matches("\\{.*\\}")){
                arguments.put(origin.substring(1, origin.length() - 1), application);
                continue;
            }
            if(!origin.equalsIgnoreCase(application)) return new ResolveResult(false);
        }

        return new ResolveResult(true, arguments);
    }
}
