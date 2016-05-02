package ru.redenergy.flexy.resolve;

import com.google.common.base.Joiner;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class which is responsible for matching templates with commands and extracting values from command
 */
public class TemplateResolver {

    private static Pattern value = Pattern.compile("\\{.*\\}"); //{value}
    private static Pattern vararg = Pattern.compile("\\{\\*.*\\}"); //{*value}

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
        List<String> listArgs = new ArrayList<>(Arrays.asList(args));
        Map<String, String> foundParameters = extractParameters(listArgs, parameters);
        if(foundParameters == null)
            return new ResolveResult(false);

        List<String> foundFlags = extractFlags(listArgs, flags);

        if(template.isEmpty() && listArgs.size() == 0)
            return new ResolveResult(true, new HashMap<String, String>(), foundParameters, foundFlags);

        String[] pattern = template.split(" ");
        boolean containsVararg = vararg.matcher(pattern[pattern.length - 1]).matches();
        //arguments size validation
        if((pattern.length != listArgs.size() && !containsVararg) || (containsVararg && pattern.length > listArgs.size()))
            return new ResolveResult(false);

        Map<String, String> arguments = extractArguments(pattern, listArgs);
        if(arguments == null)
            return new ResolveResult(false);

        return new ResolveResult(true, arguments, foundParameters, foundFlags);
    }

    /** Returns found parameters or null, if error occurred */
    private Map<String, String> extractParameters(List<String> args, List<String> possiblePars){
        Map<String, String> foundParameters = new HashMap<>();
        if(!possiblePars.isEmpty())
            for(String par: possiblePars) {
                int parIndex = args.indexOf(par);
                if(parIndex == -1)
                    continue;
                if (parIndex >= 0 && parIndex + 1 < args.size()){
                    String val = args.remove(parIndex + 1);
                    args.remove(parIndex);
                    foundParameters.put(par, val);
                } else {
                    return null;
                }
            }
        return foundParameters;
    }

    /** Returns found flags */
    private List<String> extractFlags(List<String> args, List<String> possibleFlags){
        List<String> foundFlags = new ArrayList<>();
        if(!possibleFlags.isEmpty())
            for (String flag : possibleFlags)
                if(args.contains(flag)){
                    foundFlags.add(flag);
                    args.remove(flag);
                }
        return foundFlags;
    }

    /** Returns found argumetns or null, if error occurred*/
    private Map<String, String> extractArguments(String[] template, List<String> args){
        Map<String, String> arguments = new HashMap<>();
        for(int i = 0; i < template.length; i++){
            String origin = template[i];
            String application = args.get(i);

            if(vararg.matcher(origin).matches()){
                String value = Joiner.on(" ").join(args.subList(i, args.size()));
                arguments.put(origin.substring(2, origin.length() - 1), value);
                break;
            }

            if(value.matcher(origin).matches()){
                arguments.put(origin.substring(1, origin.length() - 1), application);
                continue;
            }
            if(!origin.equalsIgnoreCase(application)) return null;
        }
        return arguments;
    }
}
