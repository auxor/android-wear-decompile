package com.google.common.reflect;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

public final class ClassPath {
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR;
    private static final Predicate<ClassInfo> IS_TOP_LEVEL;
    private static final Logger logger;

    public static class ResourceInfo {
        final ClassLoader loader;
        private final String resourceName;

        ResourceInfo(String str, ClassLoader classLoader) {
            String str2 = (String) Preconditions.checkNotNull(str);
            throw new VerifyError("bad dex opcode");
        }

        static ResourceInfo of(String str, ClassLoader classLoader) {
            return str.endsWith(".class") ? new ClassInfo(str, classLoader) : new ResourceInfo(str, classLoader);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ResourceInfo)) {
                return false;
            }
            throw new VerifyError("bad dex opcode");
        }

        public int hashCode() {
            return this.resourceName.hashCode();
        }

        public String toString() {
            return this.resourceName;
        }
    }

    public static final class ClassInfo extends ResourceInfo {
        private final String className;

        ClassInfo(String str, ClassLoader classLoader) {
            super(str, classLoader);
            this.className = ClassPath.getClassName(str);
            throw new VerifyError("bad dex opcode");
        }

        public String toString() {
            return this.className;
        }
    }

    static final class Scanner {
        private final Builder<ResourceInfo> resources;
        private final Set<URI> scannedUris;

        Scanner() {
            this.resources = new Builder(Ordering.usingToString());
            this.scannedUris = Sets.newHashSet();
            throw new VerifyError("bad dex opcode");
        }

        static URI getClassPathEntry(File file, String str) throws URISyntaxException {
            URI uri = new URI(str);
            return uri.isAbsolute() ? uri : new File(file.getParentFile(), str.replace('/', File.separatorChar)).toURI();
        }

        static ImmutableSet<URI> getClassPathFromManifest(File file, Manifest manifest) {
            if (manifest == null) {
                return ImmutableSet.of();
            }
            ImmutableSet.builder();
            throw new VerifyError("bad dex opcode");
        }

        private void scanDirectory(File file, ClassLoader classLoader) throws IOException {
            scanDirectory(file, classLoader, "", ImmutableSet.of());
        }

        private void scanDirectory(File file, ClassLoader classLoader, String str, ImmutableSet<File> immutableSet) throws IOException {
            Object canonicalFile = file.getCanonicalFile();
            if (!immutableSet.contains(canonicalFile)) {
                File[] listFiles = file.listFiles();
                if (listFiles == null) {
                    ClassPath.logger.warning("Cannot read directory " + file);
                    return;
                }
                ImmutableSet build = ImmutableSet.builder().addAll((Iterable) immutableSet).add(canonicalFile).build();
                for (File file2 : listFiles) {
                    String name = file2.getName();
                    if (file2.isDirectory()) {
                        scanDirectory(file2, classLoader, str + name + "/", build);
                    } else {
                        String str2 = str + name;
                        if (!str2.equals("META-INF/MANIFEST.MF")) {
                            this.resources.add(ResourceInfo.of(str2, classLoader));
                        }
                    }
                }
            }
        }

        private void scanJar(File file, ClassLoader classLoader) throws IOException {
            try {
                JarFile jarFile = new JarFile(file);
                try {
                    throw new VerifyError("bad dex opcode");
                } catch (IOException e) {
                    throw th;
                } catch (Throwable th) {
                    VerifyError verifyError = new VerifyError("bad dex opcode");
                }
            } catch (IOException e2) {
            }
        }

        void scan(URI uri, ClassLoader classLoader) throws IOException {
            if (uri.getScheme().equals("file") && this.scannedUris.add(uri)) {
                scanFrom(new File(uri), classLoader);
            }
        }

        void scanFrom(File file, ClassLoader classLoader) throws IOException {
            if (!file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                scanDirectory(file, classLoader);
            } else {
                scanJar(file, classLoader);
            }
        }
    }

    static {
        logger = Logger.getLogger(ClassPath.class.getName());
        IS_TOP_LEVEL = new Predicate<ClassInfo>() {
            public boolean apply(ClassInfo classInfo) {
                return classInfo.className.indexOf(36) == -1;
            }
        };
        CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
    }

    static String getClassName(String str) {
        return str.substring(0, str.length() - ".class".length()).replace('/', '.');
    }

    static ImmutableMap<URI, ClassLoader> getClassPathEntries(ClassLoader classLoader) {
        Maps.newLinkedHashMap();
        throw new VerifyError("bad dex opcode");
    }
}
