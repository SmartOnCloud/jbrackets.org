/* Generated By:JavaCC: Do not edit this line. TemplateParser.java */
package org.jbrackets.parser;
import org.jbrackets.parser.tokens.*;
import java.util.*;

public class TemplateParser implements TemplateParserConstants {
  List < String > definedBlocks = new ArrayList < String > ();
  List < String > templates = new ArrayList < String > ();

  public List < String > getTemplate()
  {
    return templates;
  }

// --------- parser ----------  final public TemplateToken process(String templateName) throws ParseException {
  definedBlocks = new ArrayList < String > ();
  templates = new ArrayList < String > ();
  TemplateToken tok = new TemplateToken(templateName);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TAG_EXTENDS:
      tok = extends_tag(templateName);
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    body(tok);
    jj_consume_token(0);
    {if (true) return tok;}
    throw new Error("Missing return statement in function");
  }

  final public ExtendsTemplateToken extends_tag(String templateName) throws ParseException {
  String parentTemplate;
    jj_consume_token(TAG_EXTENDS);
    parentTemplate = tag_param();
    jj_consume_token(END_BR);
    templates.add(parentTemplate);
    {if (true) return new ExtendsTemplateToken(templateName, parentTemplate);}
    throw new Error("Missing return statement in function");
  }

  final public void body(BaseToken tok) throws ParseException {
  BaseToken child;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EXP_LFT:
      case TAG_BLOCK:
      case TAG_SET:
      case TAG_FOR:
      case TAG_IF:
      case TAG_INCLUDE:
      case TAG_COMMENT_START:
      case CHARACTER:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAG_COMMENT_START:
        block_comment();
        break;
      case CHARACTER:
        jj_consume_token(CHARACTER);
      tok.appendText(token.image);
        break;
      case EXP_LFT:
        child = expr();
      tok.addToken(child);
        break;
      case TAG_SET:
        child = set_tag();
      tok.addToken(child);
        break;
      case TAG_BLOCK:
        child = block_tag();
      tok.addToken(child);
        break;
      case TAG_FOR:
        child = for_tag();
      tok.addToken(child);
        break;
      case TAG_IF:
        child = if_tag();
      tok.addToken(child);
        break;
      case TAG_INCLUDE:
        child = include_tag();
      tok.addToken(child);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void block_comment() throws ParseException {
    jj_consume_token(TAG_COMMENT_START);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(CHARACTER);
    }
    jj_consume_token(TAG_COMMENT_END);
  }

  final public BlockToken block_tag() throws ParseException {
  String startblock_Name;
  String endblock_Name;
  BlockToken block = new BlockToken();
    jj_consume_token(TAG_BLOCK);
    startblock_Name = tag_param();
    block.setName(startblock_Name);
    jj_consume_token(END_BR);
    body(block);
    jj_consume_token(TAG_ENDBLOCK);
    endblock_Name = opt_tag_param();
    jj_consume_token(END_BR);
    if (!endblock_Name.isEmpty() && !startblock_Name.equals(endblock_Name))  // check if endblock name is the same as startblock
    {if (true) throw new ParseException("expected {% endblock " + startblock_Name + " %}\u005cnfound {% endblock " + endblock_Name + " %}\u005cnat line " + token.beginLine + ", column " + token.beginColumn);}
    if (definedBlocks.contains(startblock_Name))
    {
      {if (true) throw new ParseException("duplicate block definition {% block " + startblock_Name + " %}");}
    }
    definedBlocks.add(startblock_Name);
    {if (true) return block;}
    throw new Error("Missing return statement in function");
  }

  final public ForLoopToken for_tag() throws ParseException {
  String param;
  ForLoopToken tok;
    jj_consume_token(TAG_FOR);
    tok = new ForLoopToken(token);
    param = tag_param();
    jj_consume_token(END_BR);
    body(tok);
    jj_consume_token(TAG_ENDFOR);
    tok.setParam(param);
    {if (true) return tok;}
    throw new Error("Missing return statement in function");
  }

  final public IncludeToken include_tag() throws ParseException {
  String param;
  IncludeToken tok;
    jj_consume_token(TAG_INCLUDE);
    tok = new IncludeToken();
    param = tag_param();
    jj_consume_token(END_BR);
    tok.setParam(param);
    templates.add(param);
    {if (true) return tok;}
    throw new Error("Missing return statement in function");
  }

  final public SetToken set_tag() throws ParseException {
  String param;
  SetToken tok;
    jj_consume_token(TAG_SET);
    tok = new SetToken(token);
    param = tag_param();
    jj_consume_token(END_BR);
    tok.setParam(param);
    {if (true) return tok;}
    throw new Error("Missing return statement in function");
  }

  final public IfToken if_tag() throws ParseException {
  String param;
  IfToken tok;
    jj_consume_token(TAG_IF);
    tok = new IfToken(token);
    param = tag_param();
    jj_consume_token(END_BR);
    body(tok.getIf());
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TAG_ELSE:
      jj_consume_token(TAG_ELSE);
      body(tok.getElse());
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    jj_consume_token(TAG_ENDIF);
    tok.setParam(param);
    {if (true) return tok;}
    throw new Error("Missing return statement in function");
  }

  final public ExpToken expr() throws ParseException {
  StringBuilder expr = new StringBuilder();
  Token tok;
    jj_consume_token(EXP_LFT);
    tok = token;
    label_3:
    while (true) {
      jj_consume_token(CHARACTER);
      expr.append(token.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_3;
      }
    }
    jj_consume_token(EXP_RGH);
    {if (true) return new ExpToken(expr.toString(), tok);}
    throw new Error("Missing return statement in function");
  }

// --- various helpers   final public String tag_param() throws ParseException {
  StringBuilder val = new StringBuilder();
    label_4:
    while (true) {
      jj_consume_token(CHARACTER);
      val.append(token.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_4;
      }
    }
    {if (true) return val.toString().trim();}
    throw new Error("Missing return statement in function");
  }

  final public String opt_tag_param() throws ParseException {
  StringBuilder val = new StringBuilder();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_5;
      }
      jj_consume_token(CHARACTER);
      val.append(token.image);
    }
    {if (true) return val.toString().trim();}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public TemplateParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[8];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x40,0x26598,0x26598,0x20000,0x800,0x20000,0x20000,0x20000,};
   }

  /** Constructor with InputStream. */
  public TemplateParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public TemplateParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new TemplateParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public TemplateParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new TemplateParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public TemplateParser(TemplateParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(TemplateParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[19];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 8; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 19; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
