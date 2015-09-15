package dalvik.system;

import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.StructStat;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;
import libcore.io.IoUtils;
import libcore.io.Libcore;

final class DexPathList {
    private static final String DEX_SUFFIX = ".dex";
    private final ClassLoader definingContext;
    private final Element[] dexElements;
    private final IOException[] dexElementsSuppressedExceptions;
    private final File[] nativeLibraryDirectories;

    static class Element {
        private final DexFile dexFile;
        private final File file;
        private boolean initialized;
        private final boolean isDirectory;
        private final File zip;
        private ZipFile zipFile;

        public Element(File file, boolean isDirectory, File zip, DexFile dexFile) {
            this.file = file;
            this.isDirectory = isDirectory;
            this.zip = zip;
            this.dexFile = dexFile;
        }

        public String toString() {
            if (this.isDirectory) {
                return "directory \"" + this.file + "\"";
            }
            if (this.zip != null) {
                return "zip file \"" + this.zip + "\"";
            }
            return "dex file \"" + this.dexFile + "\"";
        }

        public synchronized void maybeInit() {
            if (!this.initialized) {
                this.initialized = true;
                if (!(this.isDirectory || this.zip == null)) {
                    try {
                        this.zipFile = new ZipFile(this.zip);
                    } catch (IOException ioe) {
                        System.logE("Unable to open zip file: " + this.file, ioe);
                        this.zipFile = null;
                    }
                }
            }
        }

        public URL findResource(String name) {
            maybeInit();
            if (this.isDirectory) {
                File resourceFile = new File(this.file, name);
                if (resourceFile.exists()) {
                    try {
                        return resourceFile.toURI().toURL();
                    } catch (Throwable ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            if (this.zipFile == null || this.zipFile.getEntry(name) == null) {
                return null;
            }
            try {
                return new URL("jar:" + this.file.toURL() + "!/" + name);
            } catch (Throwable ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public DexPathList(ClassLoader definingContext, String dexPath, String libraryPath, File optimizedDirectory) {
        if (definingContext == null) {
            throw new NullPointerException("definingContext == null");
        } else if (dexPath == null) {
            throw new NullPointerException("dexPath == null");
        } else {
            if (optimizedDirectory != null) {
                if (!optimizedDirectory.exists()) {
                    throw new IllegalArgumentException("optimizedDirectory doesn't exist: " + optimizedDirectory);
                } else if (!(optimizedDirectory.canRead() && optimizedDirectory.canWrite())) {
                    throw new IllegalArgumentException("optimizedDirectory not readable/writable: " + optimizedDirectory);
                }
            }
            this.definingContext = definingContext;
            ArrayList<IOException> suppressedExceptions = new ArrayList();
            this.dexElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory, suppressedExceptions);
            if (suppressedExceptions.size() > 0) {
                this.dexElementsSuppressedExceptions = (IOException[]) suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
            } else {
                this.dexElementsSuppressedExceptions = null;
            }
            this.nativeLibraryDirectories = splitLibraryPath(libraryPath);
        }
    }

    public String toString() {
        return "DexPathList[" + Arrays.toString(this.dexElements) + ",nativeLibraryDirectories=" + Arrays.toString(this.nativeLibraryDirectories) + "]";
    }

    public File[] getNativeLibraryDirectories() {
        return this.nativeLibraryDirectories;
    }

    private static ArrayList<File> splitDexPath(String path) {
        return splitPaths(path, null, false);
    }

    private static File[] splitLibraryPath(String path) {
        ArrayList<File> result = splitPaths(path, System.getProperty("java.library.path"), true);
        return (File[]) result.toArray(new File[result.size()]);
    }

    private static ArrayList<File> splitPaths(String path1, String path2, boolean wantDirectories) {
        ArrayList<File> result = new ArrayList();
        splitAndAdd(path1, wantDirectories, result);
        splitAndAdd(path2, wantDirectories, result);
        return result;
    }

    private static void splitAndAdd(String searchPath, boolean directoriesOnly, ArrayList<File> resultList) {
        if (searchPath != null) {
            String[] arr$ = searchPath.split(":");
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                String path = arr$[i$];
                try {
                    StructStat sb = Libcore.os.stat(path);
                    if (!directoriesOnly || OsConstants.S_ISDIR(sb.st_mode)) {
                        resultList.add(new File(path));
                        i$++;
                    } else {
                        i$++;
                    }
                } catch (ErrnoException e) {
                }
            }
        }
    }

    private static Element[] makeDexElements(ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions) {
        ArrayList<Element> elements = new ArrayList();
        Iterator i$ = files.iterator();
        while (i$.hasNext()) {
            Object file = (File) i$.next();
            File zip = null;
            DexFile dex = null;
            String name = file.getName();
            if (file.isDirectory()) {
                elements.add(new Element(file, true, null, null));
            } else if (!file.isFile()) {
                System.logW("ClassLoader referenced unknown path: " + file);
            } else if (name.endsWith(DEX_SUFFIX)) {
                try {
                    dex = loadDexFile(file, optimizedDirectory);
                } catch (IOException ex) {
                    System.logE("Unable to load dex file: " + file, ex);
                }
            } else {
                zip = file;
                try {
                    dex = loadDexFile(file, optimizedDirectory);
                } catch (IOException suppressed) {
                    suppressedExceptions.add(suppressed);
                }
            }
            if (zip != null || dex != null) {
                elements.add(new Element(file, false, zip, dex));
            }
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }

    private static DexFile loadDexFile(File file, File optimizedDirectory) throws IOException {
        if (optimizedDirectory == null) {
            return new DexFile(file);
        }
        return DexFile.loadDex(file.getPath(), optimizedPathFor(file, optimizedDirectory), 0);
    }

    private static String optimizedPathFor(File path, File optimizedDirectory) {
        String fileName;
        CharSequence fileName2 = path.getName();
        if (!fileName2.endsWith(DEX_SUFFIX)) {
            int lastDot = fileName2.lastIndexOf(".");
            if (lastDot < 0) {
                fileName = fileName2 + DEX_SUFFIX;
            } else {
                StringBuilder sb = new StringBuilder(lastDot + 4);
                sb.append(fileName2, 0, lastDot);
                sb.append(DEX_SUFFIX);
                fileName = sb.toString();
            }
        }
        return new File(optimizedDirectory, fileName).getPath();
    }

    public Class findClass(String name, List<Throwable> suppressed) {
        for (Element element : this.dexElements) {
            DexFile dex = element.dexFile;
            if (dex != null) {
                Class clazz = dex.loadClassBinaryName(name, this.definingContext, suppressed);
                if (clazz != null) {
                    return clazz;
                }
            }
        }
        if (this.dexElementsSuppressedExceptions != null) {
            suppressed.addAll(Arrays.asList(this.dexElementsSuppressedExceptions));
        }
        return null;
    }

    public URL findResource(String name) {
        for (Element element : this.dexElements) {
            URL url = element.findResource(name);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    public Enumeration<URL> findResources(String name) {
        ArrayList<URL> result = new ArrayList();
        for (Element element : this.dexElements) {
            URL url = element.findResource(name);
            if (url != null) {
                result.add(url);
            }
        }
        return Collections.enumeration(result);
    }

    public String findLibrary(String libraryName) {
        String fileName = System.mapLibraryName(libraryName);
        for (File directory : this.nativeLibraryDirectories) {
            String path = new File(directory, fileName).getPath();
            if (IoUtils.canOpenReadOnly(path)) {
                return path;
            }
        }
        return null;
    }
}
