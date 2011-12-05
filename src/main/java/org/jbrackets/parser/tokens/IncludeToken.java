package org.jbrackets.parser.tokens;

public class IncludeToken extends BaseToken {

    private String template;

    public void setParam(String template) {
	this.template = template;
    }

    @Override
    public String getInvocation() {
	StringBuilder s = new StringBuilder();

	String classNameFromTemplateName = TemplateToken
		.getClassNameFromTemplateName(template);

	s.append("\t\tnew ").append(classNameFromTemplateName);
	s.append("().render(wr, ctx);\n");
	// new Block().render(wr, model);
	// s.append("new ForEachTag(this).iterate(\"job\", \"jobs\", Page2_ForLoop.class);");
	return s.toString();
    }

    @Override
    public String getImplementation() {
	return "";
    }

}
