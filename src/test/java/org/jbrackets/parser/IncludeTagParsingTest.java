package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IncludeTagParsingTest extends TagTest {

    @Test
    public void testTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% include ../template.html%}");
	i.add("   {% include name %} some nested text");

	checkPass(i);

    }

    @Test
    public void testTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{% include ");
	checkFail(i);
    }

}
