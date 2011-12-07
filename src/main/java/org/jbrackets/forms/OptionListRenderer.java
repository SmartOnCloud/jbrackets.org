package org.jbrackets.forms;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.jbrackets.forms.OptionProvider.Option;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.FieldError;

/**
 * @author michal.jemala
 */
public class OptionListRenderer implements Renderer {

    public String renderField(Form form, Annotation annotation, Field field) {
	try {
	    ChoiceField metadata = ChoiceField.class.cast(annotation);
	    StringBuilder sb = new StringBuilder();
	    sb.append(format("<select id='%s' name='%s'>", getId(field),
		    getName(metadata, field)));
	    for (Option option : getOptions(metadata, field)) {
		String selected = option.standsFor(getValue(form, field)) ? "selected"
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

    private String getLabel(ChoiceField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    private String getId(Field field) {
	return "id_" + field.getName();
    }

    private String getName(ChoiceField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

    private List<? extends Option> getOptions(ChoiceField metadata, Field field)
	    throws Exception {
	return metadata.options().newInstance().getOptions();
    }

    private Object getValue(Form form, Field field) {
	try {
	    ReflectionUtils.makeAccessible(field);
	    return field.get(form);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
