package org.jbrackets.parser.tokens;

import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.Token;
import org.junit.Assert;
import org.junit.Test;

public class SetTokenTest {

    SetToken tested = new SetToken(new Token());

    @Test
    public void test() throws Exception {
	tested.setParam("var some_expression");
	tested.setParam("var \tsome_expresson.\nwhateever");
	tested.setParam("var\n some_expresson.\nwhateever");
	tested.setParam("_123varZ\n some_expresson.\nwhateever");
	tested.setParam("_123varZ\n some_expresson.\nwhateever");
    }

    @Test
    public void missing_value() {
	try {
	    tested.setParam("var");
	    Assert.fail();
	} catch (ParseException e) {
	    // ok
	}
    }
    @Test
    public void missing_all() {
	try {
	    tested.setParam("");
	    Assert.fail();
	} catch (ParseException e) {
	    // ok
	}
    }

}
