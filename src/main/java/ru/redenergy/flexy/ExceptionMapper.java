package ru.redenergy.flexy;

import com.google.common.collect.Maps;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.ExceptionHandler;
import ru.redenergy.flexy.annotation.Flag;
import ru.redenergy.flexy.annotation.Par;
import ru.redenergy.flexy.config.ExceptionHandlerConfiguration;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Usually used from a CommandBackend to find and call Exception Handlers for commands
 */
public class ExceptionMapper {

    private MethodHandles.Lookup lookup = MethodHandles.lookup();
    private Map<Class<? extends Throwable>, ExceptionHandlerConfiguration> handlers = Maps.newHashMap();

    public void handlesLookup(Class<? extends FlexyCommand> target) {
        for(Method method: target.getMethods())
            if(method.isAnnotationPresent(ExceptionHandler.class))
                saveHandle(method);
    }

    private void saveHandle(Method method) {
        ExceptionHandler exceptionAnn = method.getAnnotation(ExceptionHandler.class);
        if(handlers.containsKey(exceptionAnn.value()))
            throw new IllegalArgumentException("There is a duplicated exception handler for " + exceptionAnn.value());
        MethodHandle handle;
        try {
            handle = lookup.unreflect(method);
        } catch (IllegalAccessException e){
            throw new RuntimeException("Unable to gain access to exception handler " + method + " , try to change it to public.", e);
        }
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
        ExceptionHandlerConfiguration config = new ExceptionHandlerConfiguration(handle, commandParameters, commandAnnotations, flags, parameters, exceptionAnn.value());
        handlers.put(exceptionAnn.value(), config);
    }

    public ExceptionHandlerConfiguration getHandler(Class<? extends Throwable> exception){
        if(!handlers.containsKey(exception)){
            Class<?> superclass = exception.getSuperclass();
            while (superclass != null){
                ExceptionHandlerConfiguration handler = handlers.get(superclass);
                if(handler != null){
                    handlers.put(exception, handler); //to prevent further look ups which eventually will lead to the same value
                    return handler;
                }
                superclass = superclass.getSuperclass();
            }
            handlers.put(exception, null); //to prevent any further useless look ups for the same exception without result we just map that class to null
        }
        return handlers.get(exception);
    }

    public ExceptionHandlerConfiguration getHandler(Throwable exception){
        return getHandler(exception.getClass());
    }

}
