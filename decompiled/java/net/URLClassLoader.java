package java.net;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Pack200.Unpacker;
import org.xmlpull.v1.XmlPullParser;

@FindBugsSuppressWarnings({"DMI_COLLECTION_OF_URLS", "DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"})
public class URLClassLoader extends SecureClassLoader {
    private URLStreamHandlerFactory factory;
    ArrayList<URLHandler> handlerList;
    Map<URL, URLHandler> handlerMap;
    ArrayList<URL> originalUrls;
    List<URL> searchList;

    static class IndexFile {
        private HashMap<String, ArrayList<URL>> map;

        public IndexFile(java.util.HashMap<java.lang.String, java.util.ArrayList<java.net.URL>> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.IndexFile.<init>(java.util.HashMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.IndexFile.<init>(java.util.HashMap):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.IndexFile.<init>(java.util.HashMap):void");
        }

        private static java.net.URL getParentURL(java.net.URL r1) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.IndexFile.getParentURL(java.net.URL):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.IndexFile.getParentURL(java.net.URL):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.IndexFile.getParentURL(java.net.URL):java.net.URL");
        }

        static java.net.URLClassLoader.IndexFile readIndexFile(java.util.jar.JarFile r1, java.util.jar.JarEntry r2, java.net.URL r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile");
        }

        java.util.ArrayList<java.net.URL> get(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.IndexFile.get(java.lang.String):java.util.ArrayList<java.net.URL>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.IndexFile.get(java.lang.String):java.util.ArrayList<java.net.URL>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.IndexFile.get(java.lang.String):java.util.ArrayList<java.net.URL>");
        }
    }

    class URLHandler {
        URL codeSourceUrl;
        final /* synthetic */ URLClassLoader this$0;
        URL url;

        public URLHandler(java.net.URLClassLoader r1, java.net.URL r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.<init>(java.net.URLClassLoader, java.net.URL):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.<init>(java.net.URLClassLoader, java.net.URL):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.<init>(java.net.URLClassLoader, java.net.URL):void");
        }

        java.lang.Class<?> createClass(java.io.InputStream r1, java.lang.String r2, java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.createClass(java.io.InputStream, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.createClass(java.io.InputStream, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.createClass(java.io.InputStream, java.lang.String, java.lang.String):java.lang.Class<?>");
        }

        java.lang.Class<?> findClass(java.lang.String r1, java.lang.String r2, java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>");
        }

        java.net.URL findResource(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.findResource(java.lang.String):java.net.URL");
        }

        void findResources(java.lang.String r1, java.util.ArrayList<java.net.URL> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.findResources(java.lang.String, java.util.ArrayList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.findResources(java.lang.String, java.util.ArrayList):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.findResources(java.lang.String, java.util.ArrayList):void");
        }

        java.net.URL targetURL(java.net.URL r1, java.lang.String r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLHandler.targetURL(java.net.URL, java.lang.String):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLHandler.targetURL(java.net.URL, java.lang.String):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLHandler.targetURL(java.net.URL, java.lang.String):java.net.URL");
        }
    }

    class URLFileHandler extends URLHandler {
        private String prefix;
        final /* synthetic */ URLClassLoader this$0;

        public URLFileHandler(java.net.URLClassLoader r1, java.net.URL r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLFileHandler.<init>(java.net.URLClassLoader, java.net.URL):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLFileHandler.<init>(java.net.URLClassLoader, java.net.URL):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLFileHandler.<init>(java.net.URLClassLoader, java.net.URL):void");
        }

        java.lang.Class<?> findClass(java.lang.String r1, java.lang.String r2, java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLFileHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLFileHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLFileHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>");
        }

        java.net.URL findResource(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLFileHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLFileHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLFileHandler.findResource(java.lang.String):java.net.URL");
        }
    }

    class URLJarHandler extends URLHandler {
        final IndexFile index;
        final JarFile jf;
        final String prefixName;
        final Map<URL, URLHandler> subHandlers;
        final /* synthetic */ URLClassLoader this$0;

        public URLJarHandler(java.net.URLClassLoader r1, java.net.URL r2, java.net.URL r3, java.util.jar.JarFile r4, java.lang.String r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String):void");
        }

        public URLJarHandler(java.net.URLClassLoader r1, java.net.URL r2, java.net.URL r3, java.util.jar.JarFile r4, java.lang.String r5, java.net.URLClassLoader.IndexFile r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String, java.net.URLClassLoader$IndexFile):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String, java.net.URLClassLoader$IndexFile):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.<init>(java.net.URLClassLoader, java.net.URL, java.net.URL, java.util.jar.JarFile, java.lang.String, java.net.URLClassLoader$IndexFile):void");
        }

