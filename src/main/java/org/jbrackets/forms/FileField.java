package org.jbrackets.forms;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author michal.jemala
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = InputFileRenderer.class)
public @interface FileField {

    String name() default "";

    String label() default "";

    boolean required() default true;

}
