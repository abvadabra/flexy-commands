package ru.redenergy.flexy;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import ru.redenergy.flexy.annotation.Arg;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.Flag;
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
        Class[] parameters = method.getParameterTypes();
        Annotation[][] commandAnnotations = method.getParameterAnnotations();
        List<String> flags = new ArrayList<>();
        for(Annotation[] annotations: commandAnnotations)
            for(Annotation annotation: annotations)
                if(annotation instanceof Flag)
                    flags.add(((Flag) annotation).value());
        configs.add(new CommandConfiguration(method, parameters, commandAnnotations, flags));
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
        ResolveResult result = resolver.resolve(template, args, config.getAvailableFlags());
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
        Object[] parameters = new Object[command.getParameters().length];
        for(int i = 0; i < command.getParameters().length; i++){
            Object value = getAppropriateValue(command.getParameters()[i], command.getAnnotations()[i], sender, result);
            if(value == null)
                return null;
            else
                parameters[i] = value;
        }
        return parameters;
    }

    private Object getAppropriateValue(Class clazz, Annotation[] annotations, ICommandSender sender, ResolveResult result){
        if(clazz.isAssignableFrom(sender.getClass())){
            return sender;
        } else {
            Arg arg = getArgument(annotations);
            if (arg != null) {
                return getTypeValue(result.getArguments().get(arg.value()), clazz);
            }
            Flag flag = getFlag(annotations);
            if (flag != null && clazz.isAssignableFrom(boolean.class)) {
                return result.getFoundFlags().contains(flag.value());
            }
            return null;
        }
    }

    private Object getTypeValue(String value, Class clazz){
        if(clazz == String.class){
            return value;
        } else if(clazz == int.class){
            return Integer.parseInt(value);
        } else if(clazz == float.class){
            return Float.parseFloat(value);
        } else if(clazz == double.class){
            return Double.parseDouble(value);
        } else if(clazz == boolean.class){
            return getBoolean(value);
        } else if(clazz == long.class){
            return Long.parseLong(value);
        }
        return null;
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
}