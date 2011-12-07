package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ForTagParsingTest extends TagTest {

    @Test
    public void testTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% for i in col %} some nested text {% endfor %}");
	i.add("{% for in in in %} some nested text {% endfor %}");
	i.add("{% for in in ins%} some nested text {% endfor %}");
	i.add("\n\t\t{% for\n in\t in ins%} some nested text {% endfor %}");

	checkPass(i);
    }

    @Test
    public void testTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{% for i in col %} some nested text {% endfor ");
	i.add("{% for i i col %} some nested text {% endfor ");
	i.add("{% for asd %}{%endfor%}");
	i.add("{% for in in in %}{%endblock%}");

	checkFail(i);
    }
}
