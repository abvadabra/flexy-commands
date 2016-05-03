package ru.redenergy.flexy;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import ru.redenergy.flexy.annotation.Arg;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.Flag;
import ru.redenergy.flexy.annotation.Par;
import ru.redenergy.flexy.permission.IProvider;
import ru.redenergy.flexy.resolve.ResolveResult;
import ru.redenergy.flexy.resolve.TemplateResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which manages all logic of FlexyCommand resolution and invokation
 */
public class CommandBackend {

    private static IProvider provider = new IProvider.VanillaProvider();

    private final TemplateResolver resolver = new TemplateResolver();
    private final FlexyCommand command;
    private boolean collected = false;

    private List<CommandConfiguration> configs = new ArrayList<>();

    public CommandBackend(FlexyCommand command) {
        this.command = command;
        collectCommands();
    }

    public void displayUsage(ICommandSender sender){
        for(CommandConfiguration config: configs){
            Command command = config.getCommandMethod().getAnnotation(Command.class);
            if(!command.displayable() || !hasPermission(command, sender)) continue;
            String view = "/" + this.command.getCommandName() + " " + command.value().replace("{", "<").replace("}", ">") + " ";
            StringBuilder options = new StringBuilder()
                    .append(getDisplayableFlags(config))
                    .append(getDisplayableParameters(config));
            String output = view + options.toString();
            TextComponentTranslation textcomponenttranslation = new TextComponentTranslation(output);
            textcomponenttranslation.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + this.command.getCommandName() + " "));
            sender.addChatMessage(textcomponenttranslation);
        }
    }

    private StringBuilder getDisplayableFlags(CommandConfiguration config){
        StringBuilder flags = new StringBuilder();
        if(!config.getAvailableFlags().isEmpty()){
            for(String flag: config.getAvailableFlags())
                flags.append("[").append(flag).append("]").append(" ");
            flags.append(" ");
        }
        return flags;
    }

    private StringBuilder getDisplayableParameters(CommandConfiguration config){
        StringBuilder parameters = new StringBuilder();
        if(!config.getAvailableParameters().isEmpty()){
            for (String par: config.getAvailableParameters())
                parameters.append("[").append(par).append(" ").append("<v>").append("]").append(" ");
            parameters.append(" ");
        }
        return parameters;
    }

    /**
     * Scans class and remembers all methods annotated with @Command annotation to be included into resolving strategy
     */
    public void collectCommands(){
        if(collected)
            throw new RuntimeException("Attempt to collect commands more that once is unacceptable");
        for(Method m : command.getClass().getMethods())
            if(m.isAnnotationPresent(Command.class))
                collectMethod(m);
        collected = true;
    }

    private void collectMethod(Method method){
        Command command = method.getAnnotation(Command.class);
        if(command.disabled())
            return;
        Class[] commandParameters = method.getParameterTypes();
        Annotation[][] commandAnnotations = method.getParameterAnnotations();
        List<String> flags = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        for(Annotation[] annotations: commandAnnotations)
            for(Annotation annotation: annotations)
                if(annotation instanceof Flag)
                    flags.add(((Flag) annotation).value());
                else if(annotation instanceof Par)
                    parameters.add(((Par) annotation).value());
        configs.add(new CommandConfiguration(method, commandParameters, commandAnnotations, flags, parameters));
    }

    public void resolveExecute(ICommandSender sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        boolean executed = false;
        for (CommandConfiguration configuration : configs)
            if(resolveExecuteCommand(configuration, sender, args))
                executed = true;
        if (executed)
            return;
        TextComponentBase msg = new TextComponentTranslation("commands.generic.notFound");
        msg.getChatStyle().setColor(TextFormatting.RED);
        sender.addChatMessage(msg);
    }

    private boolean resolveExecuteCommand(CommandConfiguration config, ICommandSender sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        String template = config.getCommandMethod().getAnnotation(Command.class).value();
        ResolveResult result = resolver.resolve(template, args, config.getAvailableFlags(), config.getAvailableParameters());
        if (result.isSuccess())
            executeCommand(config, sender, result);
        return result.isSuccess();
    }

    private void executeCommand(CommandConfiguration config, ICommandSender sender, ResolveResult result) throws InvocationTargetException, IllegalAccessException {
        Command command = config.getCommandMethod().getAnnotation(Command.class);
        if (hasPermission(command, sender)) {
            invokeCommand(config, sender, result);
        } else {
            TextComponentBase msg = new TextComponentTranslation("commands.generic.permission");
            msg.getChatStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(msg);
        }
    }

    private void invokeCommand(CommandConfiguration command, ICommandSender sender, ResolveResult result) throws InvocationTargetException, IllegalAccessException {
        Object[] arguments = getArguments(command, sender, result);
        if (arguments != null) {
            command.getCommandMethod().invoke(this.command, arguments);
        } else {
            TextComponentTranslation msg = new TextComponentTranslation("commands.generic.usage", new TextComponentTranslation(this.command.getCommandUsage(sender)));
            msg.getChatStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(msg);
        }
    }

    private boolean hasPermission(Command command, ICommandSender sender){
        return command.permission().equals("#")
                || sender instanceof MinecraftServer
                || (sender instanceof EntityPlayerMP && provider.hasPermission((EntityPlayerMP) sender, command.permission()));
    }

    private Object[] getArguments(CommandConfiguration command, ICommandSender sender, ResolveResult result){
        Object[] parameters = new Object[command.getCommandParameters().length];
        for(int i = 0; i < command.getCommandParameters().length; i++){
            Object value = getAppropriateValue(command.getCommandParameters()[i], command.getAnnotations()[i], sender, result);
            if(value == null)
                return null;
            else
                parameters[i] = value;
        }
        return parameters;
    }

    private Object getAppropriateValue(Class<?> clazz, Annotation[] annotations, ICommandSender sender, ResolveResult result){
        if(clazz.isAssignableFrom(sender.getClass())){
            return sender;
        } else {
            Arg arg = getArgument(annotations);
            if (arg != null)
                return getTypeValue(result.getArguments().get(arg.value()), clazz);
            Flag flag = getFlag(annotations);
            if (flag != null && clazz.isAssignableFrom(boolean.class))
                return result.getFoundFlags().contains(flag.value());
            Par par = getParameter(annotations);
            if(par != null){
                if(clazz.isAssignableFrom(Optional.class)){
                    Object value = getTypeValue(result.getParameters().get(par.value()), par.type());
                    return Optional.fromNullable(value);
                }
                return getTypeValue(result.getParameters().get(par.value()), clazz);
            }
            return null;
        }
    }

    private Object getTypeValue(String value, Class<?> clazz){
        if(clazz.isAssignableFrom(Optional.class))
            return Optional.fromNullable(value);
        else if(value == null)
            return null;
        else if(clazz == String.class)
            return value;
        else if(clazz == int.class || clazz == Integer.class)
            return Integer.parseInt(value);
        else if(clazz == float.class || clazz == Float.class)
            return Float.parseFloat(value);
        else if(clazz == double.class || clazz == Double.class)
            return Double.parseDouble(value);
        else if(clazz == boolean.class || clazz == Boolean.class)
            return getBoolean(value);
        else if(clazz == long.class || clazz == Long.class)
            return Long.parseLong(value);
        return value;
    }

    private boolean getBoolean(String value){
        value = value.toLowerCase();
        if(value.equals("1") || value.equals("yes")) return true;
        if(value.equals("0") || value.equals("no")) return false;
        return Boolean.parseBoolean(value);
    }

    private Arg getArgument(Annotation[] annotations){
        for(Annotation annotation : annotations)
            if(annotation.annotationType().isAssignableFrom(Arg.class))
                return (Arg) annotation;
        return null;
    }

    private Flag getFlag(Annotation[] annotations){
        for(Annotation annotation : annotations)
            if(annotation.annotationType().isAssignableFrom(Flag.class))
                return (Flag) annotation;
        return null;
    }

    private Par getParameter(Annotation[] annotations){
        for (Annotation annotation : annotations)
            if(annotation.annotationType().isAssignableFrom(Par.class))
                return (Par) annotation;
        return null;
    }

    public static IProvider getProvider() {
        return provider;
    }

    public static void setProvider(IProvider provider) {
        CommandBackend.provider = provider;
    }
}
