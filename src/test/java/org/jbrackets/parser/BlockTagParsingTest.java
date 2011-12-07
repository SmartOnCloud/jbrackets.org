package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BlockTagParsingTest extends TagTest {

    @Test
    public void testBlockTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% block name %} some nested text {% endblock name%}");
	i.add("{%block name%} {%   %} {%endblock%}");
	i.add("{% \n           block \t\t\t\n name%} \t\t\t\t\n {%\n\r\t\tendblock\n\n%}");
	i.add("{% block name %} {%endblock name%}");
	i.add("{% block name %} some nested text {%endblock name%}");
	i.add("{% block name %} some nested {% block block_2 %} text  {%endblock block_2%} {%endblock name%}");
	i.add("{% block name %} some nested {% block block_2 %} text  {%endblock block_2%} {%endblock name%}");

	checkPass(i);
    }

    @Test
    public void testBlockTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{% block name %}");
	i.add("{% block name %} asdas {% ");
	i.add("{% block name %} {% endblock name2 %}");
	i.add("{% block name %} {% block name2 %} {% endblock %}");

	checkFail(i);

    }
}
