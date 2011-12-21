package org.jbrackets.forms.renderer;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.Option;
import org.jbrackets.forms.OptionProvider;
import org.jbrackets.forms.annotation.ChoiceField;
import org.springframework.validation.FieldError;

/**
 * @deprecated use {@link SelectFieldRenderer}
 */
public class OptionListRenderer extends RendererSupport<ChoiceField> {

    @Override
    protected Class<ChoiceField> getAnnotationType() {
	return ChoiceField.class;
    }

    @Override
    public String renderField(Form form, FieldMetadata metadata) {
	return renderField(form, metadata.getAnnotation(), metadata.getField());
    }

    @Override
    public String renderLabel(Form form, FieldMetadata metadata) {
	return renderLabel(form, metadata.getAnnotation(), metadata.getField(),
		metadata.getError());
    }

    @Deprecated
    public String renderField(Form form, Annotation annotation, Field field) {
	try {
	    ChoiceField metadata = ChoiceField.class.cast(annotation);
	    StringBuilder sb = new StringBuilder();
	    sb.append(format("<select id='%s' name='%s'>", getId(field),
		    getName(metadata, field)));
	    for (Option option : getOptions(metadata, field)) {
		String selected = option.standsFor(getFieldValue(form, field)) ? "selected"
			: "";
		sb.append(format("<option %s value='%s'>%s</option>", selected,
			option.value(), option.label()));
	    }
	    sb.append("</select>");
	    return sb.toString();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Deprecated
    public String renderLabel(Form form, Annotation annotation, Field field,
	    FieldError error) {
	ChoiceField metadata = ChoiceField.class.cast(annotation);
	if (error != null) {
	    return format(
		    "<label for='%s' class='error' title='%s'>%s</label>",
		    getId(field), error.getDefaultMessage(),
		    getLabel(metadata, field));
	}
	return format("<label for='%s'>%s</label>", getId(field),
		getLabel(metadata, field));
    }

    protected String getLabel(ChoiceField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    protected String getName(ChoiceField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

    protected List<? extends Option> getOptions(ChoiceField metadata,
	    Field field) throws Exception {
	Class<? extends OptionProvider> optionsProvider = metadata.options();
	return optionsProvider.newInstance().getOptions();
    }

}
