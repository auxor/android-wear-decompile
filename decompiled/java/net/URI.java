package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import libcore.net.UriCodec;
import libcore.net.url.UrlUtils;
import org.xmlpull.v1.XmlPullParser;

public final class URI implements Comparable<URI>, Serializable {
    static final UriCodec ALL_LEGAL_ENCODER;
    private static final UriCodec ASCII_ONLY;
    static final UriCodec AUTHORITY_ENCODER;
    static final UriCodec FILE_AND_QUERY_ENCODER;
    static final UriCodec PATH_ENCODER;
    static final String PUNCTUATION = ",;:$&+=";
    static final String UNRESERVED = "_-!.~'()*";
    static final UriCodec USER_INFO_ENCODER;
    private static final long serialVersionUID = -6052424284110960213L;
    private transient boolean absolute;
    private transient String authority;
    private transient String fragment;
    private transient int hash;
    private transient String host;
    private transient boolean opaque;
    private transient String path;
    private transient int port;
    private transient String query;
    private transient String scheme;
    private transient String schemeSpecificPart;
    private transient boolean serverAuthority;
    private String string;
    private transient String userInfo;

    private static class PartEncoder extends UriCodec {
        private final String extraLegalCharacters;

        PartEncoder(String extraLegalCharacters) {
            this.extraLegalCharacters = extraLegalCharacters;
        }

        protected boolean isRetained(char c) {
            return (URI.UNRESERVED.indexOf((int) c) == -1 && URI.PUNCTUATION.indexOf((int) c) == -1 && this.extraLegalCharacters.indexOf((int) c) == -1 && (c <= '\u007f' || Character.isSpaceChar(c) || Character.isISOControl(c))) ? false : true;
        }
    }

    static {
        USER_INFO_ENCODER = new PartEncoder(XmlPullParser.NO_NAMESPACE);
        PATH_ENCODER = new PartEncoder("/@");
        AUTHORITY_ENCODER = new PartEncoder("@[]");
        FILE_AND_QUERY_ENCODER = new PartEncoder("/@?");
        ALL_LEGAL_ENCODER = new PartEncoder("?/[]@");
        ASCII_ONLY = new UriCodec() {
            protected boolean isRetained(char c) {
                return c <= '\u007f';
            }
        };
    }

    private URI() {
        this.port = -1;
        this.serverAuthority = false;
        this.hash = -1;
    }

    public URI(String spec) throws URISyntaxException {
        this.port = -1;
        this.serverAuthority = false;
        this.hash = -1;
        parseURI(spec, false);
    }

    public URI(String scheme, String schemeSpecificPart, String fragment) throws URISyntaxException {
        this.port = -1;
        this.serverAuthority = false;
        this.hash = -1;
        StringBuilder uri = new StringBuilder();
        if (scheme != null) {
            uri.append(scheme);
            uri.append(':');
        }
        if (schemeSpecificPart != null) {
            ALL_LEGAL_ENCODER.appendEncoded(uri, schemeSpecificPart);
        }
        if (fragment != null) {
            uri.append('#');
            ALL_LEGAL_ENCODER.appendEncoded(uri, fragment);
        }
        parseURI(uri.toString(), false);
    }

    public URI(String scheme, String userInfo, String host, int port, String path, String query, String fragment) throws URISyntaxException {
        this.port = -1;
        this.serverAuthority = false;
        this.hash = -1;
        if (scheme == null && userInfo == null && host == null && path == null && query == null && fragment == null) {
            this.path = XmlPullParser.NO_NAMESPACE;
        } else if (scheme == null || path == null || path.isEmpty() || path.charAt(0) == '/') {
            StringBuilder uri = new StringBuilder();
            if (scheme != null) {
                uri.append(scheme);
                uri.append(':');
            }
            if (!(userInfo == null && host == null && port == -1)) {
                uri.append("//");
            }
            if (userInfo != null) {
                USER_INFO_ENCODER.appendEncoded(uri, userInfo);
                uri.append('@');
            }
            if (host != null) {
                if (host.indexOf(58) != -1 && host.indexOf(93) == -1 && host.indexOf(91) == -1) {
                    host = "[" + host + "]";
                }
                uri.append(host);
            }
            if (port != -1) {
                uri.append(':');
                uri.append(port);
            }
            if (path != null) {
                PATH_ENCODER.appendEncoded(uri, path);
            }
            if (query != null) {
                uri.append('?');
                ALL_LEGAL_ENCODER.appendEncoded(uri, query);
            }
            if (fragment != null) {
                uri.append('#');
                ALL_LEGAL_ENCODER.appendEncoded(uri, fragment);
            }
            parseURI(uri.toString(), true);
        } else {
            throw new URISyntaxException(path, "Relative path");
        }
    }

