package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ExtendsTagParsingTest extends TagTest {

    @Test
    public void testTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% extends name %} some nested text");
	i.add("{% extends name %}");
	i.add("{% extends name %}");
	i.add("{% extends ../../name %}");

	checkPass(i);
    }

    @Test
    public void testTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("asd {% extends name asdas %}");
	i.add(" {% extends name %}");
	i.add("\t{% extends name %}");

	checkFail(i);

    }

}
