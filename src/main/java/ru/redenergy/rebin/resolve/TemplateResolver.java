package ru.redenergy.rebin.resolve;

import com.google.common.base.Joiner;

import java.util.Arrays;
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
        boolean containsVararg = pattern[pattern.length - 1].matches("\\{\\*.*\\}");
        if((pattern.length != args.length && !containsVararg) || (containsVararg && pattern.length > args.length))
                return new ResolveResult(false);



        Map<String, String> arguments = new HashMap<>();
        for(int i = 0; i < pattern.length; i++){
            String origin = pattern[i];
            String application = args[i];

            if(origin.matches("\\{\\*.*\\}")){ //{*value}
                String value = Joiner.on(" ").join(Arrays.copyOfRange(args, i, args.length));
                arguments.put(origin.substring(2, origin.length() - 1), value);
                break;
            }

            if(origin.matches("\\{.*\\}")){ //{value}
                arguments.put(origin.substring(1, origin.length() - 1), application);
                continue;
            }
            if(!origin.equalsIgnoreCase(application)) return new ResolveResult(false);
        }

        return new ResolveResult(true, arguments);
    }
}
