package com.example.ChinBreakHandler.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used with ConfigItem, describes valid int range for a config item.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Range
{
    int min() default 0;

    int max() default Integer.MAX_VALUE;
}
