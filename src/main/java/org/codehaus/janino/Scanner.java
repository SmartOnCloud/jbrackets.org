
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ICookable;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.util.TeeReader;


/**
 * Splits up a character stream into tokens and returns them as
 * {@link java.lang.String String} objects.
 * <p>
 * The <code>optionalFileName</code> parameter passed to many
 * constructors should point
 */

public class Scanner {

    // Public Scanners that read from a file.

    /**
     * Set up a scanner that reads tokens from the given file in the default charset.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated
     */
    public Scanner(String fileName) throws CompileException, IOException {
        this(
            fileName,                     // optionalFileName
            new FileInputStream(fileName) // is
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the given encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated
     */
    public Scanner(String fileName, String encoding) throws CompileException, IOException {
        this(
            fileName,                      // optionalFileName
            new FileInputStream(fileName), // is
            encoding                       // optionalEncoding
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the platform
     * default encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated
     */
    public Scanner(File file) throws CompileException, IOException {
        this(
            file.getAbsolutePath(),    // optionalFileName
            new FileInputStream(file), // is
            null                       // optionalEncoding
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the given encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated
     */
    public Scanner(File file, String optionalEncoding) throws CompileException, IOException {
        this(
            file.getAbsolutePath(),    // optionalFileName
            new FileInputStream(file), // fis
            optionalEncoding                   // optionalEncoding
        );
    }

    // Public Scanners that read from an InputStream

    /**
     * Set up a scanner that reads tokens from the given
     * {@link InputStream} in the platform default encoding.
     * <p>
     * The <code>fileName</code> is solely used for reporting in thrown
     * exceptions.
     */
    public Scanner(String optionalFileName, InputStream is) throws CompileException, IOException {
        this(
            optionalFileName,
            new InputStreamReader(is), // in
            (short) 1, (short) 0       // initialLineNumber, initialColumnNumber
        );
    }

    /**
     * Set up a scanner that reads tokens from the given
     * {@link InputStream} with the given <code>optionalEncoding</code>
     * (<code>null</code> means platform default encoding).
     * <p>
     * The <code>optionalFileName</code> is used for reporting errors during
     * compilation and for source level debugging, and should name an existing
     * file. If <code>null</code> is passed, and the system property
     * <code>org.codehaus.janino.source_debugging.enable</code> is set to "true", then
     * a temporary file in <code>org.codehaus.janino.source_debugging.dir</code> or the
     * system's default temp dir is created in order to make the source code
     * available to a debugger.
     */
    public Scanner(
        String      optionalFileName,
        InputStream is,
        String      optionalEncoding
    ) throws CompileException, IOException {
        this(
            optionalFileName,                  // optionalFileName
            (                                  // in
                optionalEncoding == null ?
                new InputStreamReader(is) :
                new InputStreamReader(is, optionalEncoding)
            ),
            (short) 1, (short) 0               // initialLineNumber, initialColumnNumber
        );
    }

    // Public Scanners that read from a Reader.

    /**
     * Set up a scanner that reads tokens from the given
     * {@link Reader}.
     * <p>
     * The <code>optionalFileName</code> is used for reporting errors during
     * compilation and for source level debugging, and should name an existing
     * file. If <code>null</code> is passed, and the system property
     * <code>org.codehaus.janino.source_debugging.enable</code> is set to "true", then
     * a temporary file in <code>org.codehaus.janino.source_debugging.dir</code> or the
     * system's default temp dir is created in order to make the source code
     * available to a debugger.
     */
    public Scanner(String optionalFileName, Reader in) throws CompileException, IOException {
        this(
            optionalFileName, // optionalFileName
            in,               // in
            (short) 1,        // initialLineNumber
            (short) 0         // initialColumnNumber
        );
    }

    /**
     * Creates a {@link Scanner} that counts lines and columns from non-default initial
     * values.
     */
    public Scanner(
        String optionalFileName,
        Reader in,
        short  initialLineNumber,        // "1" is a good idea
        short  initialColumnNumber       // "0" is a good idea
    ) throws CompileException, IOException {

        // Debugging on source code level is only possible if the code comes from
        // a "real" Java source file which the debugger can read. If this is not the
        // case, and we absolutely want source code level debugging, then we write
        // a verbatim copy of the source code into a temporary file in the system
        // temp directory.
        // This behavior is controlled by the two system properties
        //     org.codehaus.janino.source_debugging.enable
        //     org.codehaus.janino.source_debugging.dir
        // JANINO is designed to compile in memory to save the overhead of disk
        // I/O, so writing this file is only recommended for source code level
        // debugging purposes.
        if (optionalFileName == null && Boolean.getBoolean(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_ENABLE)) {
            String dirName = System.getProperty(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_DIR);
            File dir = dirName == null ? null : new File(dirName);
            File temporaryFile = File.createTempFile("janino", ".java", dir);
            temporaryFile.deleteOnExit();
            in = new TeeReader(
                in,                            // in
                null, // out
                true                           // closeWriterOnEOF
            );
            optionalFileName = temporaryFile.getAbsolutePath();
        }

        this.optionalFileName     = optionalFileName;
        this.in                   = new UnicodeUnescapeReader(in);
        this.nextCharLineNumber   = initialLineNumber;
        this.nextCharColumnNumber = initialColumnNumber;

        this.readNextChar();
        this.nextToken       = this.internalRead();
        this.nextButOneToken = null;
    }

    /**
     * Return the file name optionally passed to the constructor.
     */
    public String getFileName() {
        return this.optionalFileName;
    }

    /**
     * Closes the character source (file, {@link InputStream}, {@link Reader}) associated
     * with this object. The results of future calls to {@link #peek()} and
     * {@link #read()} are undefined.
     * <p>
     * <b>This method is deprecated, because the concept described above is confusing. An
     * application should close the underlying {@link InputStream} or {@link Reader} itself.</b>
     *
     * @deprecated
     */
    public void close() throws IOException {
        this.in.close();
    }

    /**
     * Read the next token from the input.
     */
    public Token read() throws CompileException, IOException {
        Token res = this.nextToken;
        if (this.nextButOneToken != null) {
            this.nextToken = this.nextButOneToken;
            this.nextButOneToken = null;
        } else {
            this.nextToken = this.internalRead();
        }
        return res;
    }

    /**
     * Peek the next token, but don't remove it from the input.
     */
    public Token peek() {
        if (Scanner.DEBUG) System.err.println("peek() => \"" + this.nextToken + "\"");
        return this.nextToken;
    }

    /**
     * Peek the next but one token, neither remove the next nor the next but one token from the
     * input.
     * <p>
     * This makes parsing so much easier, e.g. for class literals like
     * <code>Map.class</code>.
     */
    public Token peekNextButOne() throws CompileException, IOException {
        if (this.nextButOneToken == null) this.nextButOneToken = this.internalRead();
        return this.nextButOneToken;
    }

    /**
     * Get the text of the doc comment (a.k.a. "JAVADOC comment") preceeding
     * the next token.
     * @return <code>null</code> if the next token is not preceeded by a doc comment
     */
    public String doc() {
        String s = this.docComment;
        this.docComment = null;
        return s;
    }

    /**
     * Returns the {@link Location} of the next token.
     */
    public Location location() {
        return this.nextToken.getLocation();
    }

    public abstract class Token {
        private /*final*/ String optionalFileName;
        private /*final*/ short  lineNumber;
        private /*final*/ short  columnNumber;
        private Location location = null;

        private Token() {
            this.optionalFileName = Scanner.this.optionalFileName;
            this.lineNumber       = Scanner.this.tokenLineNumber;
            this.columnNumber     = Scanner.this.tokenColumnNumber;
        }

        public Location getLocation() {
            if (this.location == null) {
                this.location = new Location(this.optionalFileName, this.lineNumber, this.columnNumber);
            }
            return this.location;
        }

        public boolean isKeyword() { return false; }
        public boolean isKeyword(String k) { return false; }
        public boolean isKeyword(String[] ks) { return false; }
        public String  getKeyword() throws CompileException {
            throw new CompileException("Not a keyword token", Scanner.this.location());
        }

        public boolean isIdentifier() { return false; }
        public boolean isIdentifier(String id) { return false; }
        public String  getIdentifier() throws CompileException {
            throw new CompileException("Not an identifier token", Scanner.this.location());
        }

        public boolean isLiteral() { return false; }
        public Object  getLiteralValue() throws CompileException {
            throw new CompileException("Not a literal token", Scanner.this.location());
        }

        public boolean isOperator() { return false; }
        public boolean isOperator(String o) { return false; }
        public boolean isOperator(String[] os) { return false; }
        public String  getOperator() throws CompileException {
            throw new CompileException("Not an operator token", Scanner.this.location());
        }

        public boolean isEOF() { return false; }
    }

    public class KeywordToken extends Token {
        private final String keyword;

        /**
         * @param keyword Must be in interned string!
         */
        KeywordToken(String keyword) {
            this.keyword = keyword;
        }

        public boolean isKeyword() { return true; }
        public boolean isKeyword(String k) { return this.keyword == k; }
        public boolean isKeyword(String[] ks) {
            for (int i = 0; i < ks.length; ++i) {
                if (this.keyword == ks[i]) return true;
            }
            return false;
        }
        public String getKeyword() { return this.keyword; }

        public String toString() { return this.keyword; }
    }

    public class IdentifierToken extends Token {
        private final String identifier;

        IdentifierToken(String identifier) {
            this.identifier = identifier;
        }

        public boolean isIdentifier() { return true; }
        public boolean isIdentifier(String id) { return this.identifier.equals(id); }
        public String getIdentifier() { return this.identifier; }

        public String toString() { return this.identifier; }
    }

    /**
     * This reference represents the "magic" literal "2147483648" which is only
     * allowed in a negated context.
     */
    public static final Integer MAGIC_INTEGER = new Integer(Integer.MIN_VALUE);

    /**
     * This reference represents the "magic" literal "9223372036854775808L" which is only
     * allowed in a negated context.
     */
    public static final Long MAGIC_LONG = new Long(Long.MIN_VALUE);

    /**
     * The type of the <code>value</code> parameter determines the type of the literal
     * token:
     * <table>
     *   <tr><th>Type/value returned by {@link #getLiteralValue()}</th><th>Literal</th></tr>
     *   <tr><td>{@link String}</td><td>STRING literal</td></tr>
     *   <tr><td>{@link Character}</td><td>CHAR literal</td></tr>
     *   <tr><td>{@link Integer}</td><td>INT literal</td></tr>
     *   <tr><td>{@link Long}</td><td>LONG literal</td></tr>
     *   <tr><td>{@link Float}</td><td>FLOAT literal</td></tr>
     *   <tr><td>{@link Double}</td><td>DOUBLE literal</td></tr>
     *   <tr><td>{@link Boolean}</td><td>BOOLEAN literal</td></tr>
     *   <tr><td><code>null</code></td><td>NULL literal</td></tr>
     * </table>
     */
    public final class LiteralToken extends Token {
        private final Object value;

        /**
         * @param value A {@link Boolean}, {@link String}, {@link Double}, {@link Float}, {@link Character}, or
         *              <code>null</code>
         */
        public LiteralToken(Object value) {
            this.value = value;
        }

        // Implement {@link Literal}.
        public boolean isLiteral() { return true; }
        public Object getLiteralValue()  { return this.value; }

        public String toString() {
            return Scanner.literalValueToString(this.value);
        }
    }
    public static String literalValueToString(Object v) {
        if (v instanceof String) {
            StringBuffer sb = new StringBuffer();
            sb.append('"');
            String s = (String) v;
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);

                if (c == '"') {
                    sb.append("\\\"");
                } else {
                    Scanner.escapeCharacter(c, sb);
                }
            }
            sb.append('"');
            return sb.toString();
        }
        if (v instanceof Character) {
            char c = ((Character) v).charValue();
            if (c == '\'') return "'\\''";
            StringBuffer sb = new StringBuffer("'");
            Scanner.escapeCharacter(c, sb);
            return sb.append('\'').toString();
        }
        if (v instanceof Integer) {
            if (v == Scanner.MAGIC_INTEGER) return "2147483648";
            int iv = ((Integer) v).intValue();
            return iv < 0 ? "0x" + Integer.toHexString(iv) : Integer.toString(iv);
        }
        if (v instanceof Long) {
            if (v == Scanner.MAGIC_LONG) return "9223372036854775808L";
            long lv = ((Long) v).longValue();
            return lv < 0L ? "0x" + Long.toHexString(lv) + 'L' : Long.toString(lv) + 'L';
        }
        if (v instanceof Float) {
            return v.toString() + 'F';
        }
        if (v instanceof Double) {
            return v.toString() + 'D';
        }
        if (v instanceof Boolean) {
            return v.toString();
        }
        if (v instanceof Byte) {
            return "((byte)" + v.toString() + ")";
        }
        if (v instanceof Short) {
            return "((short)" + v.toString() + ")";
        }

        if (v == null) {
            return "null";
        }
        throw new JaninoRuntimeException("Unexpected value type \"" + v.getClass().getName() + "\"");
    }

    private class OperatorToken extends Token {
        private final String operator;

        /**
         * @param operator Must be an interned string!
         */
        OperatorToken(String operator) {
            this.operator = operator;
        }

        public boolean isOperator() { return true; }
        public boolean isOperator(String o) { return this.operator == o; }
        public boolean isOperator(String[] os) {
            for (int i = 0; i < os.length; ++i) {
                if (this.operator == os[i]) return true;
            }
            return false;
        }
        public String getOperator() { return this.operator; }

        public String toString() { return this.operator; }
    }

    public class EOFToken extends Token {
        public boolean isEOF() { return true; }
        public String toString() { return "End-Of-File"; }
    }

    /**
     * Escape unprintable characters appropriately, i.e. as
     * backslash-letter or backslash-U-four-hex-digits.
     * <p>
     * Notice: Single and double quotes are not escaped!
     */
    private static void escapeCharacter(char c, StringBuffer sb) {

        // Backslash escape sequences.
        int idx = "\b\t\n\f\r\\".indexOf(c);
        if (idx != -1) {
            sb.append('\\').append("btnfr\\".charAt(idx));
        } else

        // Printable characters.
        if (c >= ' ' && c < 255 && c != 127) {
            sb.append(c);
        } else

        // Backslash-U escape sequences.
        {
            sb.append("\\u");
            String hs = Integer.toHexString(0xffff & c);
            for (int j = hs.length(); j < 4; ++j) sb.append('0');
            sb.append(hs);
        }
    }

    private Token internalRead() throws CompileException, IOException {
        if (this.docComment != null) {
            this.warning("MDC", "Misplaced doc comment", this.nextToken.getLocation());
            this.docComment = null;
        }

        // Skip whitespace and process comments.
        int          state = 0;
        StringBuffer dcsb  = null; // For doc comment

        PROCESS_COMMENTS:
        for (;;) {
            switch (state) {

            case 0: // Outside any comment
                if (this.nextChar == -1) {
                    return new EOFToken();
                } else
                if (Character.isWhitespace((char) this.nextChar)) {
                    ;
                } else
                if (this.nextChar == '/') {
                    state = 1;
                } else
                {
                    break PROCESS_COMMENTS;
                }
                break;

            case 1:  // After "/"
                if (this.nextChar == -1) {
                    return new OperatorToken("/");
                } else
                if (this.nextChar == '=') {
                    this.readNextChar();
                    return new OperatorToken("/=");
                } else
                if (this.nextChar == '/') {
                    state = 2;
                } else
                if (this.nextChar == '*') {
                    state = 3;
                } else
                {
                    return new OperatorToken("/");
                }
                break;

            case 2: // After "//..."
                if (this.nextChar == -1) {
                    return new EOFToken();
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    state = 0;
                } else
                {
                    ;
                }
                break;

            case 3: // After "/*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 4;
                } else
                {
                    state = 9;
                }
                break;

            case 4: // After "/**"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '/') {
                    state = 0;
                } else
                {
                    if (this.docComment != null) {
                        this.warning(
                            "MDC",
                            "Multiple doc comments",
                            new Location(this.optionalFileName, this.nextCharLineNumber, this.nextCharColumnNumber)
                        );
                    }
                    dcsb = new StringBuffer();
                    dcsb.append((char) this.nextChar);
                    state = (
                        (this.nextChar == '\r' || this.nextChar == '\n') ? 6
                        : this.nextChar == '*' ? 8
                        : 5
                    );
                }
                break;

            case 5: // After "/**..."
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 8;
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    dcsb.append((char) this.nextChar);
                    state = 6;
                } else
                {
                    dcsb.append((char) this.nextChar);
                }
                break;

            case 6: // After "/**...\n"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 7;
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    dcsb.append((char) this.nextChar);
                } else
                if (this.nextChar == ' ' || this.nextChar == '\t') {
                    ;
                } else
                {
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 7: // After "/**...\n *"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    ;
                } else
                if (this.nextChar == '/') {
                    this.docComment = dcsb.toString();
                    state = 0;
                } else
                {
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 8: // After "/**...*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '/') {
                    this.docComment = dcsb.toString();
                    state = 0;
                } else
                if (this.nextChar == '*') {
                    dcsb.append('*');
                } else
                {
                    dcsb.append('*');
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 9: // After "/*..."
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 10;
                } else
                {
                    ;
                }
                break;

            case 10: // After "/*...*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '/') {
                    state = 0;
                } else
                if (this.nextChar == '*') {
                    ;
                } else
                {
                    state = 9;
                }
                break;

            default:
                throw new JaninoRuntimeException(Integer.toString(state));
            }
            this.readNextChar();
        }