        private java.lang.Class<?> createClass(java.util.jar.JarEntry r1, java.util.jar.Manifest r2, java.lang.String r3, java.lang.String r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.createClass(java.util.jar.JarEntry, java.util.jar.Manifest, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.createClass(java.util.jar.JarEntry, java.util.jar.Manifest, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.createClass(java.util.jar.JarEntry, java.util.jar.Manifest, java.lang.String, java.lang.String):java.lang.Class<?>");
        }

        private java.net.URLClassLoader.URLHandler createURLSubJarHandler(java.net.URL r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.createURLSubJarHandler(java.net.URL):java.net.URLClassLoader$URLHandler
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.createURLSubJarHandler(java.net.URL):java.net.URLClassLoader$URLHandler
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.createURLSubJarHandler(java.net.URL):java.net.URLClassLoader$URLHandler");
        }

        private synchronized java.net.URLClassLoader.URLHandler getSubHandler(java.net.URL r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.getSubHandler(java.net.URL):java.net.URLClassLoader$URLHandler
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.getSubHandler(java.net.URL):java.net.URLClassLoader$URLHandler
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.getSubHandler(java.net.URL):java.net.URLClassLoader$URLHandler");
        }

        java.lang.Class<?> findClass(java.lang.String r1, java.lang.String r2, java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.findClass(java.lang.String, java.lang.String, java.lang.String):java.lang.Class<?>");
        }

        java.net.URL findResource(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.findResource(java.lang.String):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.findResource(java.lang.String):java.net.URL");
        }

        java.net.URL findResourceInOwn(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.findResourceInOwn(java.lang.String):java.net.URL
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.findResourceInOwn(java.lang.String):java.net.URL
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.findResourceInOwn(java.lang.String):java.net.URL");
        }

        void findResources(java.lang.String r1, java.util.ArrayList<java.net.URL> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.findResources(java.lang.String, java.util.ArrayList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.findResources(java.lang.String, java.util.ArrayList):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.findResources(java.lang.String, java.util.ArrayList):void");
        }

        java.net.URLClassLoader.IndexFile getIndex() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.URLClassLoader.URLJarHandler.getIndex():java.net.URLClassLoader$IndexFile
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.URLClassLoader.URLJarHandler.getIndex():java.net.URLClassLoader$IndexFile
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.URLJarHandler.getIndex():java.net.URLClassLoader$IndexFile");
        }
    }

    public URLClassLoader(URL[] urls) {
        this(urls, ClassLoader.getSystemClassLoader(), null);
    }

    public URLClassLoader(URL[] urls, ClassLoader parent) {
        this(urls, parent, null);
    }

    protected void addURL(URL url) {
        try {
            this.originalUrls.add(url);
            this.searchList.add(createSearchURL(url));
        } catch (MalformedURLException e) {
        }
    }

    public Enumeration<URL> findResources(String name) throws IOException {
        if (name == null) {
            return null;
        }
        ArrayList<URL> result = new ArrayList();
        int n = 0;
        while (true) {
            int n2 = n + 1;
            URLHandler handler = getHandler(n);
            if (handler == null) {
                return Collections.enumeration(result);
            }
            handler.findResources(name, result);
            n = n2;
        }
    }

    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection pc = super.getPermissions(codesource);
        URL u = codesource.getLocation();
        if (u.getProtocol().equals("jar")) {
            try {
                u = ((JarURLConnection) u.openConnection()).getJarFileURL();
            } catch (IOException e) {
            }
        }
        String host;
        if (u.getProtocol().equals("file")) {
            String path = u.getFile();
            host = u.getHost();
            if (host != null && host.length() > 0) {
                path = "//" + host + path;
            }
            if (File.separatorChar != '/') {
                path = path.replace('/', File.separatorChar);
            }
            if (isDirectory(u)) {
                pc.add(new FilePermission(path + "-", "read"));
            } else {
                pc.add(new FilePermission(path, "read"));
            }
        } else {
            host = u.getHost();
            if (host.length() == 0) {
                host = "localhost";
            }
            pc.add(new SocketPermission(host, "connect, accept"));
        }
        return pc;
    }

    public URL[] getURLs() {
        return (URL[]) this.originalUrls.toArray(new URL[this.originalUrls.size()]);
    }

    private static boolean isDirectory(URL url) {
        String file = url.getFile();
        return file.length() > 0 && file.charAt(file.length() - 1) == '/';
    }

    public static URLClassLoader newInstance(URL[] urls) {
        return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
    }

    public static URLClassLoader newInstance(URL[] urls, ClassLoader parentCl) {
        return new URLClassLoader(urls, parentCl);
    }

