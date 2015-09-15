package android.net;

import android.content.ContentResolver;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.provider.SearchIndexablesContract.RawData;
import android.telecom.PhoneAccount;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import libcore.net.UriCodec;

public abstract class Uri implements Parcelable, Comparable<Uri> {
    public static final Creator<Uri> CREATOR;
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final Uri EMPTY;
    private static final char[] HEX_DIGITS;
    private static final String LOG;
    private static final String NOT_CACHED;
    private static final int NOT_CALCULATED = -2;
    private static final int NOT_FOUND = -1;
    private static final String NOT_HIERARCHICAL = "This isn't a hierarchical URI.";
    private static final int NULL_TYPE_ID = 0;

    private static abstract class AbstractHierarchicalUri extends Uri {
        private volatile String host;
        private volatile int port;
        private Part userInfo;

        private AbstractHierarchicalUri() {
            super();
            this.host = Uri.NOT_CACHED;
            this.port = Uri.NOT_CALCULATED;
        }

        public /* bridge */ /* synthetic */ int compareTo(Object x0) {
            return super.compareTo((Uri) x0);
        }

        public String getLastPathSegment() {
            List<String> segments = getPathSegments();
            int size = segments.size();
            if (size == 0) {
                return null;
            }
            return (String) segments.get(size + Uri.NOT_FOUND);
        }

        private Part getUserInfoPart() {
            if (this.userInfo != null) {
                return this.userInfo;
            }
            Part fromEncoded = Part.fromEncoded(parseUserInfo());
            this.userInfo = fromEncoded;
            return fromEncoded;
        }

        public final String getEncodedUserInfo() {
            return getUserInfoPart().getEncoded();
        }

        private String parseUserInfo() {
            String authority = getEncodedAuthority();
            if (authority == null) {
                return null;
            }
            int end = authority.indexOf(64);
            if (end != Uri.NOT_FOUND) {
                return authority.substring(0, end);
            }
            return null;
        }

        public String getUserInfo() {
            return getUserInfoPart().getDecoded();
        }

        public String getHost() {
            if (this.host != Uri.NOT_CACHED) {
                return this.host;
            }
            String parseHost = parseHost();
            this.host = parseHost;
            return parseHost;
        }

        private String parseHost() {
            String authority = getEncodedAuthority();
            if (authority == null) {
                return null;
            }
            int userInfoSeparator = authority.indexOf(64);
            int portSeparator = authority.indexOf(58, userInfoSeparator);
            return Uri.decode(portSeparator == Uri.NOT_FOUND ? authority.substring(userInfoSeparator + 1) : authority.substring(userInfoSeparator + 1, portSeparator));
        }

        public int getPort() {
            if (this.port != Uri.NOT_CALCULATED) {
                return this.port;
            }
            int parsePort = parsePort();
            this.port = parsePort;
            return parsePort;
        }

        private int parsePort() {
            int i = Uri.NOT_FOUND;
            String authority = getEncodedAuthority();
            if (authority != null) {
                int portSeparator = authority.indexOf(58, authority.indexOf(64));
                if (portSeparator != i) {
                    try {
                        i = Integer.parseInt(Uri.decode(authority.substring(portSeparator + 1)));
                    } catch (NumberFormatException e) {
                        Log.w(Uri.LOG, "Error parsing port string.", e);
                    }
                }
            }
            return i;
        }
    }

    static abstract class AbstractPart {
        volatile String decoded;
        volatile String encoded;

        static class Representation {
            static final int BOTH = 0;
            static final int DECODED = 2;
            static final int ENCODED = 1;

            Representation() {
            }
        }

        abstract String getEncoded();

        AbstractPart(String encoded, String decoded) {
            this.encoded = encoded;
            this.decoded = decoded;
        }

        final String getDecoded() {
            if (this.decoded != Uri.NOT_CACHED) {
                return this.decoded;
            }
            String decode = Uri.decode(this.encoded);
            this.decoded = decode;
            return decode;
        }

        final void writeTo(Parcel parcel) {
            boolean hasEncoded;
            boolean hasDecoded;
            if (this.encoded != Uri.NOT_CACHED) {
                hasEncoded = true;
            } else {
                hasEncoded = false;
            }
            if (this.decoded != Uri.NOT_CACHED) {
                hasDecoded = true;
            } else {
                hasDecoded = false;
            }
            if (hasEncoded && hasDecoded) {
                parcel.writeInt(0);
                parcel.writeString(this.encoded);
                parcel.writeString(this.decoded);
            } else if (hasEncoded) {
                parcel.writeInt(1);
                parcel.writeString(this.encoded);
            } else if (hasDecoded) {
                parcel.writeInt(2);
                parcel.writeString(this.decoded);
            } else {
                throw new IllegalArgumentException("Neither encoded nor decoded");
            }
        }
    }

    public static final class Builder {
        private Part authority;
        private Part fragment;
        private Part opaquePart;
        private PathPart path;
        private Part query;
        private String scheme;

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        Builder opaquePart(Part opaquePart) {
            this.opaquePart = opaquePart;
            return this;
        }

        public Builder opaquePart(String opaquePart) {
            return opaquePart(Part.fromDecoded(opaquePart));
        }

