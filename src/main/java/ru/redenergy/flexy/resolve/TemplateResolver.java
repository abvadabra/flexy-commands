package ru.redenergy.flexy.resolve;

import com.google.common.base.Joiner;

import java.util.*;

/**
 * Utility class which is responsible for matching templates with commands and extracting values from command
 */
public class TemplateResolver {

    /**
     * Matches template with given argument lists and extracts values from args according to template
     * @param template - template of the command, all arguments must be putted between brackets e.g. "add {user}"
     * @param args - arguments received from user
     * @return Returns ResolveResult in which #isSuccess() returns wheter or not templates matches arguments and #getCommandParameters() return map of values extracted from arguments
     */
    public ResolveResult resolve(String template, String[] args){
        return resolve(template, args, Collections.<String>emptyList());
    }

    /**
     * Matches template with given argument lists and extracts values from args according to template
     * @param template - template of the command, all arguments must be putted between brackets e.g. "add {user}"
     * @param args - arguments received from user
     * @param flags - flags which can be met in args and wount be recognized as part of command
     * @return Returns ResolveResult in which #isSuccess() returns wheter or not templates matches arguments and #getCommandParameters() return map of values extracted from arguments
     */
    public ResolveResult resolve(String template, String[] args, List<String> flags){
        return resolve(template, args, flags, Collections.<String>emptyList());
    }

    /**
     * Matches template with given argument lists and extracts values from args according to template
     * @param template - template of the command, all arguments must be putted between brackets e.g. "add {user}"
     * @param args - arguments received from user
     * @param flags - flags which can be met in args and wount be recognized as part of command
     * @param parameters - possible parameters which can be met in args
     * @return Returns ResolveResult in which #isSuccess() returns wheter or not templates matches arguments and #getArguments() return map of values extracted from arguments
     */
    public ResolveResult resolve(String template, String[] args, List<String> flags, List<String> parameters){
        Map<String, String> foundParameters = new HashMap<>();
        if(!parameters.isEmpty()){
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            for(String par: parameters) {
                int parIndex = listArgs.indexOf(par);
                if (parIndex >= 0 && parIndex + 1 < listArgs.size()){
                    String val = listArgs.remove(parIndex + 1);
                    listArgs.remove(parIndex);
                    foundParameters.put(par, val);
                } else {
                    return new ResolveResult(false);
                }
            }
            args = listArgs.toArray(new String[listArgs.size()]);
        }
        List<String> foundFlags = new ArrayList<>();
        if(!flags.isEmpty()) {
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            for (String flag : flags) {
                if(listArgs.contains(flag)){
                    foundFlags.add(flag);
                    listArgs.remove(flag);
                }
            }
            args = listArgs.toArray(new String[listArgs.size()]);
        }

        if(template.isEmpty() && args.length == 0) return new ResolveResult(true, new HashMap<String, String>(), foundParameters, foundFlags);
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

        return new ResolveResult(true, arguments, foundParameters, foundFlags);
    }
}
