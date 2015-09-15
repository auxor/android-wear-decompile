package libcore.net.url;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.xmlpull.v1.XmlPullParser;

public class JarHandler extends URLStreamHandler {
    protected URLConnection openConnection(URL u) throws IOException {
        return new JarURLConnectionImpl(u);
    }

    protected void parseURL(URL url, String spec, int start, int limit) {
        String file = url.getFile();
        if (file == null) {
            file = XmlPullParser.NO_NAMESPACE;
        }
        if (limit > start) {
            spec = spec.substring(start, limit);
        } else {
            spec = XmlPullParser.NO_NAMESPACE;
        }
        if (spec.indexOf("!/") == -1 && file.indexOf("!/") == -1) {
            throw new NullPointerException("Cannot find \"!/\"");
        }
        if (file.isEmpty()) {
            file = spec;
        } else if (spec.charAt(0) == '/') {
            file = file.substring(0, file.indexOf(33) + 1) + spec;
        } else {
            int idx = file.indexOf(33);
            file = file.substring(0, idx + 1) + UrlUtils.canonicalizePath(file.substring(idx + 1, file.lastIndexOf(47) + 1) + spec, true);
        }
        try {
            URL url2 = new URL(file);
            setURL(url, "jar", XmlPullParser.NO_NAMESPACE, -1, null, null, file, null, null);
        } catch (MalformedURLException e) {
            throw new NullPointerException(e.toString());
        }
    }

    protected String toExternalForm(URL url) {
        StringBuilder sb = new StringBuilder();
        sb.append("jar:");
        sb.append(url.getFile());
        String ref = url.getRef();
        if (ref != null) {
            sb.append(ref);
        }
        return sb.toString();
    }
}
