
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

package org.codehaus.janino.util.resource;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.codehaus.janino.util.iterator.*;

/**
 * Finds resources in any of the "*.jar" files that exist in a given set of directories.
 */
public class JarDirectoriesResourceFinder extends LazyMultiResourceFinder {

    /**
     * @param directories The set of directories to search for JAR files.
     */
    public JarDirectoriesResourceFinder(final File[] directories) {
        super(new MultiDimensionalIterator(

            // Iterate over directories.
            new TransformingIterator(Arrays.asList(directories).iterator()) {
                protected Object transform(Object o) { // File directory => Iterator ResourceFinder
                    File directory = (File) o;

                    if (!directory.exists()) return Collections.EMPTY_LIST.iterator();

                    // Iterate over the JAR files in the given directory.
                    File[] jarFiles = directory.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) { return name.endsWith(".jar"); }
                    });
                    return new TransformingIterator(Arrays.asList(jarFiles).iterator()) {
                        protected Object transform(Object o) { // File jarFile => ResourceFinder
                            File zipFile = (File) o;
                            try {
                                return new ZipFileResourceFinder(new ZipFile(zipFile));
                            } catch (IOException e) {
                                return ResourceFinder.EMPTY_RESOURCE_FINDER;
                            }
                        }
                    };
                }
            },
            2
        ));
    }
}
