package org.jbrackets.parser.tokens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class TemplateToken extends BaseToken {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String templateName;

    public TemplateToken(String templateName) {
	Assert.notNull(templateName);
	this.templateName = templateName;
	if (log.isDebugEnabled())
	    log.debug("teamplate name:[" + templateName + "]");
    }

    public String getTemplateName() {
	return templateName;
    }

    public String getTemplateClassName() {
	return getClassNameFromTemplateName(templateName);
    }

    public static String getClassNameFromTemplateName(String templateName) {
	return templateName.replace("../", "up_").replace("/", "_")
		.replace(".", "_").replace("-", "_").toUpperCase();
    }

    @Override
    public String getInvocation() {
	return "";
    }

    @Override
    public String getImplementation() {
	return class_construct(getTemplateClassName(), Block.class.getName(),
		false, getTokens());
    }

}
