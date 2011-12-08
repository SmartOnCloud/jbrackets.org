package org.jbrackets.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SetTagParsingTest extends TagTest {

    @Test
    public void testBlockTag_Correct() throws Exception {
	// no exception should be thrown
	List<String> i = new ArrayList<String>();
	i.add("{% set name expression %} ");
	checkPass(i);
    }

    @Test
    public void testBlockTag_InCorrect() throws Exception {
	List<String> i = new ArrayList<String>();
	i.add("{% set name expression  ");
	checkFail(i);

    }
}