        public Builder encodedOpaquePart(String opaquePart) {
            return opaquePart(Part.fromEncoded(opaquePart));
        }

        Builder authority(Part authority) {
            this.opaquePart = null;
            this.authority = authority;
            return this;
        }

        public Builder authority(String authority) {
            return authority(Part.fromDecoded(authority));
        }

        public Builder encodedAuthority(String authority) {
            return authority(Part.fromEncoded(authority));
        }

        Builder path(PathPart path) {
            this.opaquePart = null;
            this.path = path;
            return this;
        }

        public Builder path(String path) {
            return path(PathPart.fromDecoded(path));
        }

        public Builder encodedPath(String path) {
            return path(PathPart.fromEncoded(path));
        }

        public Builder appendPath(String newSegment) {
            return path(PathPart.appendDecodedSegment(this.path, newSegment));
        }

        public Builder appendEncodedPath(String newSegment) {
            return path(PathPart.appendEncodedSegment(this.path, newSegment));
        }

        Builder query(Part query) {
            this.opaquePart = null;
            this.query = query;
            return this;
        }

        public Builder query(String query) {
            return query(Part.fromDecoded(query));
        }

        public Builder encodedQuery(String query) {
            return query(Part.fromEncoded(query));
        }

        Builder fragment(Part fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder fragment(String fragment) {
            return fragment(Part.fromDecoded(fragment));
        }

        public Builder encodedFragment(String fragment) {
            return fragment(Part.fromEncoded(fragment));
        }

        public Builder appendQueryParameter(String key, String value) {
            this.opaquePart = null;
            String encodedParameter = Uri.encode(key, null) + "=" + Uri.encode(value, null);
            if (this.query == null) {
                this.query = Part.fromEncoded(encodedParameter);
            } else {
                String oldQuery = this.query.getEncoded();
                if (oldQuery == null || oldQuery.length() == 0) {
                    this.query = Part.fromEncoded(encodedParameter);
                } else {
                    this.query = Part.fromEncoded(oldQuery + "&" + encodedParameter);
                }
            }
            return this;
        }

        public Builder clearQuery() {
            return query((Part) null);
        }

        public Uri build() {
            if (this.opaquePart == null) {
                PathPart path = this.path;
                if (path == null || path == PathPart.NULL) {
                    path = PathPart.EMPTY;
                } else if (hasSchemeOrAuthority()) {
                    path = PathPart.makeAbsolute(path);
                }
                return new HierarchicalUri(this.authority, path, this.query, this.fragment, null);
            } else if (this.scheme != null) {
                return new OpaqueUri(this.opaquePart, this.fragment, null);
            } else {
                throw new UnsupportedOperationException("An opaque URI must have a scheme.");
            }
        }

        private boolean hasSchemeOrAuthority() {
            return (this.scheme == null && (this.authority == null || this.authority == Part.NULL)) ? false : true;
        }

        public String toString() {
            return build().toString();
        }
    }

    private static class HierarchicalUri extends AbstractHierarchicalUri {
        static final int TYPE_ID = 3;
        private final Part authority;
        private final Part fragment;
        private final PathPart path;
        private final Part query;
        private final String scheme;
        private Part ssp;
        private volatile String uriString;

        private HierarchicalUri(String scheme, Part authority, PathPart path, Part query, Part fragment) {
            super();
            this.uriString = Uri.NOT_CACHED;
            this.scheme = scheme;
            this.authority = Part.nonNull(authority);
            if (path == null) {
                path = PathPart.NULL;
            }
            this.path = path;
            this.query = Part.nonNull(query);
            this.fragment = Part.nonNull(fragment);
        }

        static Uri readFrom(Parcel parcel) {
            return new HierarchicalUri(parcel.readString(), Part.readFrom(parcel), PathPart.readFrom(parcel), Part.readFrom(parcel), Part.readFrom(parcel));
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(TYPE_ID);
            parcel.writeString(this.scheme);
            this.authority.writeTo(parcel);
            this.path.writeTo(parcel);
            this.query.writeTo(parcel);
            this.fragment.writeTo(parcel);
        }

        public boolean isHierarchical() {
            return true;
        }

        public boolean isRelative() {
            return this.scheme == null;
        }

        public String getScheme() {
            return this.scheme;
        }

        private Part getSsp() {
            if (this.ssp != null) {
                return this.ssp;
            }
            Part fromEncoded = Part.fromEncoded(makeSchemeSpecificPart());
            this.ssp = fromEncoded;
            return fromEncoded;
        }

        public String getEncodedSchemeSpecificPart() {
            return getSsp().getEncoded();
        }

        public String getSchemeSpecificPart() {
            return getSsp().getDecoded();
        }

        private String makeSchemeSpecificPart() {
            StringBuilder builder = new StringBuilder();
            appendSspTo(builder);
            return builder.toString();
        }

        private void appendSspTo(StringBuilder builder) {
            String encodedAuthority = this.authority.getEncoded();
            if (encodedAuthority != null) {
                builder.append("//").append(encodedAuthority);
            }
            String encodedPath = this.path.getEncoded();
            if (encodedPath != null) {
                builder.append(encodedPath);
            }
            if (!this.query.isEmpty()) {
                builder.append('?').append(this.query.getEncoded());
            }
        }

