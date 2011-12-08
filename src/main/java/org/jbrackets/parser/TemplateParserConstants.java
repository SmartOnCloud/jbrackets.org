/* Generated By:JavaCC: Do not edit this line. TemplateParserConstants.java */
package org.jbrackets.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface TemplateParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SPACE = 1;
  /** RegularExpression Id. */
  int BR_LFT = 2;
  /** RegularExpression Id. */
  int EXP_LFT = 3;
  /** RegularExpression Id. */
  int TAG_BLOCK = 4;
  /** RegularExpression Id. */
  int TAG_ENDBLOCK = 5;
  /** RegularExpression Id. */
  int TAG_EXTENDS = 6;
  /** RegularExpression Id. */
  int TAG_SET = 7;
  /** RegularExpression Id. */
  int TAG_FOR = 8;
  /** RegularExpression Id. */
  int TAG_ENDFOR = 9;
  /** RegularExpression Id. */
  int TAG_IF = 10;
  /** RegularExpression Id. */
  int TAG_ELSE = 11;
  /** RegularExpression Id. */
  int TAG_ENDIF = 12;
  /** RegularExpression Id. */
  int TAG_INCLUDE = 13;
  /** RegularExpression Id. */
  int TAG_COMMENT_START = 14;
  /** RegularExpression Id. */
  int TAG_COMMENT_END = 15;
  /** RegularExpression Id. */
  int EXP_RGH = 16;
  /** RegularExpression Id. */
  int CHARACTER = 17;
  /** RegularExpression Id. */
  int END_BR = 18;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int EXP = 1;
  /** Lexical state. */
  int PARAM = 2;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<SPACE>",
    "<BR_LFT>",
    "\"{{\"",
    "<TAG_BLOCK>",
    "<TAG_ENDBLOCK>",
    "<TAG_EXTENDS>",
    "<TAG_SET>",
    "<TAG_FOR>",
    "<TAG_ENDFOR>",
    "<TAG_IF>",
    "<TAG_ELSE>",
    "<TAG_ENDIF>",
    "<TAG_INCLUDE>",
    "\"{#\"",
    "\"#}\"",
    "<EXP_RGH>",
    "<CHARACTER>",
    "<END_BR>",
  };

}
