package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IfTagParsingTest extends TagTest {

    @Test
    public void testTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% if expr %} {% else %} {% endif %}");
	i.add("{% if expr %} sdas {% endif %}");
	i.add("{% if expr %} {%if expr%} {%endif %} {% endif %}");

	checkPass(i);
    }

    @Test
    public void testTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{% if expr %} {% else %} {% end if %}");
	i.add("{% if expr %} {%if expr%}  {% endif %}");
	i.add("{% if expr %} {% endif %} {% else %}");

	checkFail(i);

    }

}
