package org.jbrackets.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jbrackets.parser.TagTest.gr;
import static org.junit.Assert.assertThat;

import org.jbrackets.parser.tokens.TemplateToken;
import org.junit.Test;

public class TemplateParserTest {

    @Test
    public void testEmptyInput() throws Exception {
	TemplateParser parser = new TemplateParser(gr(""));
	assertThat(parser.process("name"), is(not(equalTo(null))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullTemplateName() throws Exception {
	TemplateParser parser = new TemplateParser(gr(""));
	assertThat(parser.process(null), is(not(equalTo(null))));
    }

    @Test
    public void testPlainText() throws Exception {
	TemplateParser parser = new TemplateParser(gr("this is sample text"));
	assertThat(parser.process(""), is(instanceOf(TemplateToken.class)));
    }

}
