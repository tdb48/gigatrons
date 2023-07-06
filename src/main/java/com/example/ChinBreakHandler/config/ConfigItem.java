package com.example.ChinBreakHandler.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigItem
{
    int position() default -1;

    String keyName();

    String name();

    String description();

    boolean hidden() default false;

    String warning() default "";

    boolean secret() default false;

    String section() default "";

    String title() default "";

    boolean parse() default false;

    Class<?> clazz() default void.class;

    String method() default "";

    String unhide() default "";

    String unhideValue() default "";

    String hide() default "";

    String hideValue() default "";

    String enabledBy() default "";

    String enabledByValue() default "";

    String disabledBy() default "";

    String disabledByValue() default "";

    /**
     * Use this to indicate the enum class that is going to be used in the multiple select config.
     * This implementation made debugging problems with multiple selects a lot easier
     *
     * 	@return The Enum that will be used for the multiple select
     */
    Class<? extends Enum> enumClass() default Enum.class;
}