        /*
         * Whitespace and comments are now skipped; "nextChar" is definitely
         * the first character of the token.
         */
        this.tokenLineNumber   = this.nextCharLineNumber;
        this.tokenColumnNumber = this.nextCharColumnNumber;

        // Scan identifier.
        if (Character.isJavaIdentifierStart((char) this.nextChar)) {
            StringBuffer sb = new StringBuffer();
            sb.append((char) this.nextChar);
            for (;;) {
                this.readNextChar();
                if (
                    this.nextChar == -1 ||
                    !Character.isJavaIdentifierPart((char) this.nextChar)
                ) break;
                sb.append((char) this.nextChar);
            }
            String s = sb.toString();
            if (s.equals("true")) return new LiteralToken(Boolean.TRUE);
            if (s.equals("false")) return new LiteralToken(Boolean.FALSE);
            if (s.equals("null")) return new LiteralToken(null);
            {
                String v = (String) Scanner.JAVA_KEYWORDS.get(s);
                if (v != null) return new KeywordToken(v);
            }
            return new IdentifierToken(s);
        }

        // Scan numeric literal.
        if (Character.isDigit((char) this.nextChar)) {
            return this.scanNumericLiteral(0);
        }

        // A "." is special: Could either be a floating-point constant like ".001", or the "."
        // operator.
        if (this.nextChar == '.') {
            this.readNextChar();
            if (Character.isDigit((char) this.nextChar)) {
                return this.scanNumericLiteral(2);
            } else {
                return new OperatorToken(".");
            }
        }