    public URLClassLoader(URL[] searchUrls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(parent);
        this.handlerMap = new HashMap();
        this.factory = factory;
        int nbUrls = searchUrls.length;
        this.originalUrls = new ArrayList(nbUrls);
        this.handlerList = new ArrayList(nbUrls);
        this.searchList = Collections.synchronizedList(new ArrayList(nbUrls));
        for (int i = 0; i < nbUrls; i++) {
            this.originalUrls.add(searchUrls[i]);
            try {
                this.searchList.add(createSearchURL(searchUrls[i]));
            } catch (MalformedURLException e) {
            }
        }
    }

    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String partialName = className.replace('.', '/');
        String classFileName = ".class";
        String packageName = null;
        int lastIndexOf = partialName.lastIndexOf(47);
        lastIndexOf = partialName.lastIndexOf(47);
        if (lastIndexOf != -1) {
            packageName = partialName.substring(0, lastIndexOf);
        }
        int n = 0;
        while (true) {
            int n2 = n + 1;
            URLHandler handler = getHandler(n);
            if (handler == null) {
                break;
            }
            Class<?> res = handler.findClass(packageName, classFileName, className);
            if (res != null) {
                return res;
            }
            n = n2;
        }
        throw new ClassNotFoundException(className);
    }

    private URL createSearchURL(URL url) throws MalformedURLException {
        if (url == null) {
            return url;
        }
        String protocol = url.getProtocol();
        if (isDirectory(url) || protocol.equals("jar")) {
            return url;
        }
        if (this.factory == null) {
            return new URL("jar", XmlPullParser.NO_NAMESPACE, -1, url.toString() + "!/");
        }
        return new URL("jar", XmlPullParser.NO_NAMESPACE, -1, url.toString() + "!/", this.factory.createURLStreamHandler("jar"));
    }

    public URL findResource(String name) {
        if (name == null) {
            return null;
        }
        int n = 0;
        while (true) {
            int n2 = n + 1;
            URLHandler handler = getHandler(n);
            if (handler == null) {
                return null;
            }
            URL res = handler.findResource(name);
            if (res != null) {
                return res;
            }
            n = n2;
        }
    }

