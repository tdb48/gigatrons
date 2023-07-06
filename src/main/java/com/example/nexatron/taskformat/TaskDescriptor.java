package com.example.nexatron.taskformat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface TaskDescriptor
{
	String name() default "";

	int priority() default 0;

	boolean register() default false;

	boolean blocking() default false;

	boolean client() default false;

	boolean stoppable() default false;
}