        public String getAuthority() {
            return this.authority.getDecoded();
        }

        public String getEncodedAuthority() {
            return this.authority.getEncoded();
        }

        public String getEncodedPath() {
            return this.path.getEncoded();
        }

        public String getPath() {
            return this.path.getDecoded();
        }

        public String getQuery() {
            return this.query.getDecoded();
        }

        public String getEncodedQuery() {
            return this.query.getEncoded();
        }

        public String getFragment() {
            return this.fragment.getDecoded();
        }

        public String getEncodedFragment() {
            return this.fragment.getEncoded();
        }

        public List<String> getPathSegments() {
            return this.path.getPathSegments();
        }

        public String toString() {
            if (this.uriString != Uri.NOT_CACHED) {
                return this.uriString;
            }
            String makeUriString = makeUriString();
            this.uriString = makeUriString;
            return makeUriString;
        }

        private String makeUriString() {
            StringBuilder builder = new StringBuilder();
            if (this.scheme != null) {
                builder.append(this.scheme).append(':');
            }
            appendSspTo(builder);
            if (!this.fragment.isEmpty()) {
                builder.append('#').append(this.fragment.getEncoded());
            }
            return builder.toString();
        }

        public Builder buildUpon() {
            return new Builder().scheme(this.scheme).authority(this.authority).path(this.path).query(this.query).fragment(this.fragment);
        }
    }

    private static class OpaqueUri extends Uri {
        static final int TYPE_ID = 2;
        private volatile String cachedString;
        private final Part fragment;
        private final String scheme;
        private final Part ssp;

        public /* bridge */ /* synthetic */ int compareTo(Object x0) {
            return super.compareTo((Uri) x0);
        }

        private OpaqueUri(String scheme, Part ssp, Part fragment) {
            super();
            this.cachedString = Uri.NOT_CACHED;
            this.scheme = scheme;
            this.ssp = ssp;
            if (fragment == null) {
                fragment = Part.NULL;
            }
            this.fragment = fragment;
        }

        static Uri readFrom(Parcel parcel) {
            return new OpaqueUri(parcel.readString(), Part.readFrom(parcel), Part.readFrom(parcel));
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(TYPE_ID);
            parcel.writeString(this.scheme);
            this.ssp.writeTo(parcel);
            this.fragment.writeTo(parcel);
        }

        public boolean isHierarchical() {
            return false;
        }

        public boolean isRelative() {
            return this.scheme == null;
        }

        public String getScheme() {
            return this.scheme;
        }

        public String getEncodedSchemeSpecificPart() {
            return this.ssp.getEncoded();
        }

        public String getSchemeSpecificPart() {
            return this.ssp.getDecoded();
        }

        public String getAuthority() {
            return null;
        }

        public String getEncodedAuthority() {
            return null;
        }

        public String getPath() {
            return null;
        }

        public String getEncodedPath() {
            return null;
        }

        public String getQuery() {
            return null;
        }

        public String getEncodedQuery() {
            return null;
        }

        public String getFragment() {
            return this.fragment.getDecoded();
        }

        public String getEncodedFragment() {
            return this.fragment.getEncoded();
        }

        public List<String> getPathSegments() {
            return Collections.emptyList();
        }

        public String getLastPathSegment() {
            return null;
        }

        public String getUserInfo() {
            return null;
        }

        public String getEncodedUserInfo() {
            return null;
        }

        public String getHost() {
            return null;
        }

        public int getPort() {
            return Uri.NOT_FOUND;
        }

        public String toString() {
            if (this.cachedString != Uri.NOT_CACHED) {
                return this.cachedString;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.scheme).append(':');
            sb.append(getEncodedSchemeSpecificPart());
            if (!this.fragment.isEmpty()) {
                sb.append('#').append(this.fragment.getEncoded());
            }
            String stringBuilder = sb.toString();
            this.cachedString = stringBuilder;
            return stringBuilder;
        }

        public Builder buildUpon() {
            return new Builder().scheme(this.scheme).opaquePart(this.ssp).fragment(this.fragment);
        }
    }

    static class Part extends AbstractPart {
        static final Part EMPTY;
        static final Part NULL;

        private static class EmptyPart extends Part {
            public EmptyPart(String value) {
                super(value, null);
            }

            boolean isEmpty() {
                return true;
            }
        }

        static {
            NULL = new EmptyPart(null);
            EMPTY = new EmptyPart(ProxyInfo.LOCAL_EXCL_LIST);
        }

        private Part(String encoded, String decoded) {
            super(encoded, decoded);
        }

        boolean isEmpty() {
            return false;
        }

        String getEncoded() {
            if (this.encoded != Uri.NOT_CACHED) {
                return this.encoded;
            }
            String encode = Uri.encode(this.decoded);
            this.encoded = encode;
            return encode;
        }

