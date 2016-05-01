package ru.redenergy.flexy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional parameter of the command
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Par {
    String value();

    /**
     * Should be overwritten only in cases when type of parameter
     * is Optional so we're unable to get type of expected input
     *
     * Possible use case:
     * <code>
     *     @Command //Because T of Optional is erased after compilation you should declare expected type explicitly
     *     void cmd(@Par(value = "--p", type = int.class) Optional<Integer> val){
     *          //Do something with input
     *     }
     * </code>
     */
    Class type() default String.class;
}