    public URI(String scheme, String host, String path, String fragment) throws URISyntaxException {
        this(scheme, null, host, -1, path, null, fragment);
    }

    public URI(String scheme, String authority, String path, String query, String fragment) throws URISyntaxException {
        this.port = -1;
        this.serverAuthority = false;
        this.hash = -1;
        if (scheme == null || path == null || path.isEmpty() || path.charAt(0) == '/') {
            StringBuilder uri = new StringBuilder();
            if (scheme != null) {
                uri.append(scheme);
                uri.append(':');
            }
            if (authority != null) {
                uri.append("//");
                AUTHORITY_ENCODER.appendEncoded(uri, authority);
            }
            if (path != null) {
                PATH_ENCODER.appendEncoded(uri, path);
            }
            if (query != null) {
                uri.append('?');
                ALL_LEGAL_ENCODER.appendEncoded(uri, query);
            }
            if (fragment != null) {
                uri.append('#');
                ALL_LEGAL_ENCODER.appendEncoded(uri, fragment);
            }
            parseURI(uri.toString(), false);
            return;
        }
        throw new URISyntaxException(path, "Relative path");
    }

    private void parseURI(String uri, boolean forceServer) throws URISyntaxException {
        int start;
        int fileStart;
        this.string = uri;
        int fragmentStart = UrlUtils.findFirstOf(uri, "#", 0, uri.length());
        if (fragmentStart < uri.length()) {
            this.fragment = ALL_LEGAL_ENCODER.validate(uri, fragmentStart + 1, uri.length(), "fragment");
        }
        int colon = UrlUtils.findFirstOf(uri, ":", 0, fragmentStart);
        if (colon < UrlUtils.findFirstOf(uri, "/?#", 0, fragmentStart)) {
            this.absolute = true;
            this.scheme = validateScheme(uri, colon);
            start = colon + 1;
            if (start == fragmentStart) {
                throw new URISyntaxException(uri, "Scheme-specific part expected", start);
            } else if (!uri.regionMatches(start, "/", 0, 1)) {
                this.opaque = true;
                this.schemeSpecificPart = ALL_LEGAL_ENCODER.validate(uri, start, fragmentStart, "scheme specific part");
                return;
            }
        }
        this.absolute = false;
        start = 0;
        this.opaque = false;
        this.schemeSpecificPart = uri.substring(start, fragmentStart);
        if (uri.regionMatches(start, "//", 0, 2)) {
            int authorityStart = start + 2;
            fileStart = UrlUtils.findFirstOf(uri, "/?", authorityStart, fragmentStart);
            if (authorityStart == uri.length()) {
                throw new URISyntaxException(uri, "Authority expected", uri.length());
            } else if (authorityStart < fileStart) {
                this.authority = AUTHORITY_ENCODER.validate(uri, authorityStart, fileStart, "authority");
            }
        } else {
            fileStart = start;
        }
        int queryStart = UrlUtils.findFirstOf(uri, "?", fileStart, fragmentStart);
        this.path = PATH_ENCODER.validate(uri, fileStart, queryStart, "path");
        if (queryStart < fragmentStart) {
            this.query = ALL_LEGAL_ENCODER.validate(uri, queryStart + 1, fragmentStart, "query");
        }
        parseAuthority(forceServer);
    }

    private String validateScheme(String uri, int end) throws URISyntaxException {
        if (end == 0) {
            throw new URISyntaxException(uri, "Scheme expected", 0);
        }
        int i = 0;
        while (i < end) {
            if (UrlUtils.isValidSchemeChar(i, uri.charAt(i))) {
                i++;
            } else {
                throw new URISyntaxException(uri, "Illegal character in scheme", 0);
            }
        }
        return uri.substring(0, end);
    }