        static Part readFrom(Parcel parcel) {
            int representation = parcel.readInt();
            switch (representation) {
                case Toast.LENGTH_SHORT /*0*/:
                    return from(parcel.readString(), parcel.readString());
                case Toast.LENGTH_LONG /*1*/:
                    return fromEncoded(parcel.readString());
                case Action.MERGE_IGNORE /*2*/:
                    return fromDecoded(parcel.readString());
                default:
                    throw new IllegalArgumentException("Unknown representation: " + representation);
            }
        }

        static Part nonNull(Part part) {
            return part == null ? NULL : part;
        }

        static Part fromEncoded(String encoded) {
            return from(encoded, Uri.NOT_CACHED);
        }

        static Part fromDecoded(String decoded) {
            return from(Uri.NOT_CACHED, decoded);
        }

        static Part from(String encoded, String decoded) {
            if (encoded == null) {
                return NULL;
            }
            if (encoded.length() == 0) {
                return EMPTY;
            }
            if (decoded == null) {
                return NULL;
            }
            if (decoded.length() == 0) {
                return EMPTY;
            }
            return new Part(encoded, decoded);
        }
    }

    static class PathPart extends AbstractPart {
        static final PathPart EMPTY;
        static final PathPart NULL;
        private PathSegments pathSegments;

        static {
            NULL = new PathPart(null, null);
            EMPTY = new PathPart(ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
        }

        private PathPart(String encoded, String decoded) {
            super(encoded, decoded);
        }

        String getEncoded() {
            if (this.encoded != Uri.NOT_CACHED) {
                return this.encoded;
            }
            String encode = Uri.encode(this.decoded, "/");
            this.encoded = encode;
            return encode;
        }

        PathSegments getPathSegments() {
            if (this.pathSegments != null) {
                return this.pathSegments;
            }
            String path = getEncoded();
            if (path == null) {
                PathSegments pathSegments = PathSegments.EMPTY;
                this.pathSegments = pathSegments;
                return pathSegments;
            }
            PathSegmentsBuilder segmentBuilder = new PathSegmentsBuilder();
            int previous = 0;
            while (true) {
                int current = path.indexOf(47, previous);
                if (current <= Uri.NOT_FOUND) {
                    break;
                }
                if (previous < current) {
                    segmentBuilder.add(Uri.decode(path.substring(previous, current)));
                }
                previous = current + 1;
            }
            if (previous < path.length()) {
                segmentBuilder.add(Uri.decode(path.substring(previous)));
            }
            pathSegments = segmentBuilder.build();
            this.pathSegments = pathSegments;
            return pathSegments;
        }

        static PathPart appendEncodedSegment(PathPart oldPart, String newSegment) {
            if (oldPart == null) {
                return fromEncoded("/" + newSegment);
            }
            String newPath;
            String oldPath = oldPart.getEncoded();
            if (oldPath == null) {
                oldPath = ProxyInfo.LOCAL_EXCL_LIST;
            }
            int oldPathLength = oldPath.length();
            if (oldPathLength == 0) {
                newPath = "/" + newSegment;
            } else if (oldPath.charAt(oldPathLength + Uri.NOT_FOUND) == '/') {
                newPath = oldPath + newSegment;
            } else {
                newPath = oldPath + "/" + newSegment;
            }
            return fromEncoded(newPath);
        }

        static PathPart appendDecodedSegment(PathPart oldPart, String decoded) {
            return appendEncodedSegment(oldPart, Uri.encode(decoded));
        }

        static PathPart readFrom(Parcel parcel) {
            int representation = parcel.readInt();
            switch (representation) {
                case Toast.LENGTH_SHORT /*0*/:
                    return from(parcel.readString(), parcel.readString());
                case Toast.LENGTH_LONG /*1*/:
                    return fromEncoded(parcel.readString());
                case Action.MERGE_IGNORE /*2*/:
                    return fromDecoded(parcel.readString());
                default:
                    throw new IllegalArgumentException("Bad representation: " + representation);
            }
        }

        static PathPart fromEncoded(String encoded) {
            return from(encoded, Uri.NOT_CACHED);
        }

        static PathPart fromDecoded(String decoded) {
            return from(Uri.NOT_CACHED, decoded);
        }

        static PathPart from(String encoded, String decoded) {
            if (encoded == null) {
                return NULL;
            }
            if (encoded.length() == 0) {
                return EMPTY;
            }
            return new PathPart(encoded, decoded);
        }

        static PathPart makeAbsolute(PathPart oldPart) {
            boolean encodedCached;
            boolean decodedCached = true;
            if (oldPart.encoded != Uri.NOT_CACHED) {
                encodedCached = true;
            } else {
                encodedCached = false;
            }
            String oldPath = encodedCached ? oldPart.encoded : oldPart.decoded;
            if (oldPath == null || oldPath.length() == 0 || oldPath.startsWith("/")) {
                return oldPart;
            }
            String newEncoded = encodedCached ? "/" + oldPart.encoded : Uri.NOT_CACHED;
            if (oldPart.decoded == Uri.NOT_CACHED) {
                decodedCached = false;
            }
            return new PathPart(newEncoded, decodedCached ? "/" + oldPart.decoded : Uri.NOT_CACHED);
        }
    }

    static class PathSegments extends AbstractList<String> implements RandomAccess {
        static final PathSegments EMPTY;
        final String[] segments;
        final int size;

