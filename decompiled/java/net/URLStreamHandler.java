package java.net;

import java.io.IOException;
import libcore.net.url.UrlUtils;
import libcore.util.Objects;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.xmlpull.v1.XmlPullParser;

public abstract class URLStreamHandler {
    protected abstract URLConnection openConnection(URL url) throws IOException;

    protected URLConnection openConnection(URL u, Proxy proxy) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void parseURL(URL url, String spec, int start, int end) {
        if (this != url.streamHandler) {
            throw new SecurityException("Only a URL's stream handler is permitted to mutate it");
        } else if (end < start) {
            throw new StringIndexOutOfBoundsException(spec, start, end - start);
        } else {
            int fileStart;
            String authority;
            String userInfo;
            String host;
            String path;
            String query;
            String ref;
            int port = -1;
            if (spec.regionMatches(start, "//", 0, 2)) {
                int hostStart;
                int authorityStart = start + 2;
                fileStart = UrlUtils.findFirstOf(spec, "/?#", authorityStart, end);
                authority = spec.substring(authorityStart, fileStart);
                int userInfoEnd = UrlUtils.findFirstOf(spec, "@", authorityStart, fileStart);
                if (userInfoEnd != fileStart) {
                    userInfo = spec.substring(authorityStart, userInfoEnd);
                    hostStart = userInfoEnd + 1;
                } else {
                    userInfo = null;
                    hostStart = authorityStart;
                }
                int colonSearchFrom = hostStart;
                int ipv6End = UrlUtils.findFirstOf(spec, "]", hostStart, fileStart);
                if (ipv6End != fileStart) {
                    if (UrlUtils.findFirstOf(spec, ":", hostStart, ipv6End) == ipv6End) {
                        throw new IllegalArgumentException("Expected an IPv6 address: " + spec.substring(hostStart, ipv6End + 1));
                    }
                    colonSearchFrom = ipv6End;
                }
                int hostEnd = UrlUtils.findFirstOf(spec, ":", colonSearchFrom, fileStart);
                host = spec.substring(hostStart, hostEnd);
                int portStart = hostEnd + 1;
                if (portStart < fileStart) {
                    char firstPortChar = spec.charAt(portStart);
                    if (firstPortChar < '0' || firstPortChar > '9') {
                        throw new IllegalArgumentException("invalid port: " + -1);
                    }
                    port = Integer.parseInt(spec.substring(portStart, fileStart));
                }
                path = null;
                query = null;
                ref = null;
            } else {
                fileStart = start;
                authority = url.getAuthority();
                userInfo = url.getUserInfo();
                host = url.getHost();
                if (host == null) {
                    host = XmlPullParser.NO_NAMESPACE;
                }
                port = url.getPort();
                path = url.getPath();
                query = url.getQuery();
                ref = url.getRef();
            }
            int pos = fileStart;
            while (pos < end) {
                int nextPos;
                switch (spec.charAt(pos)) {
                    case ASN1Constants.TAG_C_BITSTRING /*35*/:
                        nextPos = end;
                        ref = spec.substring(pos + 1, nextPos);
                        break;
                    case '?':
                        nextPos = UrlUtils.findFirstOf(spec, "#", pos, end);
                        query = spec.substring(pos + 1, nextPos);
                        ref = null;
                        break;
                    default:
                        nextPos = UrlUtils.findFirstOf(spec, "?#", pos, end);
                        path = relativePath(path, spec.substring(pos, nextPos));
                        query = null;
                        ref = null;
                        break;
                }
                pos = nextPos;
            }
            if (path == null) {
                path = XmlPullParser.NO_NAMESPACE;
            }
            URL url2 = url;
            setURL(url2, url.getProtocol(), host, port, authority, userInfo, UrlUtils.authoritySafePath(authority, path), query, ref);
        }
    }

    private static String relativePath(String base, String path) {
        if (path.startsWith("/")) {
            return UrlUtils.canonicalizePath(path, true);
        }
        if (base != null) {
            return UrlUtils.canonicalizePath(base.substring(0, base.lastIndexOf(47) + 1) + path, true);
        }
        return path;
    }

    @Deprecated
    protected void setURL(URL u, String protocol, String host, int port, String file, String ref) {
        if (this != u.streamHandler) {
            throw new SecurityException();
        }
        u.set(protocol, host, port, file, ref);
    }

    protected void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        if (this != u.streamHandler) {
            throw new SecurityException();
        }
        u.set(protocol, host, port, authority, userInfo, path, query, ref);
    }

    protected String toExternalForm(URL url) {
        return toExternalForm(url, false);
    }

    String toExternalForm(URL url, boolean escapeIllegalCharacters) {
        StringBuilder result = new StringBuilder();
        result.append(url.getProtocol());
        result.append(':');
        String authority = url.getAuthority();
        if (authority != null) {
            result.append("//");
            if (escapeIllegalCharacters) {
                URI.AUTHORITY_ENCODER.appendPartiallyEncoded(result, authority);
            } else {
                result.append(authority);
            }
        }
        String fileAndQuery = url.getFile();
        if (fileAndQuery != null) {
            if (escapeIllegalCharacters) {
                URI.FILE_AND_QUERY_ENCODER.appendPartiallyEncoded(result, fileAndQuery);
            } else {
                result.append(fileAndQuery);
            }
        }
        String ref = url.getRef();
        if (ref != null) {
            result.append('#');
            if (escapeIllegalCharacters) {
                URI.ALL_LEGAL_ENCODER.appendPartiallyEncoded(result, ref);
            } else {
                result.append(ref);
            }
        }
        return result.toString();
    }

    protected boolean equals(URL a, URL b) {
        return sameFile(a, b) && Objects.equal(a.getRef(), b.getRef()) && Objects.equal(a.getQuery(), b.getQuery());
    }

    protected int getDefaultPort() {
        return -1;
    }

    protected InetAddress getHostAddress(URL url) {
        InetAddress inetAddress = null;
        try {
            String host = url.getHost();
            if (!(host == null || host.length() == 0)) {
                inetAddress = InetAddress.getByName(host);
            }
        } catch (UnknownHostException e) {
        }
        return inetAddress;
    }

    protected int hashCode(URL url) {
        return toExternalForm(url).hashCode();
    }

    protected boolean hostsEqual(URL a, URL b) {
        String aHost = a.getHost();
        String bHost = b.getHost();
        return aHost == bHost || (aHost != null && aHost.equalsIgnoreCase(bHost));
    }

    protected boolean sameFile(URL a, URL b) {
        return Objects.equal(a.getProtocol(), b.getProtocol()) && hostsEqual(a, b) && a.getEffectivePort() == b.getEffectivePort() && Objects.equal(a.getFile(), b.getFile());
    }
}
