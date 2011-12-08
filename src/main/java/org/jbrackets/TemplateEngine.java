package org.jbrackets;

import static org.jbrackets.parser.tokens.TemplateToken.getClassNameFromTemplateName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.TemplateParser;
import org.jbrackets.parser.tokens.Block;
import org.jbrackets.parser.tokens.TemplateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;

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

    public String process(String templateFileName, String encoding,
	    Map<String, Object> ctx) throws IOException, ParseException {

	if (log.isDebugEnabled())
	    startGen = new Date();
	StringBuilder s = new StringBuilder();
	s.append("// ----------------------------------------------------------------------------------------------------------------------\n");
	s.append("// -----------------------------------GENERATED--------------------------------------------------------------------------\n");
	File templateFile = new File(templateFileName);
	String templateName = getClassNameFromTemplateName(templateFile
		.getName());
	

	HashSet<String> alreadyProcessed = new HashSet<String>();
	processTeplate(templateFile, encoding, templateName, s,
		alreadyProcessed, ctx);
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
	    if (log.isDebugEnabled()) {
		endExecution = new Date();
		log.debug("generation [ms]: "
			+ (startCompile.getTime() - startGen.getTime()));
		log.debug("compilation[ms]: "
			+ (endCompile.getTime() - startCompile.getTime()));
		log.debug("execution  [ms]: "
			+ (endExecution.getTime() - endCompile.getTime()));
	    }
	    return stringWriter.toString();
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	} catch (CompileException e) {
	    throw new RuntimeException(e);
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }

    private TemplateParser processTeplate(File templateFile, String encoding,
	    String templateClassName, StringBuilder s,
	    HashSet<String> alreadyProcessed, Map<String, Object> ctx)
	    throws ParseException {
	if (log.isTraceEnabled())
	    log.trace("processing template:" + templateFile.getPath());
	try {
	    alreadyProcessed.add(templateClassName);
	    FileInputStream is = new FileInputStream(templateFile);
	    TemplateParser parser = new TemplateParser(is, encoding);
	    TemplateToken tok = parser.process(templateClassName);
	    tok.setFilePath(templateFile.getPath());
	    s.append(tok.getImplementation());
	    List<String> templates = parser.getTemplate();
	    for (String template : templates) {
		template = String.valueOf(Block.eval(template, ctx)).trim();
		File file = new File(templateFile.getParent() + "/" + template);
		String templateClassToGenerate = getClassNameFromTemplateName(template);
		if (!alreadyProcessed.contains(templateClassToGenerate)) {
		    parser = processTeplate(file, encoding,
			    templateClassToGenerate, s, alreadyProcessed, ctx);
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
	} catch (SpelParseException e) {
	    ParseException e2 = new ParseException(templateFile.getPath()
		    + "\n" + e.getMessage());
	    e2.setStackTrace(e.getStackTrace());
	    throw e2;
	} catch (SpelEvaluationException e) {
	    ParseException e2 = new ParseException(templateFile.getPath()
		    + "\n" + e.getMessage());
	    e2.setStackTrace(e.getStackTrace());
	    throw e2;
	} catch (FileNotFoundException e) {
	    ParseException e2 = new ParseException(e.getMessage());
	    e2.setStackTrace(e.getStackTrace());
	    throw e2;
	}
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Block> compile(String fileName, String src)
	    throws CompileException, IOException, ClassNotFoundException {
	SimpleCompiler simpleCompiler = new SimpleCompiler(fileName,
		new StringReader(src));
	ClassLoader classLoader = simpleCompiler.getClassLoader();
	Class<? extends Block> loadClass = (Class<? extends Block>) classLoader
		.loadClass(fileName);
	return loadClass;
    }
}