        static {
            EMPTY = new PathSegments(null, 0);
        }

        PathSegments(String[] segments, int size) {
            this.segments = segments;
            this.size = size;
        }

        public String get(int index) {
            if (index < this.size) {
                return this.segments[index];
            }
            throw new IndexOutOfBoundsException();
        }

        public int size() {
            return this.size;
        }
    }

    static class PathSegmentsBuilder {
        String[] segments;
        int size;

        PathSegmentsBuilder() {
            this.size = 0;
        }

        void add(String segment) {
            if (this.segments == null) {
                this.segments = new String[4];
            } else if (this.size + 1 == this.segments.length) {
                String[] expanded = new String[(this.segments.length * 2)];
                System.arraycopy(this.segments, 0, expanded, 0, this.segments.length);
                this.segments = expanded;
            }
            String[] strArr = this.segments;
            int i = this.size;
            this.size = i + 1;
            strArr[i] = segment;
        }

        PathSegments build() {
            if (this.segments == null) {
                return PathSegments.EMPTY;
            }
            try {
                PathSegments pathSegments = new PathSegments(this.segments, this.size);
                return pathSegments;
            } finally {
                this.segments = null;
            }
        }
    }

    private static class StringUri extends AbstractHierarchicalUri {
        static final int TYPE_ID = 1;
        private Part authority;
        private volatile int cachedFsi;
        private volatile int cachedSsi;
        private Part fragment;
        private PathPart path;
        private Part query;
        private volatile String scheme;
        private Part ssp;
        private final String uriString;

        private StringUri(String uriString) {
            super();
            this.cachedSsi = Uri.NOT_CALCULATED;
            this.cachedFsi = Uri.NOT_CALCULATED;
            this.scheme = Uri.NOT_CACHED;
            if (uriString == null) {
                throw new NullPointerException("uriString");
            }
            this.uriString = uriString;
        }

        static Uri readFrom(Parcel parcel) {
            return new StringUri(parcel.readString());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(TYPE_ID);
            parcel.writeString(this.uriString);
        }

        private int findSchemeSeparator() {
            if (this.cachedSsi != Uri.NOT_CALCULATED) {
                return this.cachedSsi;
            }
            int indexOf = this.uriString.indexOf(58);
            this.cachedSsi = indexOf;
            return indexOf;
        }

        private int findFragmentSeparator() {
            if (this.cachedFsi != Uri.NOT_CALCULATED) {
                return this.cachedFsi;
            }
            int indexOf = this.uriString.indexOf(35, findSchemeSeparator());
            this.cachedFsi = indexOf;
            return indexOf;
        }

        public boolean isHierarchical() {
            int ssi = findSchemeSeparator();
            if (ssi == Uri.NOT_FOUND) {
                return true;
            }
            if (this.uriString.length() == ssi + TYPE_ID) {
                return false;
            }
            if (this.uriString.charAt(ssi + TYPE_ID) != '/') {
                return false;
            }
            return true;
        }

        public boolean isRelative() {
            return findSchemeSeparator() == Uri.NOT_FOUND;
        }

        public String getScheme() {
            if (this.scheme != Uri.NOT_CACHED) {
                return this.scheme;
            }
            String parseScheme = parseScheme();
            this.scheme = parseScheme;
            return parseScheme;
        }

        private String parseScheme() {
            int ssi = findSchemeSeparator();
            return ssi == Uri.NOT_FOUND ? null : this.uriString.substring(0, ssi);
        }

        private Part getSsp() {
            if (this.ssp != null) {
                return this.ssp;
            }
            Part fromEncoded = Part.fromEncoded(parseSsp());
            this.ssp = fromEncoded;
            return fromEncoded;
        }

        public String getEncodedSchemeSpecificPart() {
            return getSsp().getEncoded();
        }

        public String getSchemeSpecificPart() {
            return getSsp().getDecoded();
        }

        private String parseSsp() {
            int ssi = findSchemeSeparator();
            int fsi = findFragmentSeparator();
            return fsi == Uri.NOT_FOUND ? this.uriString.substring(ssi + TYPE_ID) : this.uriString.substring(ssi + TYPE_ID, fsi);
        }

        private Part getAuthorityPart() {
            if (this.authority != null) {
                return this.authority;
            }
            Part fromEncoded = Part.fromEncoded(parseAuthority(this.uriString, findSchemeSeparator()));
            this.authority = fromEncoded;
            return fromEncoded;
        }

        public String getEncodedAuthority() {
            return getAuthorityPart().getEncoded();
        }

        public String getAuthority() {
            return getAuthorityPart().getDecoded();
        }

        private PathPart getPathPart() {
            if (this.path != null) {
                return this.path;
            }
            PathPart fromEncoded = PathPart.fromEncoded(parsePath());
            this.path = fromEncoded;
            return fromEncoded;
        }

        public String getPath() {
            return getPathPart().getDecoded();
        }

        public String getEncodedPath() {
            return getPathPart().getEncoded();
        }

        public List<String> getPathSegments() {
            return getPathPart().getPathSegments();
        }

