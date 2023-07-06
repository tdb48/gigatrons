package com.example.ChinBreakHandler.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigSection
{
    String name();

    String description();

    int position();

    boolean closedByDefault() default false;

    /*
    OpenOSRS Lazy Helpers tm
     */
    String keyName() default "";
    String section() default "";
    boolean hidden() default false;
    String unhide() default "";
}