    private void parseAuthority(boolean forceServer) throws URISyntaxException {
        if (this.authority != null) {
            String tempHost;
            String tempUserInfo = null;
            String temp = this.authority;
            int index = temp.indexOf(64);
            int hostIndex = 0;
            if (index != -1) {
                tempUserInfo = temp.substring(0, index);
                validateUserInfo(this.authority, tempUserInfo, 0);
                temp = temp.substring(index + 1);
                hostIndex = index + 1;
            }
            index = temp.lastIndexOf(58);
            int endIndex = temp.indexOf(93);
            int tempPort = -1;
            if (index == -1 || endIndex >= index) {
                tempHost = temp;
            } else {
                tempHost = temp.substring(0, index);
                if (index < temp.length() - 1) {
                    try {
                        char firstPortChar = temp.charAt(index + 1);
                        if (firstPortChar >= '0' && firstPortChar <= '9') {
                            tempPort = Integer.parseInt(temp.substring(index + 1));
                        } else if (forceServer) {
                            throw new URISyntaxException(this.authority, "Invalid port number", (hostIndex + index) + 1);
                        } else {
                            return;
                        }
                    } catch (NumberFormatException e) {
                        if (forceServer) {
                            throw new URISyntaxException(this.authority, "Invalid port number", (hostIndex + index) + 1);
                        }
                        return;
                    }
                }
            }
            if (tempHost.isEmpty()) {
                if (forceServer) {
                    throw new URISyntaxException(this.authority, "Expected host", hostIndex);
                }
            } else if (isValidHost(forceServer, tempHost)) {
                this.userInfo = tempUserInfo;
                this.host = tempHost;
                this.port = tempPort;
                this.serverAuthority = true;
            }
        }
    }

    private void validateUserInfo(String uri, String userInfo, int index) throws URISyntaxException {
        for (int i = 0; i < userInfo.length(); i++) {
            char ch = userInfo.charAt(i);
            if (ch == ']' || ch == '[') {
                throw new URISyntaxException(uri, "Illegal character in userInfo", index + i);
            }
        }
    }