        private String parsePath() {
            String uriString = this.uriString;
            int ssi = findSchemeSeparator();
            if (ssi > Uri.NOT_FOUND) {
                if ((ssi + TYPE_ID == uriString.length()) || uriString.charAt(ssi + TYPE_ID) != '/') {
                    return null;
                }
            }
            return parsePath(uriString, ssi);
        }

        private Part getQueryPart() {
            if (this.query != null) {
                return this.query;
            }
            Part fromEncoded = Part.fromEncoded(parseQuery());
            this.query = fromEncoded;
            return fromEncoded;
        }

        public String getEncodedQuery() {
            return getQueryPart().getEncoded();
        }

        private String parseQuery() {
            int qsi = this.uriString.indexOf(63, findSchemeSeparator());
            if (qsi == Uri.NOT_FOUND) {
                return null;
            }
            int fsi = findFragmentSeparator();
            if (fsi == Uri.NOT_FOUND) {
                return this.uriString.substring(qsi + TYPE_ID);
            }
            if (fsi >= qsi) {
                return this.uriString.substring(qsi + TYPE_ID, fsi);
            }
            return null;
        }

        public String getQuery() {
            return getQueryPart().getDecoded();
        }

        private Part getFragmentPart() {
            if (this.fragment != null) {
                return this.fragment;
            }
            Part fromEncoded = Part.fromEncoded(parseFragment());
            this.fragment = fromEncoded;
            return fromEncoded;
        }

        public String getEncodedFragment() {
            return getFragmentPart().getEncoded();
        }

        private String parseFragment() {
            int fsi = findFragmentSeparator();
            return fsi == Uri.NOT_FOUND ? null : this.uriString.substring(fsi + TYPE_ID);
        }

        public String getFragment() {
            return getFragmentPart().getDecoded();
        }

        public String toString() {
            return this.uriString;
        }

        static String parseAuthority(String uriString, int ssi) {
            int length = uriString.length();
            if (length <= ssi + 2 || uriString.charAt(ssi + TYPE_ID) != '/' || uriString.charAt(ssi + 2) != '/') {
                return null;
            }
            int end = ssi + 3;
            while (end < length) {
                switch (uriString.charAt(end)) {
                    case MotionEvent.AXIS_GENERIC_4 /*35*/:
                    case MotionEvent.AXIS_GENERIC_16 /*47*/:
                    case KeyEvent.KEYCODE_SYM /*63*/:
                        break;
                    default:
                        end += TYPE_ID;
                }
                return uriString.substring(ssi + 3, end);
            }
            return uriString.substring(ssi + 3, end);
        }

        static String parsePath(String uriString, int ssi) {
            int pathStart;
            int length = uriString.length();
            if (length > ssi + 2 && uriString.charAt(ssi + TYPE_ID) == '/' && uriString.charAt(ssi + 2) == '/') {
                pathStart = ssi + 3;
                while (pathStart < length) {
                    switch (uriString.charAt(pathStart)) {
                        case MotionEvent.AXIS_GENERIC_4 /*35*/:
                        case KeyEvent.KEYCODE_SYM /*63*/:
                            return ProxyInfo.LOCAL_EXCL_LIST;
                        case MotionEvent.AXIS_GENERIC_16 /*47*/:
                            break;
                        default:
                            pathStart += TYPE_ID;
                    }
                }
            } else {
                pathStart = ssi + TYPE_ID;
            }
            int pathEnd = pathStart;
            while (pathEnd < length) {
                switch (uriString.charAt(pathEnd)) {
                    case MotionEvent.AXIS_GENERIC_4 /*35*/:
                    case KeyEvent.KEYCODE_SYM /*63*/:
                        break;
                    default:
                        pathEnd += TYPE_ID;
                }
                return uriString.substring(pathStart, pathEnd);
            }
            return uriString.substring(pathStart, pathEnd);
        }

        public Builder buildUpon() {
            if (isHierarchical()) {
                return new Builder().scheme(getScheme()).authority(getAuthorityPart()).path(getPathPart()).query(getQueryPart()).fragment(getFragmentPart());
            }
            return new Builder().scheme(getScheme()).opaquePart(getSsp()).fragment(getFragmentPart());
        }
    }

    public abstract Builder buildUpon();

    public abstract String getAuthority();

    public abstract String getEncodedAuthority();

    public abstract String getEncodedFragment();

    public abstract String getEncodedPath();

    public abstract String getEncodedQuery();

    public abstract String getEncodedSchemeSpecificPart();

    public abstract String getEncodedUserInfo();

    public abstract String getFragment();

    public abstract String getHost();

    public abstract String getLastPathSegment();

    public abstract String getPath();

    public abstract List<String> getPathSegments();

    public abstract int getPort();

    public abstract String getQuery();

    public abstract String getScheme();

    public abstract String getSchemeSpecificPart();

    public abstract String getUserInfo();

    public abstract boolean isHierarchical();

    public abstract boolean isRelative();

    public abstract String toString();

