package java.net;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import libcore.net.UriCodec;

public abstract class JarURLConnection extends URLConnection {
    private String entryName;
    private String file;
    private URL fileURL;
    protected URLConnection jarFileURLConnection;

    public abstract JarFile getJarFile() throws IOException;

    protected JarURLConnection(URL url) throws MalformedURLException {
        super(url);
        this.file = decode(url.getFile());
        int sepIdx = this.file.indexOf("!/");
        if (sepIdx < 0) {
            throw new MalformedURLException();
        }
        this.fileURL = new URL(this.file.substring(0, sepIdx));
        sepIdx += 2;
        if (this.file.length() != sepIdx) {
            this.entryName = this.file.substring(sepIdx, this.file.length());
            if (url.getRef() != null) {
                this.entryName += "#" + url.getRef();
            }
        }
    }

    public Attributes getAttributes() throws IOException {
        JarEntry jEntry = getJarEntry();
        return jEntry == null ? null : jEntry.getAttributes();
    }

    public Certificate[] getCertificates() throws IOException {
        JarEntry jEntry = getJarEntry();
        if (jEntry == null) {
            return null;
        }
        return jEntry.getCertificates();
    }

    public String getEntryName() {
        return this.entryName;
    }

    public JarEntry getJarEntry() throws IOException {
        if (!this.connected) {
            connect();
        }
        if (this.entryName == null) {
            return null;
        }
        return getJarFile().getJarEntry(this.entryName);
    }

    public Manifest getManifest() throws IOException {
        return (Manifest) getJarFile().getManifest().clone();
    }

    public URL getJarFileURL() {
        return this.fileURL;
    }

    public Attributes getMainAttributes() throws IOException {
        Manifest m = getJarFile().getManifest();
        return m == null ? null : m.getMainAttributes();
    }

    private static String decode(String encoded) throws MalformedURLException {
        try {
            return UriCodec.decode(encoded, false, StandardCharsets.UTF_8, true);
        } catch (IllegalArgumentException e) {
            throw new MalformedURLException("Unable to decode URL", e);
        }
    }
}
