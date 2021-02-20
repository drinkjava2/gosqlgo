/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.gosqlgo.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.github.drinkjava2.gosqlgo.GoSqlGoEnv;
import com.github.drinkjava2.gosqlgo.util.GsgFileUtils;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;

/**
 * This is a DynamicCompileEngine to compile and load Java source code into
 * memory, only tested in Eclipse, Maven, Tomcat, Weblogic
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
@SuppressWarnings("all")
public class DynamicCompileEngine {
    public static final DynamicCompileEngine instance = new DynamicCompileEngine();
    private static final Map<String, Class<?>> compiledClassCache = new ConcurrentHashMap<String, Class<?>>();

    private ClassLoader parentClassLoader;
    private String classpath;

    private DynamicCompileEngine() {
        this.buildClassPath();
    }

    private static String readFileToString(String filename) {
        String result = null;
        File file = new File(filename);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            result = new String(chars);
            reader.close();
        } catch (IOException e) {
            throw new CompileException("Error happen when open file '" + filename + "'", e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new CompileException("Error happen when closing file '" + filename + "'", e);
                }
        }
        return result;
    }

    /**
     * Only tested in Eclipse, Maven, Tomcat, Weblogic
     */
    private void buildClassPath() {
        // Build classPath for weblogic
        this.classpath = null;

        // buildClassPath for Tomcat
        URLClassLoader servletLoader = (URLClassLoader) HttpServletRequest.class.getClassLoader();
        StringBuilder sb = new StringBuilder();
        for (URL url : servletLoader.getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }

        this.parentClassLoader = Thread.currentThread().getContextClassLoader();
        // this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
        for (URL url : ((URLClassLoader) this.parentClassLoader).getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        this.classpath = sb.toString();

        // buildClassPath for Maven unit test by run "mvn test" command
        if (classpath.indexOf("surefire/surefirebooter") > 0) {
            String path = GsgStrUtils.substringAfter(classpath, "/");
            path = GsgStrUtils.substringBefore(path, ";");
            String mavenJarPath = readFileToString(path);
            mavenJarPath = GsgStrUtils.substringBetween(mavenJarPath, "Class-Path: ", "Main-Class: ");
            mavenJarPath = GsgStrUtils.replace(mavenJarPath, "\r\n ", "").trim();
            mavenJarPath = GsgStrUtils.replace(mavenJarPath, " ", ";");
            classpath = GsgStrUtils.replace(mavenJarPath, "file:/", "");
        }
    }

    public Class<?> javaCodeToClass(String fullClassName, String javaCode) {
        if (GsgStrUtils.isEmpty(fullClassName))
            throw new CompileException("Can not compile class with empty name");
        if (GsgStrUtils.isEmpty(javaCode))
            throw new CompileException("Can not compile class " + fullClassName + " with empty Java source code");

        Class<?> result = compiledClassCache.get(fullClassName);
        if (result != null)
            return result;

        Object instance = null;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnosticCollector, null, null));

        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(this.classpath);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, options, null, jfiles);
        boolean success = task.call();
        if (success) {
            JavaClassObject jco = fileManager.getJavaClassObject();
            String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
            path = GsgStrUtils.replaceFirst(path, "file:", "");
            String fileName = path + GsgStrUtils.replace(fullClassName, ".", "/") + ".class";
            GsgFileUtils.writeFile(fileName, jco.getBytes());

            if (GoSqlGoEnv.isJavaFileExport()) {
                fileName = path + GsgStrUtils.replace(fullClassName, ".", "/") + ".java";
                GsgFileUtils.writeFile(fileName, javaCode, "utf-8");
            }

            try {
                result = parentClassLoader.loadClass(fullClassName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompileException(" \r\n <<< Dynamic Class Loadere Error \r\n", e);
            }
            if (result != null) {
                compiledClassCache.put(fullClassName, result);
            } else
                throw new CompileException(" \r\n <<< Dynamic Class Loadere Null Error \r\n" + fullClassName);
            return result;
        } else {
            String error = "";
            List<Diagnostic<? extends JavaFileObject>> dias = diagnosticCollector.getDiagnostics();
            List<String> javaCodes = new BufferedReader(new StringReader(javaCode)).lines().collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < javaCodes.size(); i++) {
                sb.append(javaCodes.get(i)).append("\n");
                Diagnostic d = null;
                for (int j = 0; j < dias.size(); j++) {
                    d = dias.get(j);
                    if (i == d.getLineNumber() - 1) {
                        for (int sp = 0; sp < d.getColumnNumber() - 1; sp++)
                            sb.append(" ");
                        for (int k = 0; k < d.getEndPosition() - d.getStartPosition(); k++)
                            sb.append("^");
                        String message = d.getMessage(Locale.ENGLISH);
                        message = GsgStrUtils.substringBefore(message, "\n");
                        sb.append("  ").append(message).append("\n");
                        break;
                    }
                }
            }
            javaCode = sb.toString();

            throw new CompileException("\r\n ========= Java Source Code Compile Error =========\r\n" + javaCode);
        }
    }

    public Object javaCodeToNewInstance(String fullClassName, String javaCode) throws InstantiationException, IllegalAccessException {
        Class<?> clazz = javaCodeToClass(fullClassName, javaCode);
        Object instance = clazz.newInstance();
        return instance;
    }

    private String compilePrint(Diagnostic diagnostic) {
        StringBuffer res = new StringBuffer();
        res.append("Code:[" + diagnostic.getCode() + "]\n");
        res.append("Kind:[" + diagnostic.getKind() + "]\n");
        res.append("Position:[" + diagnostic.getPosition() + "]\n");
        res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        res.append("Source:[" + diagnostic.getSource() + "]\n");
        res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
        return res.toString();
    }

}