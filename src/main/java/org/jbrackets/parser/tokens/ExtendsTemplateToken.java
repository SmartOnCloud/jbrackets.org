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
	StringBuilder s = new StringBuilder();

	s.append("public class ").append(getTemplateClassName())
		.append(" extends ").append(getParentClassName())
		.append(" {\n");

	for (BaseToken tok : getTokens()) {
	    s.append(tok.getImplementation());
	}
	s.append("}\n");
	return s.toString();
    }

}
