package org.jbrackets.forms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.renderer.InputFileRenderer;

/**
 * @deprecated use {@link InputField} instead
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = InputFileRenderer.class)
@Deprecated
public @interface FileField {

    String name() default "";

    String label() default "";

    boolean required() default true;
    
    String placeholder() default "";
    
    boolean multiple() default false;

}
