package ru.redenergy.rebin;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.redenergy.rebin.annotation.Arg;
import ru.redenergy.rebin.annotation.Command;
import ru.redenergy.rebin.resolve.ResolveResult;
import ru.redenergy.rebin.resolve.TemplateResolver;
import ru.skymine.permissions.Permissions;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CommandSet extends CommandBase {

    //TODO: Read message from external file
    public static String NO_PERMISSION_MSG = TextFormatting.RED + "\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432\u0021";
    public static String UNABLE_TO_PROCESS = TextFormatting.RED + "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c \u043a\u043e\u043c\u0430\u043d\u0434\u0443\u0021";
    public static String EXCEPTION = TextFormatting.RED + "\u0412\u043e \u0432\u0440\u0435\u043c\u044f \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u043e\u0448\u0438\u0431\u043a\u0430\u002e";

    private final TemplateResolver resolver = new TemplateResolver();
    private List<CommandConfiguration> configs = new ArrayList<>();

    public void collectCommands(){
        Method[] methods = this.getClass().getMethods();
        for(Method m : methods) {
            Command command = m.getAnnotation(Command.class);
            if(command != null) configs.add(new CommandConfiguration(m, m.getParameterTypes(), m.getParameterAnnotations()));
        }
    }

    private void resolveAndInvoke(ICommandSender sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        boolean executed = false;
        for(CommandConfiguration configuration : configs){
            String template = configuration.getCommandMethod().getAnnotation(Command.class).value();
            ResolveResult result = resolver.resolve(template, args);
            if(result.isSuccess()) {
                executed = true;
                Command command = configuration.getCommandMethod().getAnnotation(Command.class);
                if(command.permission().equals("#") || sender instanceof MinecraftServer || Permissions.hasPermission(sender.getName(), command.permission())) {
                    invokeCommand(configuration, result.getArguments(), sender, args);
                } else {
                    sender.addChatMessage(new TextComponentString(NO_PERMISSION_MSG));
                }
            }
        }
        if(!executed)
            sender.addChatMessage(new TextComponentString(UNABLE_TO_PROCESS));
    }
    
    private void invokeCommand(CommandConfiguration command, Map<String, String> extracted, ICommandSender sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        Object[] arguments = commandArguments(command, sender, extracted);
        if (arguments != null) {
            command.getCommandMethod().invoke(this, commandArguments(command, sender, extracted));
        } else {
            sender.addChatMessage(new TextComponentString(UNABLE_TO_PROCESS));
        }
    }

    private Object[] commandArguments(CommandConfiguration command, ICommandSender sender, Map<String, String> args){
        Object[] parameters = new Object[command.getParameters().length];
        for(int i = 0; i < command.getParameters().length; i++){
            Class clazz = command.getParameters()[i];
            if(clazz.isAssignableFrom(sender.getClass())){
                parameters[i] = sender;
            } else {
                Arg arg = findArgumentAnnotation(command.getAnnotations()[i]); // <|*|>
                if(arg == null){
                    return null; //unable to process it, most possibly because command is limited to sender type, e.g. Console tried to access in-game only command
                }
                parameters[i] = extractValueByType(args.get(arg.value()), clazz);
            }
        }
        return parameters;
    }

    private Object extractValueByType(String value, Class clazz){
        if(clazz.isAssignableFrom(String.class)){
            return value;
        } else if(clazz.isAssignableFrom(int.class)){
            return Integer.parseInt(value);
        } else if(clazz.isAssignableFrom(float.class)){
            return Float.parseFloat(value);
        } else if(clazz.isAssignableFrom(double.class)){
            return Double.parseDouble(value);
        } else if(clazz.isAssignableFrom(boolean.class)){
            return parseComplicatedBoolean(value);
        } else if(clazz.isAssignableFrom(long.class)){
            return Long.parseLong(value);
        }
        return null;
    }

    private boolean parseComplicatedBoolean(String value){
        if(value.equals("1") || value.equals("yes")) return true;
        if(value.equals("0") || value.equals("no")) return false;
        return Boolean.parseBoolean(value);
    }

    private Arg findArgumentAnnotation(Annotation[] annotations){
        for(Annotation annotation : annotations){
            if(annotation.annotationType().isAssignableFrom(Arg.class)){
                return (Arg) annotation;
            }
        }
        return null;
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            resolveAndInvoke(sender, args);
        } catch (Exception exception){
            sender.addChatMessage(new TextComponentString(EXCEPTION));
            exception.printStackTrace();
        }
    }

    public static void register(CommandSet set){
        set.collectCommands();
        ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(set);
    }

}