package ru.redenergy.flexy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Arguments marked with this annotation will be perceived as a flag arguments of command
 * Type of argument always must be boolean
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Flag {

    String value();
}