    private URLHandler getHandler(int num) {
        if (num < this.handlerList.size()) {
            return (URLHandler) this.handlerList.get(num);
        }
        makeNewHandler();
        if (num < this.handlerList.size()) {
            return (URLHandler) this.handlerList.get(num);
        }
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void makeNewHandler() {
        /*
        r5 = this;
        monitor-enter(r5);
    L_0x0001:
        r3 = r5.searchList;	 Catch:{ all -> 0x001c }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x001c }
        if (r3 != 0) goto L_0x0043;
    L_0x0009:
        r3 = r5.searchList;	 Catch:{ all -> 0x001c }
        r4 = 0;
        r0 = r3.remove(r4);	 Catch:{ all -> 0x001c }
        r0 = (java.net.URL) r0;	 Catch:{ all -> 0x001c }
        if (r0 != 0) goto L_0x001f;
    L_0x0014:
        r3 = new java.lang.NullPointerException;	 Catch:{ all -> 0x001c }
        r4 = "nextCandidate == null";
        r3.<init>(r4);	 Catch:{ all -> 0x001c }
        throw r3;	 Catch:{ all -> 0x001c }
    L_0x001c:
        r3 = move-exception;
        monitor-exit(r5);
        throw r3;
    L_0x001f:
        r3 = r5.handlerMap;	 Catch:{ all -> 0x001c }
        r3 = r3.containsKey(r0);	 Catch:{ all -> 0x001c }
        if (r3 != 0) goto L_0x0001;
    L_0x0027:
        r1 = r0.getProtocol();	 Catch:{ all -> 0x001c }
        r3 = "jar";
        r3 = r1.equals(r3);	 Catch:{ all -> 0x001c }
        if (r3 == 0) goto L_0x0045;
    L_0x0033:
        r2 = r5.createURLJarHandler(r0);	 Catch:{ all -> 0x001c }
    L_0x0037:
        if (r2 == 0) goto L_0x0001;
    L_0x0039:
        r3 = r5.handlerMap;	 Catch:{ all -> 0x001c }
        r3.put(r0, r2);	 Catch:{ all -> 0x001c }
        r3 = r5.handlerList;	 Catch:{ all -> 0x001c }
        r3.add(r2);	 Catch:{ all -> 0x001c }
    L_0x0043:
        monitor-exit(r5);
        return;
    L_0x0045:
        r3 = "file";
        r3 = r1.equals(r3);	 Catch:{ all -> 0x001c }
        if (r3 == 0) goto L_0x0052;
    L_0x004d:
        r2 = r5.createURLFileHandler(r0);	 Catch:{ all -> 0x001c }
        goto L_0x0037;
    L_0x0052:
        r2 = r5.createURLHandler(r0);	 Catch:{ all -> 0x001c }
        goto L_0x0037;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.makeNewHandler():void");
    }

    private URLHandler createURLHandler(URL url) {
        return new URLHandler(this, url);
    }

    private URLHandler createURLFileHandler(URL url) {
        return new URLFileHandler(this, url);
    }

    private URLHandler createURLJarHandler(URL url) {
        String prefixName;
        String file = url.getFile();
        if (url.getFile().endsWith("!/")) {
            prefixName = XmlPullParser.NO_NAMESPACE;
        } else {
            int sepIdx = file.lastIndexOf("!/");
            if (sepIdx == -1) {
                return null;
            }
            prefixName = file.substring(sepIdx + 2);
        }
        try {
            URL jarURL = ((JarURLConnection) url.openConnection()).getJarFileURL();
            JarFile jf = ((JarURLConnection) new URL("jar", XmlPullParser.NO_NAMESPACE, jarURL.toExternalForm() + "!/").openConnection()).getJarFile();
            URLJarHandler jarH = new URLJarHandler(this, url, jarURL, jf, prefixName);
            if (jarH.getIndex() != null) {
                return jarH;
            }
            try {
                Manifest manifest = jf.getManifest();
                if (manifest == null) {
                    return jarH;
                }
                String classpath = manifest.getMainAttributes().getValue(Name.CLASS_PATH);
                if (classpath == null) {
                    return jarH;
                }
                this.searchList.addAll(0, getInternalURLs(url, classpath));
                return jarH;
            } catch (IOException e) {
                return jarH;
            }
        } catch (IOException e2) {
            return null;
        }
    }

    protected Package definePackage(String packageName, Manifest manifest, URL url) throws IllegalArgumentException {
        Attributes mainAttributes = manifest.getMainAttributes();
        String dirName = packageName.replace('.', '/') + "/";
        Attributes packageAttributes = manifest.getAttributes(dirName);
        boolean noEntry = false;
        if (packageAttributes == null) {
            noEntry = true;
            packageAttributes = mainAttributes;
        }
        String specificationTitle = packageAttributes.getValue(Name.SPECIFICATION_TITLE);
        if (specificationTitle == null && !noEntry) {
            specificationTitle = mainAttributes.getValue(Name.SPECIFICATION_TITLE);
        }
        String specificationVersion = packageAttributes.getValue(Name.SPECIFICATION_VERSION);
        if (specificationVersion == null && !noEntry) {
            specificationVersion = mainAttributes.getValue(Name.SPECIFICATION_VERSION);
        }
        String specificationVendor = packageAttributes.getValue(Name.SPECIFICATION_VENDOR);
        if (specificationVendor == null && !noEntry) {
            specificationVendor = mainAttributes.getValue(Name.SPECIFICATION_VENDOR);
        }
        String implementationTitle = packageAttributes.getValue(Name.IMPLEMENTATION_TITLE);
        if (implementationTitle == null && !noEntry) {
            implementationTitle = mainAttributes.getValue(Name.IMPLEMENTATION_TITLE);
        }
        String implementationVersion = packageAttributes.getValue(Name.IMPLEMENTATION_VERSION);
        if (implementationVersion == null && !noEntry) {
            implementationVersion = mainAttributes.getValue(Name.IMPLEMENTATION_VERSION);
        }
        String implementationVendor = packageAttributes.getValue(Name.IMPLEMENTATION_VENDOR);
        if (implementationVendor == null && !noEntry) {
            implementationVendor = mainAttributes.getValue(Name.IMPLEMENTATION_VENDOR);
        }
        return definePackage(packageName, specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, isSealed(manifest, dirName) ? url : null);
    }

    private boolean isSealed(Manifest manifest, String dirName) {
        String value;
        Attributes attributes = manifest.getAttributes(dirName);
        if (attributes != null) {
            value = attributes.getValue(Name.SEALED);
            if (value != null) {
                return value.equalsIgnoreCase(Unpacker.TRUE);
            }
        }
        value = manifest.getMainAttributes().getValue(Name.SEALED);
        return value != null && value.equalsIgnoreCase(Unpacker.TRUE);
    }

    private ArrayList<URL> getInternalURLs(URL root, String classpath) {
        StringTokenizer tokenizer = new StringTokenizer(classpath);
        ArrayList<URL> addedURLs = new ArrayList();
        String file = root.getFile();
        int jarIndex = file.lastIndexOf("!/") - 1;
        int index = file.lastIndexOf("/", jarIndex) + 1;
        if (index == 0) {
            index = file.lastIndexOf(System.getProperty("file.separator"), jarIndex) + 1;
        }
        file = file.substring(0, index);
        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken();
            if (!element.isEmpty()) {
                try {
                    addedURLs.add(createSearchURL(new URL(new URL(file), element)));
                } catch (MalformedURLException e) {
                }
            }
        }
        return addedURLs;
    }
}
