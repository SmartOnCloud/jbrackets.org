package org.jbrackets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.jbrackets.parser.ParseException;
import org.junit.Test;

public class TemplateEngineTest {
    TemplateEngine tested = new TemplateEngine();

    @Test
    public void testSimple() throws IOException, ParseException,
	    URISyntaxException {
	tested.process(getFilePath("/engine/inputOK.txt"),
		new HashMap<String, Object>());
    }

    @Test
    public void testParseException_Syntax_Javacc() throws IOException,
	    ParseException, URISyntaxException {
	try {
	    tested.process(getFilePath("/engine/inputErrorJavac.txt"),
		    new HashMap<String, Object>());
	    fail();
	} catch (ParseException pe) {
	    String expected = "Encountered \" <TAG_EXTENDS> \"{% extends \"\" at line 1, column 2";
	    BufferedReader ex = new BufferedReader(new StringReader(
		    pe.getMessage()));
	    assertThat(ex.readLine().endsWith("inputErrorJavac.txt"), is(true));
	    assertThat(ex.readLine().startsWith(expected), is(true));
	}
    }

    @Test
    public void testParseException_Syntax_Implemented() throws IOException,
	    ParseException, URISyntaxException {
	try {
	    tested.process(getFilePath("/engine/inputErrorJbracket.txt"),
		    new HashMap<String, Object>());
	    fail();
	} catch (ParseException pe) {
	    BufferedReader m = new BufferedReader(new StringReader(
		    pe.getMessage()));
	    assertThat(m.readLine().endsWith("inputErrorJbracket.txt"),
		    is(true));
	    assertThat(m.readLine().startsWith("expected {% endblock name %}"),
		    is(true));
	    assertThat(m.readLine().startsWith("found {% endblock name2 %}"),
		    is(true));
	    assertThat(m.readLine().startsWith("at line 5, column 18"),
		    is(true));
	}
    }

    @Test
    public void testElvisOperator() throws IOException, ParseException,
	    URISyntaxException {
	String process = tested.process(
		getFilePath("/engine/inputElvisOperator.txt"),
		new HashMap<String, Object>());
	assertThat(process, is(equalTo("alternative")));
    }

    private static String getFilePath(String fileName)
	    throws URISyntaxException {
	URL url = TemplateEngine.class.getResource(fileName);
	return new File(url.toURI()).getPath();
    }
}
