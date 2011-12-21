package org.jbrackets.forms.annotation;

import static org.jbrackets.forms.Widget.INPUTTEXT;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.Widget;
import org.jbrackets.forms.renderer.InputTextRenderer;

/**
 * @deprecated use {@link InputField} instead
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FormField(renderer = InputTextRenderer.class)
@Deprecated
public @interface CharField {

    String name() default "";

    String label() default "";

    boolean required() default true;

    boolean hidden() default false;

    Widget widget() default INPUTTEXT;

}
