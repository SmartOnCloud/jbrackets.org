
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.janino.tools.Disassembler;

/**
 * This {@link ClassLoader} allows for the loading of a set of Java&trade; classes
 * provided in class file format.
 */
public class ByteArrayClassLoader extends ClassLoader {
    private static final boolean DEBUG = false;

    /**
     * The given {@link Map} of classes must not be modified afterwards.
     *
     * @param classes String className => byte[] data
     */
    public ByteArrayClassLoader(Map classes) {
        this.classes = classes;
    }

    /**
     * @see #ByteArrayClassLoader(Map)
     */
    public ByteArrayClassLoader(Map classes, ClassLoader parent) {
        super(parent);
        this.classes = classes;
    }

    /**
     * Implements {@link ClassLoader#findClass(String)}.
     * <p>
     * Notice that, although nowhere documented, no more than one thread at a time calls this
     * method, because {@link ClassLoader#loadClass(java.lang.String)} is
     * <code>synchronized</code>.
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] data = (byte[]) this.classes.get(name);
        if (data == null) throw new ClassNotFoundException(name);

        if (DEBUG) {
            System.out.println("*** Disassembly of class \"" + name + "\":");
            try {
                new Disassembler().disasm(new ByteArrayInputStream(data));
                System.out.flush();
            } catch (IOException ex) {
                throw new JaninoRuntimeException("SNO: IOException despite ByteArrayInputStream");
            }
        }

        // Notice: Not inheriting the protection domain will cause problems with Java Web Start /
        // JNLP. See
        //     http://jira.codehaus.org/browse/JANINO-104
        //     http://www.nabble.com/-Help-jel--java.security.AccessControlException-to13073723.html
        return super.defineClass(
            name,                                 // name
            data, 0, data.length,                 // b, off, len
            this.getClass().getProtectionDomain() // protectionDomain
        );
    }

    /**
     * An object is regarded equal to <code>this</code> iff
     * <ul>
     *   <li>It is also an instance of {@link ByteArrayClassLoader}
     *   <li>Both have the same parent {@link ClassLoader}
     *   <li>Exactly the same classes (name, bytecode) were added to both
     * </ul>
     * Roughly speaking, equal {@link ByteArrayClassLoader}s will return functionally identical
     * {@link Class}es on {@link ClassLoader#loadClass(java.lang.String)}.
     */
    public boolean equals(Object o) {
        if (!(o instanceof ByteArrayClassLoader)) return false;
        if (this == o) return true;
        ByteArrayClassLoader that = (ByteArrayClassLoader) o;

        {
            final ClassLoader parentOfThis = this.getParent();
            final ClassLoader parentOfThat = that.getParent();
            if (parentOfThis == null ? parentOfThat != null : !parentOfThis.equals(parentOfThat)) return false;
        }

        if (this.classes.size() != that.classes.size()) return false;
        for (Iterator it = this.classes.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            byte[] ba = (byte[]) that.classes.get(me.getKey());
            if (ba == null) return false; // Key missing in "that".
            if (!Arrays.equals((byte[]) me.getValue(), ba)) return false; // Byte arrays differ.
        }
        return true;
    }
    public int hashCode() {
        int hc = this.getParent().hashCode();
        if (this.classes==null) this.classes = new HashMap();
        Set entrySet = this.classes.entrySet();
	for (Iterator it = entrySet.iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            hc ^= me.getKey().hashCode();
            byte[] ba = (byte[]) me.getValue();
            for (int i = 0; i < ba.length; ++i) {
                hc = (31 * hc) ^ ba[i];
            }
        }
        return hc;
    }

    private Map classes = new HashMap(); // String className => byte[] data
}
