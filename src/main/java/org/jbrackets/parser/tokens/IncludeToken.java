package org.jbrackets.parser.tokens;

import static java.lang.String.format;
import static org.jbrackets.parser.tokens.TemplateToken.getClassNameFromTemplateName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncludeToken extends BaseToken {
    private static Logger log = LoggerFactory.getLogger(IncludeToken.class);

    private String template;

    public void setParam(String template) {
	if (log.isDebugEnabled())
	    log.debug("include:[" + template + "]");
	this.template = template;
    }

    @Override
    public String getInvocation() {
	return format("new %s().render(wr, ctx);\n",
		getClassNameFromTemplateName(template));
    }

    @Override
    public String getImplementation() {
	return "";
    }

}
