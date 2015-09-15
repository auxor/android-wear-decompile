package libcore.net.url;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import libcore.net.UriCodec;

public class JarURLConnectionImpl extends JarURLConnection {
    private static final HashMap<URL, JarFile> jarCache;
    private boolean closed;
    private JarEntry jarEntry;
    private JarFile jarFile;
    private URL jarFileURL;
    private InputStream jarInput;

    private class JarURLConnectionInputStream extends FilterInputStream {
        final JarFile jarFile;

        protected JarURLConnectionInputStream(InputStream in, JarFile file) {
            super(in);
            this.jarFile = file;
        }

        public void close() throws IOException {
            super.close();
            if (!JarURLConnectionImpl.this.getUseCaches()) {
                JarURLConnectionImpl.this.closed = true;
                this.jarFile.close();
            }
        }
    }

    static {
        jarCache = new HashMap();
    }

    public JarURLConnectionImpl(URL url) throws MalformedURLException, IOException {
        super(url);
        this.jarFileURL = getJarFileURL();
        this.jarFileURLConnection = this.jarFileURL.openConnection();
    }

    public void connect() throws IOException {
        if (!this.connected) {
            findJarFile();
            findJarEntry();
            this.connected = true;
        }
    }

    public JarFile getJarFile() throws IOException {
        connect();
        return this.jarFile;
    }

    private void findJarFile() throws IOException {
        if (getUseCaches()) {
            synchronized (jarCache) {
                this.jarFile = (JarFile) jarCache.get(this.jarFileURL);
            }
            if (this.jarFile == null) {
                JarFile jar = openJarFile();
                synchronized (jarCache) {
                    this.jarFile = (JarFile) jarCache.get(this.jarFileURL);
                    if (this.jarFile == null) {
                        jarCache.put(this.jarFileURL, jar);
                        this.jarFile = jar;
                    } else {
                        jar.close();
                    }
                }
            }
        } else {
            this.jarFile = openJarFile();
        }
        if (this.jarFile == null) {
            throw new IOException();
        }
    }

    private JarFile openJarFile() throws IOException {
        Throwable th;
        Throwable th2;
        if (this.jarFileURL.getProtocol().equals("file")) {
            return new JarFile(new File(UriCodec.decode(this.jarFileURL.getFile())), true, 1);
        }
        InputStream is = this.jarFileURL.openConnection().getInputStream();
        FileOutputStream fos = null;
        try {
            File tempJar = File.createTempFile("hyjar_", ".tmp", null);
            tempJar.deleteOnExit();
            FileOutputStream fos2 = new FileOutputStream(tempJar);
            try {
                byte[] buf = new byte[Modifier.SYNTHETIC];
                while (true) {
                    int nbytes = is.read(buf);
                    if (nbytes <= -1) {
                        break;
                    }
                    fos2.write(buf, 0, nbytes);
                }
                fos2.close();
                JarFile jarFile = new JarFile(tempJar, true, 5);
                if (fos2 != null) {
                    try {
                        fos2.close();
                    } catch (IOException e) {
                        if (is == null) {
                            return null;
                        }
                        is.close();
                        return null;
                    } catch (Throwable th3) {
                        th = th3;
                        fos = fos2;
                        if (is != null) {
                            is.close();
                        }
                        throw th;
                    }
                }
                if (is != null) {
                    is.close();
                }
                return jarFile;
            } catch (IOException e2) {
                fos = fos2;
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e3) {
                        if (is == null) {
                            return null;
                        }
                        is.close();
                        return null;
                    }
                }
                if (is != null) {
                    return null;
                }
                is.close();
                return null;
            } catch (Throwable th4) {
                th2 = th4;
                fos = fos2;
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e4) {
                        if (is == null) {
                            return null;
                        }
                        is.close();
                        return null;
                    }
                }
                try {
                    throw th2;
                } catch (Throwable th5) {
                    th = th5;
                }
            }
        } catch (IOException e5) {
            if (fos != null) {
                fos.close();
            }
            if (is != null) {
                return null;
            }
            is.close();
            return null;
        } catch (Throwable th6) {
            th2 = th6;
            if (fos != null) {
                fos.close();
            }
            throw th2;
        }
    }

    public JarEntry getJarEntry() throws IOException {
        connect();
        return this.jarEntry;
    }

    private void findJarEntry() throws IOException {
        if (getEntryName() != null) {
            this.jarEntry = this.jarFile.getJarEntry(getEntryName());
            if (this.jarEntry == null) {
                throw new FileNotFoundException(getEntryName());
            }
        }
    }

    public InputStream getInputStream() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("JarURLConnection InputStream has been closed");
        }
        connect();
        if (this.jarInput != null) {
            return this.jarInput;
        }
        if (this.jarEntry == null) {
            throw new IOException("Jar entry not specified");
        }
        InputStream jarURLConnectionInputStream = new JarURLConnectionInputStream(this.jarFile.getInputStream(this.jarEntry), this.jarFile);
        this.jarInput = jarURLConnectionInputStream;
        return jarURLConnectionInputStream;
    }

    public String getContentType() {
        if (this.url.getFile().endsWith("!/")) {
            return "x-java/jar";
        }
        String cType = null;
        String entryName = getEntryName();
        if (entryName != null) {
            cType = URLConnection.guessContentTypeFromName(entryName);
        } else {
            try {
                connect();
                cType = this.jarFileURLConnection.getContentType();
            } catch (IOException e) {
            }
        }
        if (cType == null) {
            return "content/unknown";
        }
        return cType;
    }

    public int getContentLength() {
        try {
            connect();
            if (this.jarEntry == null) {
                return this.jarFileURLConnection.getContentLength();
            }
            return (int) getJarEntry().getSize();
        } catch (IOException e) {
            return -1;
        }
    }

    public Object getContent() throws IOException {
        connect();
        if (this.jarEntry == null) {
            return this.jarFile;
        }
        return super.getContent();
    }

    public Permission getPermission() throws IOException {
        return this.jarFileURLConnection.getPermission();
    }

    public boolean getUseCaches() {
        return this.jarFileURLConnection.getUseCaches();
    }

    public void setUseCaches(boolean usecaches) {
        this.jarFileURLConnection.setUseCaches(usecaches);
    }

    public boolean getDefaultUseCaches() {
        return this.jarFileURLConnection.getDefaultUseCaches();
    }

    public void setDefaultUseCaches(boolean defaultusecaches) {
        this.jarFileURLConnection.setDefaultUseCaches(defaultusecaches);
    }
}
