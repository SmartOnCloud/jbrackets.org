package org.jbrackets.forms;

import static org.jbrackets.forms.Widget.INPUTTEXT;

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
@FormField(renderer = InputTextRenderer.class)
public @interface CharField {

    String name() default "";

    String label() default "";

    boolean required() default true;

    boolean hidden() default false;

    Widget widget() default INPUTTEXT;

}