        // Scan string literal.
        if (this.nextChar == '"') {
            StringBuffer sb = new StringBuffer("");
            this.readNextChar();
            if (this.nextChar == -1) throw new CompileException("EOF in string literal", this.location());
            if (this.nextChar == '\r' || this.nextChar == '\n') {
                throw new CompileException("Line break in string literal", this.location());
            }
            while (this.nextChar != '"') {
                sb.append(this.unescapeCharacterLiteral());
            }
            this.readNextChar();
            return new LiteralToken(sb.toString());
        }

        // Scan character literal.
        if (this.nextChar == '\'') {
            this.readNextChar();
            if (this.nextChar == '\'') {
                throw new CompileException(
                    "Single quote must be backslash-escaped in character literal",
                    this.location()
                );
            }
            char lit = this.unescapeCharacterLiteral();
            if (this.nextChar != '\'') throw new CompileException("Closing single quote missing", this.location());
            this.readNextChar();

            return new LiteralToken(new Character(lit));
        }

        // Scan separator / operator.
        {
            String v = (String) Scanner.JAVA_OPERATORS.get(
                new String(new char[] { (char) this.nextChar })
            );
            if (v != null) {
                for (;;) {
                    this.readNextChar();
                    String v2 = (String) Scanner.JAVA_OPERATORS.get(v + (char) this.nextChar);
                    if (v2 == null) return new OperatorToken(v);
                    v = v2;
                }
            }
        }

