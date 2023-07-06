package com.example.ChinBreakHandler.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigGroup
{
    /**
     * The key name of the config group used for storing configuration within the config group.
     * This should typically be a lowercase version of your plugin name, with all spaces removed.
     * <p>
     * For example, the {@code Grand Exchange} plugin uses the key name {@code grandexchange}.
     */
    String value();
}
