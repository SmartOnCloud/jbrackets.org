package org.jbrackets.forms;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author michal.jemala
 */
public class InputTextRenderer implements Renderer {
    public String stringTypeField;
    private TypeDescriptor targetType;

    public InputTextRenderer() throws SecurityException, NoSuchFieldException {
	targetType = new TypeDescriptor(
		InputTextRenderer.class.getField("stringTypeField"));
    }

    public String renderField(Form form, Annotation annotation, Field field) {
	CharField metadata = (CharField) annotation;
	if (getWidget(metadata, field).equals(Widget.TEXTAREA))
	    return format("<textarea  id='%s' name='%s'  >%s</textarea>",
		    getId(field), getName(metadata, field),
		    getValue(form, field));

	return format("<input type='text' id='%s' name='%s' value='%s' />",
		getId(field), getName(metadata, field), getValue(form, field));
    }

    public String renderLabel(Form form, Annotation annotation, Field field,
	    FieldError error) {
	CharField metadata = (CharField) annotation;
	if (error != null) {
	    return format(
		    "<label for='%s' class='error' title='%s'>%s</label>",
		    getId(field), error.getDefaultMessage(),
		    getLabel(metadata, field));
	}
	return format("<label for='%s'>%s</label>", getId(field),
		getLabel(metadata, field));
    }

    private String getLabel(CharField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    private String getId(Field field) {
	return "id_" + field.getName();
    }

    private Widget getWidget(CharField metadata, Field field) {
	return metadata.widget();
    }

    private String getName(CharField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

    private String getValue(Form form, Field field) {
	Object value = null;
	try {
	    ReflectionUtils.makeAccessible(field);
	    value = field.get(form);
	    FormattingConversionService formattingService = getFormattingConvertionService();
	    TypeDescriptor typeSrc = new TypeDescriptor(field);
	    Object convert = formattingService.convert(value, typeSrc,
		    targetType);
	    return convert == null ? "" : String.valueOf(convert);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	// return value != null ? String.valueOf(value) : "";
    }

    private FormattingConversionService getFormattingConvertionService() {
	return RequestContextUtils.getWebApplicationContext(
		((ServletRequestAttributes) RequestContextHolder
			.currentRequestAttributes()).getRequest()).getBean(
		FormattingConversionService.class);
    }

}
