package com.android.okhttp.internal.http;

import com.android.okhttp.Headers;
import com.android.okhttp.OkAuthenticator;
import com.android.okhttp.OkAuthenticator.Challenge;
import com.android.okhttp.OkAuthenticator.Credential;
import com.android.okhttp.Request;
import com.android.okhttp.Response;
import java.io.IOException;
import java.net.Authenticator;
import java.net.Authenticator.RequestorType;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class HttpAuthenticator {
    public static final OkAuthenticator SYSTEM_DEFAULT;

    static {
        SYSTEM_DEFAULT = new OkAuthenticator() {
            public Credential authenticate(Proxy proxy, URL url, List<Challenge> challenges) throws IOException {
                int size = challenges.size();
                for (int i = 0; i < size; i++) {
                    Challenge challenge = (Challenge) challenges.get(i);
                    if ("Basic".equalsIgnoreCase(challenge.getScheme())) {
                        PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(url.getHost(), getConnectToInetAddress(proxy, url), url.getPort(), url.getProtocol(), challenge.getRealm(), challenge.getScheme(), url, RequestorType.SERVER);
                        if (auth != null) {
                            return Credential.basic(auth.getUserName(), new String(auth.getPassword()));
                        }
                    }
                }
                return null;
            }

            public Credential authenticateProxy(Proxy proxy, URL url, List<Challenge> challenges) throws IOException {
                int size = challenges.size();
                for (int i = 0; i < size; i++) {
                    Challenge challenge = (Challenge) challenges.get(i);
                    if ("Basic".equalsIgnoreCase(challenge.getScheme())) {
                        InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
                        PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(proxyAddress.getHostName(), getConnectToInetAddress(proxy, url), proxyAddress.getPort(), url.getProtocol(), challenge.getRealm(), challenge.getScheme(), url, RequestorType.PROXY);
                        if (auth != null) {
                            return Credential.basic(auth.getUserName(), new String(auth.getPassword()));
                        }
                    }
                }
                return null;
            }

            private InetAddress getConnectToInetAddress(Proxy proxy, URL url) throws IOException {
                return (proxy == null || proxy.type() == Type.DIRECT) ? InetAddress.getByName(url.getHost()) : ((InetSocketAddress) proxy.address()).getAddress();
            }
        };
    }

    private HttpAuthenticator() {
    }

    public static Request processAuthHeader(OkAuthenticator authenticator, Response response, Proxy proxy) throws IOException {
        String responseField;
        String requestField;
        if (response.code() == 401) {
            responseField = "WWW-Authenticate";
            requestField = "Authorization";
        } else if (response.code() == 407) {
            responseField = "Proxy-Authenticate";
            requestField = "Proxy-Authorization";
        } else {
            throw new IllegalArgumentException();
        }
        List<Challenge> challenges = parseChallenges(response.headers(), responseField);
        if (challenges.isEmpty()) {
            return null;
        }
        Request request = response.request();
        Credential credential = response.code() == 407 ? authenticator.authenticateProxy(proxy, request.url(), challenges) : authenticator.authenticate(proxy, request.url(), challenges);
        if (credential != null) {
            return request.newBuilder().header(requestField, credential.getHeaderValue()).build();
        }
        return null;
    }

    private static List<Challenge> parseChallenges(Headers responseHeaders, String challengeHeader) {
        List<Challenge> result = new ArrayList();
        for (int h = 0; h < responseHeaders.size(); h++) {
            if (challengeHeader.equalsIgnoreCase(responseHeaders.name(h))) {
                String value = responseHeaders.value(h);
                int pos = 0;
                while (pos < value.length()) {
                    int tokenStart = pos;
                    pos = HeaderParser.skipUntil(value, pos, " ");
                    String scheme = value.substring(tokenStart, pos).trim();
                    pos = HeaderParser.skipWhitespace(value, pos);
                    if (!value.regionMatches(true, pos, "realm=\"", 0, "realm=\"".length())) {
                        break;
                    }
                    pos += "realm=\"".length();
                    int realmStart = pos;
                    pos = HeaderParser.skipUntil(value, pos, "\"");
                    String realm = value.substring(realmStart, pos);
                    pos = HeaderParser.skipWhitespace(value, HeaderParser.skipUntil(value, pos + 1, ",") + 1);
                    result.add(new Challenge(scheme, realm));
                }
            }
        }
        return result;
    }
}
