package org.jbrackets;

import static org.jbrackets.parser.tokens.TemplateToken.getClassNameFromTemplateName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe stateless implementation of template engine
 */
public class TemplateEngine {

    private static Logger log = LoggerFactory.getLogger(TemplateEngine.class);

    private Date startGen;
    private Date startCompile;
    private Date endCompile;
    private Date endExecution;

    public TemplateEngine() {
    }

    public String process(String templateFileName, Map<String, Object> ctx)
	    throws IOException, ParseException {

	if (log.isDebugEnabled())
	    startGen = new Date();
	StringBuilder s = createOutputStringBuilder();
	File templateFile = new File(templateFileName);
	String templateName = getClassNameFromTemplateName(templateFile
		.getName());

	HashSet<String> alreadyProcessed = new HashSet<String>();
	processTeplate(templateFile, templateName, s, alreadyProcessed);
	s.append("// -------------------------------END GENERATED--------------------------------------------------------------------------\n");
	s.append("// ----------------------------------------------------------------------------------------------------------------------");
	if (log.isDebugEnabled())
	    startCompile = new Date();
	if (log.isTraceEnabled())
	    log.trace("generated source:\n" + s);

	try {
	    Block newInstance = compile(templateName, s.toString())
		    .newInstance();
	    if (log.isDebugEnabled())
		endCompile = new Date();
	    StringWriter stringWriter = new StringWriter();
	    newInstance.render(new PrintWriter(stringWriter), ctx);
	    stringWriter.flush();
	    if (log.isDebugEnabled())
		endExecution = new Date();
	    log.debug("generation [ms]: "
		    + (startCompile.getTime() - startGen.getTime()));
	    log.debug("compilation[ms]: "
		    + (endCompile.getTime() - startCompile.getTime()));
	    log.debug("execution  [ms]: "
		    + (endExecution.getTime() - endCompile.getTime()));
	    return stringWriter.toString();
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	} catch (ScanException e) {
	    throw new RuntimeException(e);
	} catch (org.codehaus.janino.Parser.ParseException e) {
	    throw new RuntimeException(e);
	} catch (CompileException e) {
	    throw new RuntimeException(e);
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }

    private TemplateParser processTeplate(File templateFile,
	    String templateClassName, StringBuilder s,
	    HashSet<String> alreadyProcessed) throws IOException,
	    ParseException {
	if (log.isTraceEnabled())
	    log.trace("processing template:" + templateFile.getPath());
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
		} else {
		    if (log.isTraceEnabled())
			log.trace("already processed template [ignoring]:"
				+ file.getPath());
		}
	    }
	    return parser;
	} catch (ParseException e) {
	    ParseException e2 = new ParseException(templateFile.getPath()
		    + "\n" + e.getMessage());
	    e2.setStackTrace(e.getStackTrace());
	    throw e2;
	}
    }

    private StringBuilder createOutputStringBuilder() {
	StringBuilder s = new StringBuilder();
	s.append("// ----------------------------------------------------------------------------------------------------------------------\n");
	s.append("// -----------------------------------GENERATED--------------------------------------------------------------------------\n");
	s.append("import ").append(Block.class.getName()).append(";\n");
	s.append("import org.jbrackets.tags.*;\n");
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

