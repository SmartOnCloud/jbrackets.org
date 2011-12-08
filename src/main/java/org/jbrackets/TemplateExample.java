package org.jbrackets;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import org.jbrackets.parser.tokens.MapFailoverAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TemplateExample {

    public static void main(String[] args) throws Exception {
	URL resource = TemplateExample.class.getResource("/template.html");
	String path = new File(resource.toURI().normalize()).getParent();
	HashMap<String, Object> ctx = new HashMap<String, Object>();
	ctx.put("jobs", Arrays.asList(new String[] { "job1", "job2", "job3" }));

	// --
	TemplateEngine templateEngine = new TemplateEngine();
	StandardEvaluationContext context = createEvalContext();

	templateEngine.process(path + "/template.html", "UTF-8", context, ctx);
	templateEngine
		.process(path + "/main/index.html", "UTF-8", context, ctx);
    }

    private static StandardEvaluationContext createEvalContext() {
	StandardEvaluationContext context = new StandardEvaluationContext();
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapFailoverAccessor());
	return context;
    }

    // ----------------
}