    private boolean isValidHost(boolean forceServer, String host) throws URISyntaxException {
        if (host.startsWith("[")) {
            if (!host.endsWith("]")) {
                throw new URISyntaxException(host, "Expected a closing square bracket for IPv6 address", 0);
            } else if (InetAddress.isNumeric(host)) {
                return true;
            } else {
                throw new URISyntaxException(host, "Malformed IPv6 address");
            }
        } else if (host.indexOf(91) == -1 && host.indexOf(93) == -1) {
            int index = host.lastIndexOf(46);
            if (index >= 0 && index != host.length() - 1 && Character.isDigit(host.charAt(index + 1))) {
                try {
                    if (InetAddress.parseNumericAddress(host) instanceof Inet4Address) {
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                }
                if (!forceServer) {
                    return false;
                }
                throw new URISyntaxException(host, "Malformed IPv4 address", 0);
            } else if (isValidDomainName(host)) {
                return true;
            } else {
                if (!forceServer) {
                    return false;
                }
                throw new URISyntaxException(host, "Illegal character in host name", 0);
            }
        } else {
            throw new URISyntaxException(host, "Illegal character in host name", 0);
        }
    }

    private boolean isValidDomainName(String host) {
        try {
            UriCodec.validateSimple(host, "_-.");
            String lastLabel = null;
            for (String lastLabel2 : host.split("\\.")) {
                if (lastLabel2.startsWith("-") || lastLabel2.endsWith("-")) {
                    return false;
                }
            }
            if (lastLabel2 == null) {
                return false;
            }
            if (!lastLabel2.equals(host)) {
                char ch = lastLabel2.charAt(0);
                if (ch >= '0' && ch <= '9') {
                    return false;
                }
            }
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public int compareTo(URI uri) {
        if (this.scheme == null && uri.scheme != null) {
            return -1;
        }
        if (this.scheme != null && uri.scheme == null) {
            return 1;
        }
        int ret;
        if (!(this.scheme == null || uri.scheme == null)) {
            ret = this.scheme.compareToIgnoreCase(uri.scheme);
            if (ret != 0) {
                return ret;
            }
        }
        if (!this.opaque && uri.opaque) {
            return -1;
        }
        if (this.opaque && !uri.opaque) {
            return 1;
        }
        if (this.opaque && uri.opaque) {
            ret = this.schemeSpecificPart.compareTo(uri.schemeSpecificPart);
            if (ret != 0) {
                return ret;
            }
        } else if (this.authority != null && uri.authority == null) {
            return 1;
        } else {
            if (this.authority == null && uri.authority != null) {
                return -1;
            }
            if (!(this.authority == null || uri.authority == null)) {
                if (this.host == null || uri.host == null) {
                    ret = this.authority.compareTo(uri.authority);
                    if (ret != 0) {
                        return ret;
                    }
                } else if (this.userInfo != null && uri.userInfo == null) {
                    return 1;
                } else {
                    if (this.userInfo == null && uri.userInfo != null) {
                        return -1;
                    }
                    if (!(this.userInfo == null || uri.userInfo == null)) {
                        ret = this.userInfo.compareTo(uri.userInfo);
                        if (ret != 0) {
                            return ret;
                        }
                    }
                    ret = this.host.compareToIgnoreCase(uri.host);
                    if (ret != 0) {
                        return ret;
                    }
                    if (this.port != uri.port) {
                        return this.port - uri.port;
                    }
                }
            }
            ret = this.path.compareTo(uri.path);
            if (ret != 0) {
                return ret;
            }
            if (this.query != null && uri.query == null) {
                return 1;
            }
            if (this.query == null && uri.query != null) {
                return -1;
            }
            if (!(this.query == null || uri.query == null)) {
                ret = this.query.compareTo(uri.query);
                if (ret != 0) {
                    return ret;
                }
            }
        }
        if (this.fragment != null && uri.fragment == null) {
            return 1;
        }
        if (this.fragment == null && uri.fragment != null) {
            return -1;
        }
        if (!(this.fragment == null || uri.fragment == null)) {
            ret = this.fragment.compareTo(uri.fragment);
            if (ret != 0) {
                return ret;
            }
        }
        return 0;
    }

    public static URI create(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private URI duplicate() {
        URI clone = new URI();
        clone.absolute = this.absolute;
        clone.authority = this.authority;
        clone.fragment = this.fragment;
        clone.host = this.host;
        clone.opaque = this.opaque;
        clone.path = this.path;
        clone.port = this.port;
        clone.query = this.query;
        clone.scheme = this.scheme;
        clone.schemeSpecificPart = this.schemeSpecificPart;
        clone.userInfo = this.userInfo;
        clone.serverAuthority = this.serverAuthority;
        return clone;
    }

    private String convertHexToLowerCase(String s) {
        StringBuilder result = new StringBuilder(XmlPullParser.NO_NAMESPACE);
        if (s.indexOf(37) == -1) {
            return s;
        }
        int prevIndex = 0;
        while (true) {
            int index = s.indexOf(37, prevIndex);
            if (index == -1) {
                return result.toString();
            }
            result.append(s.substring(prevIndex, index + 1));
            result.append(s.substring(index + 1, index + 3).toLowerCase(Locale.US));
            prevIndex = index + 3;
        }
    }

    private boolean escapedEquals(String first, String second) {
        if (first.length() != second.length()) {
            return false;
        }
        int prevIndex = 0;
        while (true) {
            int index = first.indexOf(37, prevIndex);
            if (index != second.indexOf(37, prevIndex)) {
                return false;
            }
            if (index == -1) {
                return first.regionMatches(prevIndex, second, prevIndex, second.length() - prevIndex);
            }
            if (!first.regionMatches(prevIndex, second, prevIndex, index - prevIndex)) {
                return false;
            }
            if (!first.regionMatches(true, index + 1, second, index + 1, 2)) {
                return false;
            }
            prevIndex = index + 3;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof URI)) {
            return false;
        }
        URI uri = (URI) o;
        if (uri.fragment == null && this.fragment != null) {
            return false;
        }
        if (uri.fragment != null && this.fragment == null) {
            return false;
        }
        if (uri.fragment != null && this.fragment != null && !escapedEquals(uri.fragment, this.fragment)) {
            return false;
        }
        if (uri.scheme == null && this.scheme != null) {
            return false;
        }
        if (uri.scheme != null && this.scheme == null) {
            return false;
        }
        if (uri.scheme != null && this.scheme != null && !uri.scheme.equalsIgnoreCase(this.scheme)) {
            return false;
        }
        if (uri.opaque && this.opaque) {
            return escapedEquals(uri.schemeSpecificPart, this.schemeSpecificPart);
        }
        if (uri.opaque || this.opaque || !escapedEquals(this.path, uri.path)) {
            return false;
        }
        if (uri.query != null && this.query == null) {
            return false;
        }
        if (uri.query == null && this.query != null) {
            return false;
        }
        if (uri.query != null && this.query != null && !escapedEquals(uri.query, this.query)) {
            return false;
        }
        if (uri.authority != null && this.authority == null) {
            return false;
        }
        if (uri.authority == null && this.authority != null) {
            return false;
        }
        if (uri.authority == null || this.authority == null) {
            return true;
        }
        if (uri.host != null && this.host == null) {
            return false;
        }
        if (uri.host == null && this.host != null) {
            return false;
        }
        if (uri.host == null && this.host == null) {
            return escapedEquals(uri.authority, this.authority);
        }
        if (!this.host.equalsIgnoreCase(uri.host) || this.port != uri.port) {
            return false;
        }
        if (uri.userInfo != null && this.userInfo == null) {
            return false;
        }
        if (uri.userInfo != null || this.userInfo == null) {
            return (uri.userInfo == null || this.userInfo == null) ? true : escapedEquals(this.userInfo, uri.userInfo);
        } else {
            return false;
        }
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getSchemeSpecificPart() {
        return decode(this.schemeSpecificPart);
    }

    public String getRawSchemeSpecificPart() {
        return this.schemeSpecificPart;
    }

    public String getAuthority() {
        return decode(this.authority);
    }

    public String getRawAuthority() {
        return this.authority;
    }

    public String getUserInfo() {
        return decode(this.userInfo);
    }

    public String getRawUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public int getEffectivePort() {
        return getEffectivePort(this.scheme, this.port);
    }

    public static int getEffectivePort(String scheme, int specifiedPort) {
        if (specifiedPort != -1) {
            return specifiedPort;
        }
        if ("http".equalsIgnoreCase(scheme)) {
            return 80;
        }
        return "https".equalsIgnoreCase(scheme) ? 443 : -1;
    }

    public String getPath() {
        return decode(this.path);
    }

    public String getRawPath() {
        return this.path;
    }

    public String getQuery() {
        return decode(this.query);
    }

    public String getRawQuery() {
        return this.query;
    }

    public String getFragment() {
        return decode(this.fragment);
    }

    public String getRawFragment() {
        return this.fragment;
    }

    public int hashCode() {
        if (this.hash == -1) {
            this.hash = getHashString().hashCode();
        }
        return this.hash;
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    private String normalize(String path, boolean discardRelativePrefix) {
        path = UrlUtils.canonicalizePath(path, discardRelativePrefix);
        int colon = path.indexOf(58);
        if (colon == -1) {
            return path;
        }
        int slash = path.indexOf(47);
        if (slash == -1 || colon < slash) {
            return "./" + path;
        }
        return path;
    }

    public URI normalize() {
        if (this.opaque) {
            return this;
        }
        String normalizedPath = normalize(this.path, false);
        if (this.path.equals(normalizedPath)) {
            return this;
        }
        URI result = duplicate();
        result.path = normalizedPath;
        result.setSchemeSpecificPart();
        return result;
    }

    public URI parseServerAuthority() throws URISyntaxException {
        if (!this.serverAuthority) {
            parseAuthority(true);
        }
        return this;
    }

    public URI relativize(URI relative) {
        if (relative.opaque || this.opaque) {
            return relative;
        }
        if (this.scheme == null) {
            if (relative.scheme != null) {
                return relative;
            }
        } else if (!this.scheme.equals(relative.scheme)) {
            return relative;
        }
        if (this.authority == null) {
            if (relative.authority != null) {
                return relative;
            }
        } else if (!this.authority.equals(relative.authority)) {
            return relative;
        }
        String thisPath = normalize(this.path, false);
        String relativePath = normalize(relative.path, false);
        if (!thisPath.equals(relativePath)) {
            thisPath = thisPath.substring(0, thisPath.lastIndexOf(47) + 1);
            if (!relativePath.startsWith(thisPath)) {
                return relative;
            }
        }
        URI result = new URI();
        result.fragment = relative.fragment;
        result.query = relative.query;
        result.path = relativePath.substring(thisPath.length());
        result.setSchemeSpecificPart();
        return result;
    }

    public URI resolve(URI relative) {
        if (relative.absolute || this.opaque) {
            return relative;
        }
        URI result;
        if (relative.authority != null) {
            result = relative.duplicate();
            result.scheme = this.scheme;
            result.absolute = this.absolute;
            return result;
        } else if (relative.path.isEmpty() && relative.scheme == null && relative.query == null) {
            result = duplicate();
            result.fragment = relative.fragment;
            return result;
        } else {
            String resolvedPath;
            result = duplicate();
            result.fragment = relative.fragment;
            result.query = relative.query;
            if (relative.path.startsWith("/")) {
                resolvedPath = relative.path;
            } else if (relative.path.isEmpty()) {
                resolvedPath = this.path;
            } else {
                resolvedPath = this.path.substring(0, this.path.lastIndexOf(47) + 1) + relative.path;
            }
            result.path = UrlUtils.authoritySafePath(result.authority, normalize(resolvedPath, true));
            result.setSchemeSpecificPart();
            return result;
        }
    }

    private void setSchemeSpecificPart() {
        StringBuilder ssp = new StringBuilder();
        if (this.authority != null) {
            ssp.append("//" + this.authority);
        }
        if (this.path != null) {
            ssp.append(this.path);
        }
        if (this.query != null) {
            ssp.append("?" + this.query);
        }
        this.schemeSpecificPart = ssp.toString();
        this.string = null;
    }

    public URI resolve(String relative) {
        return resolve(create(relative));
    }

    private String decode(String s) {
        return s != null ? UriCodec.decode(s) : null;
    }

    public String toASCIIString() {
        StringBuilder result = new StringBuilder();
        ASCII_ONLY.appendEncoded(result, toString());
        return result.toString();
    }

    public String toString() {
        if (this.string != null) {
            return this.string;
        }
        StringBuilder result = new StringBuilder();
        if (this.scheme != null) {
            result.append(this.scheme);
            result.append(':');
        }
        if (this.opaque) {
            result.append(this.schemeSpecificPart);
        } else {
            if (this.authority != null) {
                result.append("//");
                result.append(this.authority);
            }
            if (this.path != null) {
                result.append(this.path);
            }
            if (this.query != null) {
                result.append('?');
                result.append(this.query);
            }
        }
        if (this.fragment != null) {
            result.append('#');
            result.append(this.fragment);
        }
        this.string = result.toString();
        return this.string;
    }

    private String getHashString() {
        StringBuilder result = new StringBuilder();
        if (this.scheme != null) {
            result.append(this.scheme.toLowerCase(Locale.US));
            result.append(':');
        }
        if (this.opaque) {
            result.append(this.schemeSpecificPart);
        } else {
            if (this.authority != null) {
                result.append("//");
                if (this.host == null) {
                    result.append(this.authority);
                } else {
                    if (this.userInfo != null) {
                        result.append(this.userInfo + "@");
                    }
                    result.append(this.host.toLowerCase(Locale.US));
                    if (this.port != -1) {
                        result.append(":" + this.port);
                    }
                }
            }
            if (this.path != null) {
                result.append(this.path);
            }
            if (this.query != null) {
                result.append('?');
                result.append(this.query);
            }
        }
        if (this.fragment != null) {
            result.append('#');
            result.append(this.fragment);
        }
        return convertHexToLowerCase(result.toString());
    }

    public URL toURL() throws MalformedURLException {
        if (this.absolute) {
            return new URL(toString());
        }
        throw new IllegalArgumentException("URI is not absolute: " + toString());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            parseURI(this.string, false);
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        toString();
        out.defaultWriteObject();
    }
}