        throw new CompileException(
            "Invalid character input \"" + (char) this.nextChar + "\" (character code " + this.nextChar + ")",
            this.location()
        );
    }

    private Token scanNumericLiteral(int initialState) throws CompileException, IOException {
        StringBuffer sb = (initialState == 2) ? new StringBuffer("0.") : new StringBuffer();
        int state = initialState;
        for (;;) {
            switch (state) {

            case 0: // First character.
                if (this.nextChar == '0') {
                    state = 6;
                } else
                /* if (Character.isDigit((char) this.nextChar)) */ {
                    sb.append((char) this.nextChar);
                    state = 1;
                }
                break;

            case 1: // Decimal digits.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    this.readNextChar();
                    return this.stringToLongLiteralToken(sb.toString(), 10);
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F') {
                    this.readNextChar();
                    return this.stringToFloatLiteralToken(sb.toString());
                } else
                if (this.nextChar == 'd' || this.nextChar == 'D') {
                    this.readNextChar();
                    return this.stringToDoubleLiteralToken(sb.toString());
                } else
                if (this.nextChar == '.') {
                    sb.append('.');
                    state = 2;
                } else
                if (this.nextChar == 'E' || this.nextChar == 'e') {
                    sb.append('E');
                    state = 3;
                } else
                {
                    return this.stringToIntegerLiteralToken(sb.toString(), 10);
                }
                break;

            case 2: // After decimal point.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'e' || this.nextChar == 'E') {
                    sb.append('E');
                    state = 3;
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F') {
                    this.readNextChar();
                    return this.stringToFloatLiteralToken(sb.toString());
                } else
                if (this.nextChar == 'd' || this.nextChar == 'D') {
                    this.readNextChar();
                    return this.stringToDoubleLiteralToken(sb.toString());
                } else
                {
                    return this.stringToDoubleLiteralToken(sb.toString());
                }
                break;

            case 3: // Read exponent.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                    state = 5;
                } else
                if (this.nextChar == '-' || this.nextChar == '+') {
                    sb.append((char) this.nextChar);
                    state = 4;
                } else
                {
                    throw new CompileException("Exponent missing after \"E\"", this.location());
                }
                break;

            case 4: // After exponent sign.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                    state = 5;
                } else
                {
                    throw new CompileException("Exponent missing after \"E\" and sign", this.location());
                }
                break;

            case 5: // After first exponent digit.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F') {
                    this.readNextChar();
                    return this.stringToFloatLiteralToken(sb.toString());
                } else
                if (this.nextChar == 'd' || this.nextChar == 'D') {
                    this.readNextChar();
                    return this.stringToDoubleLiteralToken(sb.toString());
                } else
                {
                    return this.stringToDoubleLiteralToken(sb.toString());
                }
                break;

            case 6: // After leading zero
                if ("01234567".indexOf(this.nextChar) != -1) {
                    sb.append((char) this.nextChar);
                    state = 7;
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    this.readNextChar();
                    return this.stringToLongLiteralToken("0", 10);
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F') {
                    this.readNextChar();
                    return this.stringToFloatLiteralToken("0");
                } else
                if (this.nextChar == 'd' || this.nextChar == 'D') {
                    this.readNextChar();
                    return this.stringToDoubleLiteralToken("0");
                } else
                if (this.nextChar == '.') {
                    sb.append("0.");
                    state = 2;
                } else
                if (this.nextChar == 'E' || this.nextChar == 'e') {
                    sb.append('E');
                    state = 3;
                } else
                if (this.nextChar == 'x' || this.nextChar == 'X') {
                    state = 8;
                } else
                {
                    return this.stringToIntegerLiteralToken("0", 10);
                }
                break;

            case 7: // In octal literal.
                if ("01234567".indexOf(this.nextChar) != -1) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == '8' || this.nextChar == '9') {
                    throw new CompileException(
                        "Digit '" + (char) this.nextChar + "' not allowed in octal literal",
                        this.location()
                    );
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    // Octal long literal.
                    this.readNextChar();
                    return this.stringToLongLiteralToken(sb.toString(), 8);
                } else
                {
                    // Octal int literal
                    return this.stringToIntegerLiteralToken(sb.toString(), 8);
                }
                break;

            case 8: // First hex digit
                if (Character.digit((char) this.nextChar, 16) != -1) {
                    sb.append((char) this.nextChar);
                    state = 9;
                } else
                {
                    throw new CompileException("Hex digit expected after \"0x\"", this.location());
                }
                break;

            case 9:
                if (Character.digit((char) this.nextChar, 16) != -1) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    // Hex long literal
                    this.readNextChar();
                    return this.stringToLongLiteralToken(sb.toString(), 16);
                } else
                {
                    // Hex long literal
                    return this.stringToIntegerLiteralToken(sb.toString(), 16);
                }
                break;

            default:
                throw new JaninoRuntimeException(Integer.toString(state));
            }
            this.readNextChar();
        }
    }

    private LiteralToken stringToIntegerLiteralToken(final String s, int radix) throws CompileException {
        int x;
        switch (radix) {

        case 10:
            // Special case: Decimal literal 2^31 must only appear in "negated" context, i.e.
            // "-2147483648" is a valid long literal, but "2147483648" is not.
            if (s.equals("2147483648")) return new LiteralToken(Scanner.MAGIC_INTEGER);
            try {
                x = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new CompileException(
                    "Value of decimal integer literal \"" + s + "\" is out of range",
                    this.location()
                );
            }
            break;

        case 8:
            // Cannot use "Integer.parseInt(s, 8)" because that parses SIGNED values.
            x = 0;
            for (int i = 0; i < s.length(); ++i) {
                if ((x & 0xe0000000) != 0) {
                    throw new CompileException(
                        "Value of octal integer literal \"" + s + "\" is out of range",
                        this.location()
                    );
                }
                x = (x << 3) + Character.digit(s.charAt(i), 8);
            }
            break;

        case 16:
            // Cannot use "Integer.parseInt(s, 16)" because that parses SIGNED values.
            x = 0;
            for (int i = 0; i < s.length(); ++i) {
                if ((x & 0xf0000000) != 0) {
                    throw new CompileException(
                        "Value of hexadecimal integer literal \"" + s + "\" is out of range",
                        this.location()
                    );
                }
                x = (x << 4) + Character.digit(s.charAt(i), 16);
            }
            break;

        default:
            throw new JaninoRuntimeException("Illegal radix " + radix);
        }
        return new LiteralToken(new Integer(x));
    }

    private LiteralToken stringToLongLiteralToken(final String s, int radix) throws CompileException {
        long x;
        switch (radix) {

        case 10:
            // Special case: Decimal literal 2^63 must only appear in "negated" context, i.e.
            // "-9223372036854775808" is a valid long literal, but "9223372036854775808" is not.
            if (s.equals("9223372036854775808")) return new LiteralToken(Scanner.MAGIC_LONG);

            try {
                x = Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new CompileException(
                    "Value of decimal long literal \"" + s + "\" is out of range",
                    this.location()
                );
            }
            break;

        case 8:
            // Cannot use "Long.parseLong(s, 8)" because that parses SIGNED values.
            x = 0L;
            for (int i = 0; i < s.length(); ++i) {
                if ((x & 0xe000000000000000L) != 0L) {
                    throw new CompileException(
                        "Value of octal long literal \"" + s + "\" is out of range",
                        this.location()
                    );
                }
                x = (x << 3) + Character.digit(s.charAt(i), 8);
            }
            break;

        case 16:
            // Cannot use "Long.parseLong(s, 16)" because that parses SIGNED values.
            x = 0L;
            for (int i = 0; i < s.length(); ++i) {
                if ((x & 0xf000000000000000L) != 0L) {
                    throw new CompileException(
                        "Value of hexadecimal long literal \"" + s + "\" is out of range",
                        this.location()
                    );
                }
                x = (x << 4) + (long) Character.digit(s.charAt(i), 16);
            }
            break;

        default:
            throw new JaninoRuntimeException("Illegal radix " + radix);
        }
        return new LiteralToken(new Long(x));
    }

    private LiteralToken stringToFloatLiteralToken(final String s) throws CompileException {
        float f;
        try {
            f = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new JaninoRuntimeException(
                "SNO: parsing float literal \"" + s + "\" throws a \"NumberFormatException\""
            );
        }
        if (Float.isInfinite(f)) {
            throw new CompileException("Value of float literal \"" + s + "\" is out of range", this.location());
        }
        if (Float.isNaN(f)) {
            throw new JaninoRuntimeException("SNO: parsing float literal \"" + s + "\" results is NaN");
        }

        // Check for FLOAT underrun.
        if (f == 0.0F) {
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if ("123456789".indexOf(c) != -1) {
                    throw new CompileException(
                        "Literal \"" + s + "\" is too small to be represented as a float",
                        this.location()
                    );
                }
                if ("0.".indexOf(c) == -1) break;
            }
        }

        return new LiteralToken(new Float(f));
    }

    private LiteralToken stringToDoubleLiteralToken(final String s) throws CompileException {
        double d;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new JaninoRuntimeException(
                "SNO: parsing double literal \"" + s + "\" throws a \"NumberFormatException\""
            );
        }
        if (Double.isInfinite(d)) {
            throw new CompileException("Value of double literal \"" + s + "\" is out of range", this.location());
        }
        if (Double.isNaN(d)) {
            throw new JaninoRuntimeException("SNO: parsing double literal \"" + s + "\" results is NaN");
        }


        // Check for DOUBLE underrun.
        if (d == 0.0D) {
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if ("123456789".indexOf(c) != -1) {
                    throw new CompileException(
                        "Literal \"" + s + "\" is too small to be represented as a double",
                        this.location()
                    );
                }
                if ("0.".indexOf(c) == -1) break;
            }
        }

        return new LiteralToken(new Double(d));
    }

    /**
     * Consume characters until a literal character is complete.
     */
    private char unescapeCharacterLiteral() throws CompileException, IOException {
        if (this.nextChar == -1) throw new CompileException("EOF in character literal", this.location());

        if (this.nextChar == '\r' || this.nextChar == '\n') {
            throw new CompileException("Line break in literal not allowed", this.location());
        }

        if (this.nextChar != '\\') {
            char res = (char) this.nextChar;
            this.readNextChar();
            return res;
        }
        this.readNextChar();
        int idx = "btnfr".indexOf(this.nextChar);
        if (idx != -1) {
            char res = "\b\t\n\f\r".charAt(idx);
            this.readNextChar();
            return res;
        }
        idx = "01234567".indexOf(this.nextChar);
        if (idx != -1) {
            int code = idx;
            this.readNextChar();
            idx = "01234567".indexOf(this.nextChar);
            if (idx == -1) return (char) code;
            code = 8 * code + idx;
            this.readNextChar();
            idx = "01234567".indexOf(this.nextChar);
            if (idx == -1) return (char) code;
            code = 8 * code + idx;
            if (code > 255) throw new CompileException("Invalid octal escape", this.location());
            this.readNextChar();
            return (char) code;
        }

        char res = (char) this.nextChar;
        this.readNextChar();
        return res;
    }

    // Read one character and store in "nextChar".
    private void readNextChar() throws IOException, CompileException {
        try {
            this.nextChar = this.in.read();
        } catch (UnicodeUnescapeException ex) {
            throw new CompileException(ex.getMessage(), this.location(), ex);
        }
        if (this.nextChar == '\r') {
            ++this.nextCharLineNumber;
            this.nextCharColumnNumber = 0;
            this.crLfPending = true;
        } else
        if (this.nextChar == '\n') {
            if (this.crLfPending) {
                this.crLfPending = false;
            } else {
                ++this.nextCharLineNumber;
                this.nextCharColumnNumber = 0;
            }
        } else
        {
            ++this.nextCharColumnNumber;
        }
//System.out.println("'" + (char) nextChar + "' = " + (int) nextChar);
    }

    private static final boolean DEBUG = false;

    private /*final*/ String     optionalFileName;
    private /*final*/ Reader     in;
    private int              nextChar  = -1; // Always valid (one character read-ahead).
    private boolean          crLfPending = false;
    private short            nextCharLineNumber;
    private short            nextCharColumnNumber;

    private Token  nextToken = new Token() { }; // Is always non-null (one token read-ahead).
    private Token  nextButOneToken;             // Is only non-null after "peekNextButOne()".
    private short  tokenLineNumber;             // Line number of "nextToken" (typically starting at one).
    private short  tokenColumnNumber;           // Column number of first character of "nextToken" (1 if token is
                                                // immediately preceeded by a line break).
    private String docComment = null;           // The optional JAVADOC comment preceeding the "nextToken".

    private static final Map JAVA_KEYWORDS = new HashMap();
    static {
        String[] ks = {
            "abstract", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else",
            "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try",
            "void", "volatile", "while"
        };
        for (int i = 0; i < ks.length; ++i) Scanner.JAVA_KEYWORDS.put(ks[i], ks[i]);
    }
    private static final Map JAVA_OPERATORS = new HashMap();
    static {
        String[] ops = {
            // Separators:
            "(", ")", "{", "}", "[", "]", ";", ",", ".",
            // Operators:
            "=",  ">",  "<",  "!",  "~",  "?",  ":",
            "==", "<=", ">=", "!=", "&&", "||", "++", "--",
            "+",  "-",  "*",  "/",  "&",  "|",  "^",  "%",  "<<",  ">>",  ">>>",
            "+=", "-=", "*=", "/=", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>=",
        };
        for (int i = 0; i < ops.length; ++i) Scanner.JAVA_OPERATORS.put(ops[i], ops[i]);
    }

    /**
     * By default, warnings are discarded, but an application my install a
     * {@link WarningHandler}.
     * <p>
     * Notice that there is no <code>Scanner.setErrorHandler()</code> method, but scan errors
     * always throw a {@link CompileException}. The reason being is that there is no reasonable
     * way to recover from scan errors and continue scanning, so there is no need to install
     * a custom scan error handler.
     *
     * @param optionalWarningHandler <code>null</code> to indicate that no warnings be issued
     */
    public void setWarningHandler(WarningHandler optionalWarningHandler) {
        this.optionalWarningHandler = optionalWarningHandler;
    }

    // Used for elaborate warning handling.
    private WarningHandler optionalWarningHandler = null;

    /**
     * Issues a warning with the given message and location and returns. This is done through
     * a {@link WarningHandler} that was installed through
     * {@link #setWarningHandler(WarningHandler)}.
     * <p>
     * The <code>handle</code> argument qulifies the warning and is typically used by
     * the {@link WarningHandler} to suppress individual warnings.
     */
    private void warning(String handle, String message, Location optionalLocation) {
        if (this.optionalWarningHandler != null) {
            this.optionalWarningHandler.handleWarning(handle, message, optionalLocation);
        }
    }
}
