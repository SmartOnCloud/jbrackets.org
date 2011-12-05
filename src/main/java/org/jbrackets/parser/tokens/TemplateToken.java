package org.jbrackets.parser.tokens;

public class TemplateToken extends BaseToken {

    private String templateName;

    public TemplateToken(String templateName) {
	this.templateName = templateName;
    }

    public String getTemplateName() {
	return templateName;
    }

    public String getTemplateClassName() {
	return getClassNameFromTemplateName(templateName);
    }

    public static String getClassNameFromTemplateName(String templateName) {
	return templateName.replace("../", "up_").replace("/", "_")
		.replace(".", "_").replace("-", "_");
    }

    @Override
    public String getInvocation() {
	return "";
    }

    @Override
    public String getImplementation() {
	StringBuilder s = new StringBuilder();

	s.append("public class ").append(getTemplateClassName())
		.append(" extends Block {\n");
	s.append("\tprotected void main() {\n");
	for (BaseToken tok : getTokens())
	    s.append(tok.getInvocation());
	s.append("\t};\n");
	for (BaseToken tok : getTokens()) {
	    s.append(tok.getImplementation());
	}
	s.append("}\n");
	return s.toString();
    }

}
