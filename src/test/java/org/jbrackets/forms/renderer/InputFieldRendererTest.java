package org.jbrackets.forms.renderer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

import org.jbrackets.forms.FieldMetadata;
import org.jbrackets.forms.Form;
import org.jbrackets.forms.InputType;
import org.jbrackets.forms.annotation.FormField;
import org.jbrackets.forms.annotation.InputField;
import org.junit.Before;
import org.junit.Test;

public class InputFieldRendererTest extends InputFieldRenderer {

    InputFieldRenderer tested;
    Person form;

    @Before
    public void setUp() {
	tested = new InputFieldRenderer();
	form = new Person();
    }

    @Test
    public void testRenderField() throws Exception {

	assertThat(tested.renderField(form, createDescriptor(form, "name")),
		equalTo(String.format(InputFieldRenderer.FIELD_FORMAT, "text",
			"id_name", "name", "", "", "required='required'")));
	assertThat(tested.renderField(form, createDescriptor(form, "dob")),
		equalTo(String.format(InputFieldRenderer.FIELD_FORMAT, "date",
			"id_dob", "dob", "", "", "")));
	assertThat(tested.renderField(form, createDescriptor(form, "email")),
		equalTo(String.format(InputFieldRenderer.FIELD_FORMAT, "email",
			"id_email", "email", "", "Email address", "required='required'")));
    }

    @Test
    public void testRenderLabel() throws Exception {

	assertThat(tested.renderLabel(form, createDescriptor(form, "name")),
		equalTo(String.format(InputFieldRenderer.LABEL_FORMAT,
			"id_name", "name")));
	assertThat(tested.renderLabel(form, createDescriptor(form, "dob")),
		equalTo(String.format(InputFieldRenderer.LABEL_FORMAT,
			"id_dob", "Date of Birth")));
	assertThat(tested.renderLabel(form, createDescriptor(form, "email")),
		equalTo(String.format(InputFieldRenderer.LABEL_FORMAT,
			"id_email", "email")));
    }

    // Helpers ----------------------------------------------------------------

    class Person extends Form {
	@InputField
	public String name;
	@InputField(type = InputType.date, required = false, label = "Date of Birth")
	public Date dob;
	@InputField(type = InputType.email, required = true, placeholder = "Email address")
	public String email;
    }

    private FieldMetadata createDescriptor(Person form, String fieldName)
	    throws SecurityException, NoSuchFieldException {
	Field field = form.getClass().getField(fieldName);
	Annotation annotation = field.getAnnotations()[0];
	FormField markerAnnotation = annotation.annotationType().getAnnotation(
		FormField.class);
	Class<? extends Renderer> renderer = markerAnnotation.renderer();

	return new FieldMetadata(field, annotation, renderer);
    }

}
