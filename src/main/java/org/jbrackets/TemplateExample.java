package org.jbrackets;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class TemplateExample {

    public static void main(String[] args) throws Exception {
	URL resource = TemplateExample.class.getResource("/template.html");
	String path = new File(resource.toURI().normalize()).getParent();
	HashMap<String, Object> ctx = new HashMap<String, Object>();
	ctx.put("jobs", Arrays.asList(new String[] { "job1", "job2", "job3" }));

	// --
	TemplateEngine templateEngine = new TemplateEngine();
	for (int i = 0; i < 1; i++) {
	    Date date = new Date();
	    String output = templateEngine.process(path + "/main/index.html",
		    ctx);
	    System.out.print(output);
	    System.out.println(new Date().getTime() - date.getTime() + "ms");
	}
    }
}