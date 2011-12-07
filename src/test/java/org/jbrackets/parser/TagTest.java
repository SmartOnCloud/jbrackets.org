package org.jbrackets.parser;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;

public abstract class TagTest {

    protected void checkPass(List<String> i) throws ParseException {
	for (String string : i) {
	    TemplateParser parser = new TemplateParser(TagTest.gr(string));
	    parser.process("name");
	}
    }

    protected void checkFail(List<String> i) {
	for (String string : i)
	    try {
		new TemplateParser(TagTest.gr(string)).process("name");
		Assert.fail("should fail: " + string);
	    } catch (ParseException e) {
		// exception is expected
	    }
    }

    public static StringReader gr(String s) {
	return new StringReader(s);
    }
}
