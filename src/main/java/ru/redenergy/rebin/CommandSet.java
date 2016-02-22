package ru.redenergy.rebin;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import ru.redenergy.rebin.annotation.Arg;
import ru.redenergy.rebin.annotation.Command;
import ru.redenergy.rebin.resolve.ResolveResult;
import ru.redenergy.rebin.resolve.TemplateResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CommandSet extends CommandBase {

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
        for(CommandConfiguration configuration : configs){
            String template = configuration.getCommandMethod().getAnnotation(Command.class).value();
            ResolveResult result = resolver.resolve(template, args);
            if(result.isSuccess()) {
                invokeCommand(configuration, result.getArguments(), sender, args);
            }
        }
    }
    
    private void invokeCommand(CommandConfiguration command, Map<String, String> extracted, ICommandSender sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        command.getCommandMethod().invoke(this, commandArguments(command, sender, extracted));
    }

    private Object[] commandArguments(CommandConfiguration command, ICommandSender sender, Map<String, String> args){
        Object[] parameters = new Object[command.getParameters().length];
        for(int i = 0; i < command.getParameters().length; i++){
            Class clazz = command.getParameters()[i];
            if(clazz.isAssignableFrom(ICommandSender.class)){
                parameters[i] = sender;
            } else {
                Arg arg = findArgumentAnnotation(command.getAnnotations()[i]);
                if(arg == null){
                    throw new RuntimeException("Unable to resolve arguments for " + command.getCommandMethod());
                }
                parameters[i] = args.get(arg.value());
            }
        }
        return parameters;
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
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            resolveAndInvoke(sender, args);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}