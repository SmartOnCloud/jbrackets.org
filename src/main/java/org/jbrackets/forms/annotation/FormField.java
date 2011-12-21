package org.jbrackets.forms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbrackets.forms.renderer.Renderer;

/**
 * @author michal.jemala
 */
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormField {

    Class<? extends Renderer> renderer();

}
