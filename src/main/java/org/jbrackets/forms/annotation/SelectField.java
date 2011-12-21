package org.jbrackets.forms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.OptionProvider;
import org.jbrackets.forms.renderer.SelectFieldRenderer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = SelectFieldRenderer.class)
public @interface SelectField {

    /** The name attribute of the created field */
    String name() default "";

    /** The label value for the created field */
    String label() default "";

    /** Determines whether the value of required attribute */
    boolean required() default true;

    /** Determines whether the field can accept multiple options */
    boolean multiple() default false;

    /** Determines the size of visible options */
    int size() default 1;

    /** Determines if the possbile options should be prepended by a blank option */
    boolean blank() default false;

    /** Determines the possible select options */
    Class<? extends OptionProvider> options();

}
