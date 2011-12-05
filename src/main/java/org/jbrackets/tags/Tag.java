package org.jbrackets.tags;

import java.io.PrintWriter;
import java.util.HashMap;

public class Tag {

    protected PrintWriter wr;
    protected HashMap<String, Object> ctx;

    public Tag(PrintWriter wr, HashMap<String, Object> ctx) {
	this.wr = wr;
	this.ctx = ctx;
    }
}