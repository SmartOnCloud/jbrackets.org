package org.jbrackets.forms.renderer;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;

public interface Renderer {

    String renderField(Form form, FieldMetadata metadata);

    String renderLabel(Form form, FieldMetadata metadata);

}
