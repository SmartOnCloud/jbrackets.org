package org.jbrackets;

import static java.lang.System.out;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class TemplateExample {

    public static void main(String[] args) throws Exception {
	URL resource = TemplateExample.class.getResource("/template.html");
	String path = new File(resource.toURI().normalize()).getParent();
	HashMap<String, Object> ctx = new HashMap<String, Object>();
	ctx.put("jobs", Arrays.asList(new String[] { "job1", "job2", "job3" }));

	// --
	TemplateEngine templateEngine = new TemplateEngine();
	templateEngine.process(path + "/template.html", ctx);
	templateEngine.process(path + "/main/index.html", ctx);
    }
}