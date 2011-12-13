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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.SimpleCompiler;
import org.jbrackets.parser.ParseException;
import org.jbrackets.parser.TemplateParser;
import org.jbrackets.parser.tokens.Block;
import org.jbrackets.parser.tokens.MapFailoverAccessor;
import org.jbrackets.parser.tokens.TemplateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Thread-safe stateless implementation of template engine
 */
public class TemplateEngine {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Date startGen;
    private Date startCompile;
    private Date endCompile;
    private Date endExecution;

    private final String encoding;

    private StandardEvaluationContext evalContext;

    public TemplateEngine() {
	this("UTF-8");
    }

    public TemplateEngine(String encoding) {
	this.encoding = encoding;
    }

    private StandardEvaluationContext createEvalContext() {
	StandardEvaluationContext context = new StandardEvaluationContext();
	context.addPropertyAccessor(new ReflectivePropertyAccessor());
	context.addPropertyAccessor(new BeanFactoryAccessor());
	context.addPropertyAccessor(new MapFailoverAccessor());
	return context;
    }

    public String process(String templateFileName) throws ParseException {
	return process(templateFileName, encoding, getEvalContext(),
		new HashMap<String, Object>());
    }

    public StandardEvaluationContext getEvalContext() {
	if (evalContext == null) {
	    evalContext = createEvalContext();
	}
	return evalContext;
    }

    public void setEvalContext(StandardEvaluationContext evalContext) {
	this.evalContext = evalContext;
    }

    public String process(String templateFileName, Map<String, Object> ctx)
	    throws ParseException {
	return process(templateFileName, encoding, getEvalContext(), ctx);
    }

    public String process(String templateFileName, String encoding,
	    EvaluationContext context, Map<String, Object> ctx)
	    throws ParseException {

	if (log.isDebugEnabled())
	    startGen = new Date();
	StringBuilder s = new StringBuilder();
	s.append("// ----------------------------------------------------------------------------------------------------------------------\n");
	s.append("// -----------------------------------GENERATED--------------------------------------------------------------------------\n");
	File templateFile = new File(templateFileName);
	String templateName = getClassNameFromTemplateName(templateFile
		.getName());

	processTeplate(templateFile, encoding, templateName, s,
		new HashSet<String>(), ctx);
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
	    newInstance.setEvalContext(context);
	    newInstance.render(new PrintWriter(stringWriter), ctx);
	    stringWriter.flush();
	    if (log.isDebugEnabled()) {
		logPerformace();
	    }
	    return stringWriter.toString();
	} catch (ParseException e) {
	    log.error("error", e);
	    throw e;
	} catch (SpelParseException e) {
	    throw convertException(templateFile, e);
	} catch (SpelEvaluationException e) {
	    throw convertException(templateFile, e);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private void logPerformace() {
	endExecution = new Date();
	log.debug("generation [ms]: "
		+ (startCompile.getTime() - startGen.getTime()));
	log.debug("compilation[ms]: "
		+ (endCompile.getTime() - startCompile.getTime()));
	log.debug("execution  [ms]: "
		+ (endExecution.getTime() - endCompile.getTime()));
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
		template = String.valueOf(eval(template, ctx)).trim();
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
	    throw convertException(templateFile, e);
	} catch (SpelParseException e) {
	    throw convertException(templateFile, e);
	} catch (SpelEvaluationException e) {
	    throw convertException(templateFile, e);
	} catch (FileNotFoundException e) {
	    throw convertException(templateFile, e);
	}
    }

    private ParseException convertException(File templateFile, Exception e)
	    throws ParseException {
	log.error("error", e);
	ParseException e2 = new ParseException(templateFile.getPath() + "\n"
		+ e.getMessage());
	e2.setStackTrace(e.getStackTrace());
	return e2;
    }

    private ParseException convertException(Exception e) throws ParseException {
	log.error("error", e);
	ParseException e2 = new ParseException(e.getMessage());
	e2.setStackTrace(e.getStackTrace());
	return e2;
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

    private Object eval(String expr, Object ctx) throws ParseException {
	SpelExpressionParser parser = new SpelExpressionParser();
	Expression parseExpression = parser.parseExpression(expr);
	return parseExpression.getValue(getEvalContext(), ctx);
    }

    public String processString(String template, HashMap<String, Object> ctx)
	    throws ParseException {
	try {
	    TemplateParser parser = new TemplateParser(new StringReader(
		    template));
	    TemplateToken tok = parser.process("TEMPLATE");
	    if (parser.getTemplate().size() > 0) {
		throw new ParseException(
			"using {% extends %} and {% include %} tags is not permitted in String templates");
	    }
	    Block newInstance = compile("TEMPLATE", tok.getImplementation())
		    .newInstance();

	    StringWriter stringWriter = new StringWriter();
	    newInstance.setEvalContext(getEvalContext());
	    newInstance.render(new PrintWriter(stringWriter), ctx);
	    stringWriter.flush();

	    return stringWriter.toString();
	} catch (ParseException e) {
	    throw e;
	} catch (Exception e) {
	    throw convertException(e);
	}
    }
}
