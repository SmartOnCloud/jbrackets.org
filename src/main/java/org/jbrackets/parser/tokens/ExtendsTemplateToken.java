package org.jbrackets.parser.tokens;

public class ExtendsTemplateToken extends TemplateToken {
    private final String parentTemplate;

    public ExtendsTemplateToken(String templateName, String parentTemplate) {
	super(templateName);
	this.parentTemplate = parentTemplate;
    }

    private String getParentClassName() {
	return getClassNameFromTemplateName(parentTemplate);
    }

    @Override
    public String getInvocation() {
	return "";
    }

    @Override
    public String getImplementation() {
	return class_construct(getTemplateClassName(), getParentClassName(),
		false, getTokens());
    }
}
