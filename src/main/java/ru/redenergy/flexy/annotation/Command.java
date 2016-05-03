package ru.redenergy.flexy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods, marked with this annotation will be considered as command
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /** Template for the command */
    String value() default "";

    /** Required permission to run this command, by default it's not required */
    String permission() default "#";

    /** if command disabled it will not be collected */
    boolean disabled() default false;

    /** whether or not this command should be displayed in /help output */
    boolean displayable() default true;
}
