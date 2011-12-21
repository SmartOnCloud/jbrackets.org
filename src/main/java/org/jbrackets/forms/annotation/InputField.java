package org.jbrackets.forms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.InputType;
import org.jbrackets.forms.renderer.InputFieldRenderer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = InputFieldRenderer.class)
public @interface InputField {

    /** The name attribute of the created field */
    String name() default "";

    /** The label value for the created field */
    String label() default "";

    /** The placeholder value for the created field */
    String placeholder() default "";

    /** Determines whether the value of required attribute */
    boolean required() default true;

    /** Determines whether a type for the created field */
    InputType type() default InputType.text;

    /** Determines whether a type can accept multiple values */
    boolean multiple() default false;

}
