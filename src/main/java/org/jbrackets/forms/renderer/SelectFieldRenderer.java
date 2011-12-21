package org.jbrackets.forms.renderer;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.Option;
import org.jbrackets.forms.OptionProvider;
import org.jbrackets.forms.annotation.SelectField;
import org.springframework.web.context.WebApplicationContext;

public class SelectFieldRenderer extends RendererSupport<SelectField> {

    private static final String FIELD_FORMAT = "<select id='%s' name='%s' size='%d' %s>%s</select>";
    private static final String OPTION_FORMAT = "<option %s value='%s'>%s</option>";
    private static final String LABEL_FORMAT = "<label for='%s'>%s</label>";

    @Override
    protected Class<SelectField> getAnnotationType() {
	return SelectField.class;
    }

    @Override
    public String renderField(Form form, FieldMetadata descriptor) {
	Field field = descriptor.getField();
	SelectField metadata = getAnnotation(descriptor);

	return format(FIELD_FORMAT, getId(field), getName(metadata, field),
		getSize(metadata), getAdditionalAttributes(metadata),
		getOptionMarkup(form, metadata, field));
    }

    @Override
    public String renderLabel(Form form, FieldMetadata descriptor) {
	Field field = descriptor.getField();
	SelectField metadata = getAnnotation(descriptor);

	return format(LABEL_FORMAT, getId(field), getLabel(metadata, field));
    }

    // Helpers ----------------------------------------------------------------
    protected String getName(SelectField metadata, Field field) {
	String name = metadata.name();
	return name.length() > 0 ? name : field.getName();
    }

    protected String getLabel(SelectField metadata, Field field) {
	String label = metadata.label();
	return label.length() > 0 ? label : field.getName();
    }

    protected int getSize(SelectField metadata) {
	return metadata.size();
    }

    protected String getAdditionalAttributes(SelectField metadata) {
	StringBuilder sb = new StringBuilder();

	if (metadata.required())
	    sb.append("required='required' ");

	if (metadata.multiple())
	    sb.append("multiple='multiple' ");

	return sb.toString();
    }

    protected String getOptionMarkup(Form form, SelectField metadata,
	    Field field) {
	StringBuilder sb = new StringBuilder();

	List<? extends Option> options = getOptions(metadata, field);
	for (Option o : options) {
	    Object fieldValue = getFieldValue(form, field);
	    String selected = o.standsFor(fieldValue) ? "selected='selected'"
		    : "";

	    String optionMarkup = format(OPTION_FORMAT, selected, o.value(),
		    o.label());
	    sb.append(optionMarkup);
	}

	return sb.toString();
    }

    protected List<? extends Option> getOptions(SelectField metadata,
	    Field field) {
	Class<? extends OptionProvider> optionsProvider = metadata.options();

	OptionProvider provider = lookupProvider(optionsProvider);
	if (provider == null)
	    provider = createProvider(optionsProvider);

	if (provider == null)
	    return new ArrayList<Option>();
	else
	    return provider.getOptions();
    }

    protected OptionProvider lookupProvider(
	    Class<? extends OptionProvider> providerClazz) {
	OptionProvider provider = null;

	try {
	    WebApplicationContext context = getWebApplicationContext();
	    if (context != null)
		provider = context.getBean(providerClazz);
	} catch (Exception e) {
	    // TODO logging
	    e.printStackTrace();
	}

	return provider;

    }

    protected OptionProvider createProvider(
	    Class<? extends OptionProvider> providerClazz) {
	OptionProvider provider = null;

	try {
	    provider = providerClazz.newInstance();
	} catch (Exception e) {
	    // TODO logging
	    e.printStackTrace();
	}

	return provider;
    }

}
