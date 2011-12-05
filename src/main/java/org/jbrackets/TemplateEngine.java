package org.jbrackets;

import static org.jbrackets.parser.tokens.TemplateToken.getClassNameFromTemplateName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.SimpleCompiler;
import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.TemplateParser;
import org.jbrackets.parser.tokens.Block;
import org.jbrackets.parser.tokens.TemplateToken;


/**
 * Thread-safe stateless implementation of template engine
 */
public class TemplateEngine {

    public TemplateEngine() {
    }

    public String process(String templateFileName, Map<String, Object> ctx)
	    throws IOException, ParseException {

	StringBuilder s = createOutputStringBuilder();
	File templateFile = new File(templateFileName);
	String templateName = getClassNameFromTemplateName(templateFile
		.getName());

	HashSet<String> alreadyProcessed = new HashSet<String>();
	processTeplate(templateFile, templateName, s, alreadyProcessed);

	try {
	    Block newInstance = compile(templateName, s.toString())
		    .newInstance();
	    StringWriter stringWriter = new StringWriter();
	    newInstance.render(new PrintWriter(stringWriter), ctx);
	    stringWriter.flush();
	    return stringWriter.toString();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private TemplateParser processTeplate(File templateFile,
	    String templateClassName, StringBuilder s,
	    HashSet<String> alreadyProcessed) throws IOException {
	try {
	    alreadyProcessed.add(templateClassName);
	    FileInputStream is = new FileInputStream(templateFile);
	    TemplateParser parser = new TemplateParser(is);
	    TemplateToken tok = parser.process(templateClassName);
	    s.append(tok.getImplementation());
	    List<String> templates = parser.templates;
	    for (String template : templates) {
		File file = new File(templateFile.getParent() + "/" + template);
		String templateClassToGenerate = getClassNameFromTemplateName(template);
		if (!alreadyProcessed.contains(templateClassToGenerate)) {
		    parser = processTeplate(file, templateClassToGenerate, s,
			    alreadyProcessed);
		}
	    }
	    return parser;
	} catch (ParseException e) {
	    throw new RuntimeException(templateFile.toString(), e);
	}
    }

    private StringBuilder createOutputStringBuilder() {
	StringBuilder s = new StringBuilder();
	s.append("import ").append(Block.class.getName()).append(";\n");
	s.append("import org.jbrackets.tags.*;\n\n");
	return s;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Block> compile(String fileName, String src)
	    throws ScanException, org.codehaus.janino.Parser.ParseException,
	    CompileException, IOException, ClassNotFoundException {
	SimpleCompiler simpleCompiler = new SimpleCompiler(fileName,
		new StringReader(src));
	ClassLoader classLoader = simpleCompiler.getClassLoader();
	Class<? extends Block> loadClass = (Class<? extends Block>) classLoader
		.loadClass(fileName);
	return loadClass;
    }
}
