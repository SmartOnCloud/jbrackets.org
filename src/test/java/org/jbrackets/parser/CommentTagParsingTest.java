package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CommentTagParsingTest extends TagTest {

    @Test
    public void testTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{# asdd any text inside #}");
	i.add("{# asdd any text inside even {% %} #}");
	checkPass(i);
    }

    @Test
    public void testTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{# asdd any text {# inside #}");
	checkFail(i);
    }
}