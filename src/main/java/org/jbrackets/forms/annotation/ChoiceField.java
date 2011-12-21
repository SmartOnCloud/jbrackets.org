package org.jbrackets.forms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.OptionProvider;
import org.jbrackets.forms.renderer.OptionListRenderer;

/**
 * @Deprecated use {@link SelectField} instead
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = OptionListRenderer.class)
@Deprecated
public @interface ChoiceField {

    String name() default "";

    String label() default "";

    boolean required() default true;

    boolean hidden() default false;

    /**
     * Option is selected if: option.toString() == fieldValue.toString()
     */
    Class<? extends OptionProvider> options();
}