    static {
        LOG = Uri.class.getSimpleName();
        NOT_CACHED = new String("NOT CACHED");
        EMPTY = new HierarchicalUri(Part.NULL, PathPart.EMPTY, Part.NULL, Part.NULL, null);
        CREATOR = new Creator<Uri>() {
            public Uri createFromParcel(Parcel in) {
                int type = in.readInt();
                switch (type) {
                    case Toast.LENGTH_SHORT /*0*/:
                        return null;
                    case Toast.LENGTH_LONG /*1*/:
                        return StringUri.readFrom(in);
                    case Action.MERGE_IGNORE /*2*/:
                        return OpaqueUri.readFrom(in);
                    case SetDrawableParameters.TAG /*3*/:
                        return HierarchicalUri.readFrom(in);
                    default:
                        throw new IllegalArgumentException("Unknown URI type: " + type);
                }
            }

            public Uri[] newArray(int size) {
                return new Uri[size];
            }
        };
        HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    }

    private Uri() {
    }

    public boolean isOpaque() {
        return !isHierarchical();
    }

    public boolean isAbsolute() {
        return !isRelative();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Uri)) {
            return false;
        }
        return toString().equals(((Uri) o).toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public int compareTo(Uri other) {
        return toString().compareTo(other.toString());
    }

    public String toSafeString() {
        String scheme = getScheme();
        String ssp = getSchemeSpecificPart();
        StringBuilder builder;
        if (scheme == null || !(scheme.equalsIgnoreCase(PhoneAccount.SCHEME_TEL) || scheme.equalsIgnoreCase(PhoneAccount.SCHEME_SIP) || scheme.equalsIgnoreCase("sms") || scheme.equalsIgnoreCase("smsto") || scheme.equalsIgnoreCase("mailto"))) {
            builder = new StringBuilder(64);
            if (scheme != null) {
                builder.append(scheme);
                builder.append(':');
            }
            if (ssp != null) {
                builder.append(ssp);
            }
            return builder.toString();
        }
        builder = new StringBuilder(64);
        builder.append(scheme);
        builder.append(':');
        if (ssp != null) {
            for (int i = 0; i < ssp.length(); i++) {
                char c = ssp.charAt(i);
                if (c == '-' || c == '@' || c == '.') {
                    builder.append(c);
                } else {
                    builder.append('x');
                }
            }
        }
        return builder.toString();
    }

    public static Uri parse(String uriString) {
        return new StringUri(null);
    }

    public static Uri fromFile(File file) {
        if (file == null) {
            throw new NullPointerException(ContentResolver.SCHEME_FILE);
        }
        return new HierarchicalUri(Part.EMPTY, PathPart.fromDecoded(file.getAbsolutePath()), Part.NULL, Part.NULL, null);
    }

    public static Uri fromParts(String scheme, String ssp, String fragment) {
        if (scheme == null) {
            throw new NullPointerException("scheme");
        } else if (ssp != null) {
            return new OpaqueUri(Part.fromDecoded(ssp), Part.fromDecoded(fragment), null);
        } else {
            throw new NullPointerException("ssp");
        }
    }

    public Set<String> getQueryParameterNames() {
        if (isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }
        String query = getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet();
        int start = 0;
        do {
            int end;
            int next = query.indexOf(38, start);
            if (next == NOT_FOUND) {
                end = query.length();
            } else {
                end = next;
            }
            int separator = query.indexOf(61, start);
            if (separator > end || separator == NOT_FOUND) {
                separator = end;
            }
            names.add(decode(query.substring(start, separator)));
            start = end + 1;
        } while (start < query.length());
        return Collections.unmodifiableSet(names);
    }

    public List<String> getQueryParameters(String key) {
        if (isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        } else if (key == null) {
            throw new NullPointerException(RawData.COLUMN_KEY);
        } else {
            String query = getEncodedQuery();
            if (query == null) {
                return Collections.emptyList();
            }
            try {
                String encodedKey = URLEncoder.encode(key, DEFAULT_ENCODING);
                ArrayList<String> values = new ArrayList();
                int start = 0;
                while (true) {
                    int nextAmpersand = query.indexOf(38, start);
                    int end = nextAmpersand != NOT_FOUND ? nextAmpersand : query.length();
                    int separator = query.indexOf(61, start);
                    if (separator > end || separator == NOT_FOUND) {
                        separator = end;
                    }
                    if (separator - start == encodedKey.length() && query.regionMatches(start, encodedKey, 0, encodedKey.length())) {
                        if (separator == end) {
                            values.add(ProxyInfo.LOCAL_EXCL_LIST);
                        } else {
                            values.add(decode(query.substring(separator + 1, end)));
                        }
                    }
                    if (nextAmpersand == NOT_FOUND) {
                        return Collections.unmodifiableList(values);
                    }
                    start = nextAmpersand + 1;
                }
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }
    }

    public String getQueryParameter(String key) {
        if (isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        } else if (key == null) {
            throw new NullPointerException(RawData.COLUMN_KEY);
        } else {
            String query = getEncodedQuery();
            if (query == null) {
                return null;
            }
            int end;
            int separator;
            String encodedKey = encode(key, null);
            int length = query.length();
            int start = 0;
            while (true) {
                int nextAmpersand = query.indexOf(38, start);
                if (nextAmpersand != NOT_FOUND) {
                    end = nextAmpersand;
                } else {
                    end = length;
                }
                separator = query.indexOf(61, start);
                if (separator > end || separator == NOT_FOUND) {
                    separator = end;
                }
                if (separator - start == encodedKey.length() && query.regionMatches(start, encodedKey, 0, encodedKey.length())) {
                    break;
                } else if (nextAmpersand == NOT_FOUND) {
                    return null;
                } else {
                    start = nextAmpersand + 1;
                }
            }
            if (separator == end) {
                return ProxyInfo.LOCAL_EXCL_LIST;
            }
            return UriCodec.decode(query.substring(separator + 1, end), true, StandardCharsets.UTF_8, false);
        }
    }

    public boolean getBooleanQueryParameter(String key, boolean defaultValue) {
        String flag = getQueryParameter(key);
        if (flag == null) {
            return defaultValue;
        }
        flag = flag.toLowerCase(Locale.ROOT);
        boolean z = ("false".equals(flag) || WifiEnterpriseConfig.ENGINE_DISABLE.equals(flag)) ? false : true;
        return z;
    }

    public Uri normalizeScheme() {
        String scheme = getScheme();
        if (scheme == null) {
            return this;
        }
        String lowerScheme = scheme.toLowerCase(Locale.ROOT);
        return !scheme.equals(lowerScheme) ? buildUpon().scheme(lowerScheme).build() : this;
    }

    public static void writeToParcel(Parcel out, Uri uri) {
        if (uri == null) {
            out.writeInt(0);
        } else {
            uri.writeToParcel(out, 0);
        }
    }

    public static String encode(String s) {
        return encode(s, null);
    }

    public static String encode(String s, String allow) {
        if (s == null) {
            return null;
        }
        StringBuilder encoded = null;
        int oldLength = s.length();
        int current = 0;
        while (current < oldLength) {
            int nextToEncode = current;
            while (nextToEncode < oldLength && isAllowed(s.charAt(nextToEncode), allow)) {
                nextToEncode++;
            }
            if (nextToEncode != oldLength) {
                if (encoded == null) {
                    encoded = new StringBuilder();
                }
                if (nextToEncode > current) {
                    encoded.append(s, current, nextToEncode);
                }
                current = nextToEncode;
                int nextAllowed = current + 1;
                while (nextAllowed < oldLength && !isAllowed(s.charAt(nextAllowed), allow)) {
                    nextAllowed++;
                }
                try {
                    byte[] bytes = s.substring(current, nextAllowed).getBytes(DEFAULT_ENCODING);
                    int bytesLength = bytes.length;
                    for (int i = 0; i < bytesLength; i++) {
                        encoded.append('%');
                        encoded.append(HEX_DIGITS[(bytes[i] & LayoutParams.SOFT_INPUT_MASK_ADJUST) >> 4]);
                        encoded.append(HEX_DIGITS[bytes[i] & 15]);
                    }
                    current = nextAllowed;
                } catch (UnsupportedEncodingException e) {
                    throw new AssertionError(e);
                }
            } else if (current == 0) {
                return s;
            } else {
                encoded.append(s, current, oldLength);
                return encoded.toString();
            }
        }
        return encoded != null ? encoded.toString() : s;
    }

    private static boolean isAllowed(char c, String allow) {
        return (c >= 'A' && c <= 'Z') || ((c >= 'a' && c <= 'z') || !((c < '0' || c > '9') && "_-!.~'()*".indexOf(c) == NOT_FOUND && (allow == null || allow.indexOf(c) == NOT_FOUND)));
    }

    public static String decode(String s) {
        if (s == null) {
            return null;
        }
        return UriCodec.decode(s, false, StandardCharsets.UTF_8, false);
    }

    public static Uri withAppendedPath(Uri baseUri, String pathSegment) {
        return baseUri.buildUpon().appendEncodedPath(pathSegment).build();
    }

    public Uri getCanonicalUri() {
        if (!ContentResolver.SCHEME_FILE.equals(getScheme())) {
            return this;
        }
        try {
            String canonicalPath = new File(getPath()).getCanonicalPath();
            if (Environment.isExternalStorageEmulated()) {
                String legacyPath = Environment.getLegacyExternalStorageDirectory().toString();
                if (canonicalPath.startsWith(legacyPath)) {
                    return fromFile(new File(Environment.getExternalStorageDirectory().toString(), canonicalPath.substring(legacyPath.length() + 1)));
                }
            }
            return fromFile(new File(canonicalPath));
        } catch (IOException e) {
            return this;
        }
    }

    public void checkFileUriExposed(String location) {
        if (ContentResolver.SCHEME_FILE.equals(getScheme())) {
            StrictMode.onFileUriExposed(location);
        }
    }

    public boolean isPathPrefixMatch(Uri prefix) {
        if (!Objects.equals(getScheme(), prefix.getScheme()) || !Objects.equals(getAuthority(), prefix.getAuthority())) {
            return false;
        }
        List<String> seg = getPathSegments();
        List<String> prefixSeg = prefix.getPathSegments();
        int prefixSize = prefixSeg.size();
        if (seg.size() < prefixSize) {
            return false;
        }
        for (int i = 0; i < prefixSize; i++) {
            if (!Objects.equals(seg.get(i), prefixSeg.get(i))) {
                return false;
            }
        }
        return true;
    }
}
