package android.content;

import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PatternMatcher;
import android.util.AndroidException;
import android.util.Log;
import android.util.Printer;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class IntentFilter implements Parcelable {
    private static final String ACTION_STR = "action";
    private static final String AUTH_STR = "auth";
    private static final String CAT_STR = "cat";
    public static final Creator<IntentFilter> CREATOR;
    private static final String HOST_STR = "host";
    private static final String LITERAL_STR = "literal";
    public static final int MATCH_ADJUSTMENT_MASK = 65535;
    public static final int MATCH_ADJUSTMENT_NORMAL = 32768;
    public static final int MATCH_CATEGORY_EMPTY = 1048576;
    public static final int MATCH_CATEGORY_HOST = 3145728;
    public static final int MATCH_CATEGORY_MASK = 268369920;
    public static final int MATCH_CATEGORY_PATH = 5242880;
    public static final int MATCH_CATEGORY_PORT = 4194304;
    public static final int MATCH_CATEGORY_SCHEME = 2097152;
    public static final int MATCH_CATEGORY_SCHEME_SPECIFIC_PART = 5767168;
    public static final int MATCH_CATEGORY_TYPE = 6291456;
    private static final String NAME_STR = "name";
    public static final int NO_MATCH_ACTION = -3;
    public static final int NO_MATCH_CATEGORY = -4;
    public static final int NO_MATCH_DATA = -2;
    public static final int NO_MATCH_TYPE = -1;
    private static final String PATH_STR = "path";
    private static final String PORT_STR = "port";
    private static final String PREFIX_STR = "prefix";
    private static final String SCHEME_STR = "scheme";
    private static final String SGLOB_STR = "sglob";
    private static final String SSP_STR = "ssp";
    public static final int SYSTEM_HIGH_PRIORITY = 1000;
    public static final int SYSTEM_LOW_PRIORITY = -1000;
    private static final String TYPE_STR = "type";
    private final ArrayList<String> mActions;
    private ArrayList<String> mCategories;
    private ArrayList<AuthorityEntry> mDataAuthorities;
    private ArrayList<PatternMatcher> mDataPaths;
    private ArrayList<PatternMatcher> mDataSchemeSpecificParts;
    private ArrayList<String> mDataSchemes;
    private ArrayList<String> mDataTypes;
    private boolean mHasPartialTypes;
    private int mPriority;

    public static final class AuthorityEntry {
        private final String mHost;
        private final String mOrigHost;
        private final int mPort;
        private final boolean mWild;

        public AuthorityEntry(String host, String port) {
            boolean z = false;
            this.mOrigHost = host;
            if (host.length() > 0 && host.charAt(0) == '*') {
                z = true;
            }
            this.mWild = z;
            if (this.mWild) {
                host = host.substring(1).intern();
            }
            this.mHost = host;
            this.mPort = port != null ? Integer.parseInt(port) : IntentFilter.NO_MATCH_TYPE;
        }

        AuthorityEntry(Parcel src) {
            this.mOrigHost = src.readString();
            this.mHost = src.readString();
            this.mWild = src.readInt() != 0;
            this.mPort = src.readInt();
        }

        void writeToParcel(Parcel dest) {
            dest.writeString(this.mOrigHost);
            dest.writeString(this.mHost);
            dest.writeInt(this.mWild ? 1 : 0);
            dest.writeInt(this.mPort);
        }

        public String getHost() {
            return this.mOrigHost;
        }

        public int getPort() {
            return this.mPort;
        }

        public boolean match(AuthorityEntry other) {
            if (this.mWild == other.mWild && this.mHost.equals(other.mHost) && this.mPort == other.mPort) {
                return true;
            }
            return false;
        }

        public int match(Uri data) {
            String host = data.getHost();
            if (host == null) {
                return IntentFilter.NO_MATCH_DATA;
            }
            if (this.mWild) {
                if (host.length() < this.mHost.length()) {
                    return IntentFilter.NO_MATCH_DATA;
                }
                host = host.substring(host.length() - this.mHost.length());
            }
            if (host.compareToIgnoreCase(this.mHost) != 0) {
                return IntentFilter.NO_MATCH_DATA;
            }
            if (this.mPort < 0) {
                return IntentFilter.MATCH_CATEGORY_HOST;
            }
            if (this.mPort == data.getPort()) {
                return IntentFilter.MATCH_CATEGORY_PORT;
            }
            return IntentFilter.NO_MATCH_DATA;
        }
    }

    public static class MalformedMimeTypeException extends AndroidException {
        public MalformedMimeTypeException(String name) {
            super(name);
        }
    }

    private static int findStringInSet(String[] set, String string, int[] lengths, int lenPos) {
        if (set == null) {
            return NO_MATCH_TYPE;
        }
        int N = lengths[lenPos];
        for (int i = 0; i < N; i++) {
            if (set[i].equals(string)) {
                return i;
            }
        }
        return NO_MATCH_TYPE;
    }

    private static String[] addStringToSet(String[] set, String string, int[] lengths, int lenPos) {
        if (findStringInSet(set, string, lengths, lenPos) >= 0) {
            return set;
        }
        if (set == null) {
            return new String[]{string, 1};
        }
        int N = lengths[lenPos];
        if (N < set.length) {
            set[N] = string;
            lengths[lenPos] = N + 1;
            return set;
        }
        String[] newSet = new String[(((N * 3) / 2) + 2)];
        System.arraycopy(set, 0, newSet, 0, N);
        set = newSet;
        set[N] = string;
        lengths[lenPos] = N + 1;
        return set;
    }

    private static String[] removeStringFromSet(String[] set, String string, int[] lengths, int lenPos) {
        int pos = findStringInSet(set, string, lengths, lenPos);
        if (pos < 0) {
            return set;
        }
        int N = lengths[lenPos];
        if (N > set.length / 4) {
            int copyLen = N - (pos + 1);
            if (copyLen > 0) {
                System.arraycopy(set, pos + 1, set, pos, copyLen);
            }
            set[N + NO_MATCH_TYPE] = null;
            lengths[lenPos] = N + NO_MATCH_TYPE;
            return set;
        }
        String[] newSet = new String[(set.length / 3)];
        if (pos > 0) {
            System.arraycopy(set, 0, newSet, 0, pos);
        }
        if (pos + 1 < N) {
            System.arraycopy(set, pos + 1, newSet, pos, N - (pos + 1));
        }
        return newSet;
    }

    public static IntentFilter create(String action, String dataType) {
        try {
            return new IntentFilter(action, dataType);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Bad MIME type", e);
        }
    }

    public IntentFilter() {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList();
    }

    public IntentFilter(String action) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList();
        addAction(action);
    }

    public IntentFilter(String action, String dataType) throws MalformedMimeTypeException {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList();
        addAction(action);
        addDataType(dataType);
    }

    public IntentFilter(IntentFilter o) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = o.mPriority;
        this.mActions = new ArrayList(o.mActions);
        if (o.mCategories != null) {
            this.mCategories = new ArrayList(o.mCategories);
        }
        if (o.mDataTypes != null) {
            this.mDataTypes = new ArrayList(o.mDataTypes);
        }
        if (o.mDataSchemes != null) {
            this.mDataSchemes = new ArrayList(o.mDataSchemes);
        }
        if (o.mDataSchemeSpecificParts != null) {
            this.mDataSchemeSpecificParts = new ArrayList(o.mDataSchemeSpecificParts);
        }
        if (o.mDataAuthorities != null) {
            this.mDataAuthorities = new ArrayList(o.mDataAuthorities);
        }
        if (o.mDataPaths != null) {
            this.mDataPaths = new ArrayList(o.mDataPaths);
        }
        this.mHasPartialTypes = o.mHasPartialTypes;
    }

    public final void setPriority(int priority) {
        this.mPriority = priority;
    }

    public final int getPriority() {
        return this.mPriority;
    }

    public final void addAction(String action) {
        if (!this.mActions.contains(action)) {
            this.mActions.add(action.intern());
        }
    }

    public final int countActions() {
        return this.mActions.size();
    }

    public final String getAction(int index) {
        return (String) this.mActions.get(index);
    }

    public final boolean hasAction(String action) {
        return action != null && this.mActions.contains(action);
    }

    public final boolean matchAction(String action) {
        return hasAction(action);
    }

    public final Iterator<String> actionsIterator() {
        return this.mActions != null ? this.mActions.iterator() : null;
    }

    public final void addDataType(String type) throws MalformedMimeTypeException {
        int slashpos = type.indexOf(47);
        int typelen = type.length();
        if (slashpos <= 0 || typelen < slashpos + 2) {
            throw new MalformedMimeTypeException(type);
        }
        if (this.mDataTypes == null) {
            this.mDataTypes = new ArrayList();
        }
        if (typelen == slashpos + 2 && type.charAt(slashpos + 1) == '*') {
            String str = type.substring(0, slashpos);
            if (!this.mDataTypes.contains(str)) {
                this.mDataTypes.add(str.intern());
            }
            this.mHasPartialTypes = true;
        } else if (!this.mDataTypes.contains(type)) {
            this.mDataTypes.add(type.intern());
        }
    }

    public final boolean hasDataType(String type) {
        return this.mDataTypes != null && findMimeType(type);
    }

    public final boolean hasExactDataType(String type) {
        return this.mDataTypes != null && this.mDataTypes.contains(type);
    }

    public final int countDataTypes() {
        return this.mDataTypes != null ? this.mDataTypes.size() : 0;
    }

    public final String getDataType(int index) {
        return (String) this.mDataTypes.get(index);
    }

    public final Iterator<String> typesIterator() {
        return this.mDataTypes != null ? this.mDataTypes.iterator() : null;
    }

    public final void addDataScheme(String scheme) {
        if (this.mDataSchemes == null) {
            this.mDataSchemes = new ArrayList();
        }
        if (!this.mDataSchemes.contains(scheme)) {
            this.mDataSchemes.add(scheme.intern());
        }
    }

    public final int countDataSchemes() {
        return this.mDataSchemes != null ? this.mDataSchemes.size() : 0;
    }

    public final String getDataScheme(int index) {
        return (String) this.mDataSchemes.get(index);
    }

    public final boolean hasDataScheme(String scheme) {
        return this.mDataSchemes != null && this.mDataSchemes.contains(scheme);
    }

    public final Iterator<String> schemesIterator() {
        return this.mDataSchemes != null ? this.mDataSchemes.iterator() : null;
    }

    public final void addDataSchemeSpecificPart(String ssp, int type) {
        addDataSchemeSpecificPart(new PatternMatcher(ssp, type));
    }

    public final void addDataSchemeSpecificPart(PatternMatcher ssp) {
        if (this.mDataSchemeSpecificParts == null) {
            this.mDataSchemeSpecificParts = new ArrayList();
        }
        this.mDataSchemeSpecificParts.add(ssp);
    }

    public final int countDataSchemeSpecificParts() {
        return this.mDataSchemeSpecificParts != null ? this.mDataSchemeSpecificParts.size() : 0;
    }

    public final PatternMatcher getDataSchemeSpecificPart(int index) {
        return (PatternMatcher) this.mDataSchemeSpecificParts.get(index);
    }

    public final boolean hasDataSchemeSpecificPart(String data) {
        if (this.mDataSchemeSpecificParts == null) {
            return false;
        }
        int numDataSchemeSpecificParts = this.mDataSchemeSpecificParts.size();
        for (int i = 0; i < numDataSchemeSpecificParts; i++) {
            if (((PatternMatcher) this.mDataSchemeSpecificParts.get(i)).match(data)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasDataSchemeSpecificPart(PatternMatcher ssp) {
        if (this.mDataSchemeSpecificParts == null) {
            return false;
        }
        int numDataSchemeSpecificParts = this.mDataSchemeSpecificParts.size();
        for (int i = 0; i < numDataSchemeSpecificParts; i++) {
            PatternMatcher pe = (PatternMatcher) this.mDataSchemeSpecificParts.get(i);
            if (pe.getType() == ssp.getType() && pe.getPath().equals(ssp.getPath())) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> schemeSpecificPartsIterator() {
        return this.mDataSchemeSpecificParts != null ? this.mDataSchemeSpecificParts.iterator() : null;
    }

    public final void addDataAuthority(String host, String port) {
        if (port != null) {
            port = port.intern();
        }
        addDataAuthority(new AuthorityEntry(host.intern(), port));
    }

    public final void addDataAuthority(AuthorityEntry ent) {
        if (this.mDataAuthorities == null) {
            this.mDataAuthorities = new ArrayList();
        }
        this.mDataAuthorities.add(ent);
    }

    public final int countDataAuthorities() {
        return this.mDataAuthorities != null ? this.mDataAuthorities.size() : 0;
    }

    public final AuthorityEntry getDataAuthority(int index) {
        return (AuthorityEntry) this.mDataAuthorities.get(index);
    }

    public final boolean hasDataAuthority(Uri data) {
        return matchDataAuthority(data) >= 0;
    }

    public final boolean hasDataAuthority(AuthorityEntry auth) {
        if (this.mDataAuthorities == null) {
            return false;
        }
        int numDataAuthorities = this.mDataAuthorities.size();
        for (int i = 0; i < numDataAuthorities; i++) {
            if (((AuthorityEntry) this.mDataAuthorities.get(i)).match(auth)) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<AuthorityEntry> authoritiesIterator() {
        return this.mDataAuthorities != null ? this.mDataAuthorities.iterator() : null;
    }

    public final void addDataPath(String path, int type) {
        addDataPath(new PatternMatcher(path.intern(), type));
    }

    public final void addDataPath(PatternMatcher path) {
        if (this.mDataPaths == null) {
            this.mDataPaths = new ArrayList();
        }
        this.mDataPaths.add(path);
    }

    public final int countDataPaths() {
        return this.mDataPaths != null ? this.mDataPaths.size() : 0;
    }

    public final PatternMatcher getDataPath(int index) {
        return (PatternMatcher) this.mDataPaths.get(index);
    }

    public final boolean hasDataPath(String data) {
        if (this.mDataPaths == null) {
            return false;
        }
        int numDataPaths = this.mDataPaths.size();
        for (int i = 0; i < numDataPaths; i++) {
            if (((PatternMatcher) this.mDataPaths.get(i)).match(data)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasDataPath(PatternMatcher path) {
        if (this.mDataPaths == null) {
            return false;
        }
        int numDataPaths = this.mDataPaths.size();
        for (int i = 0; i < numDataPaths; i++) {
            PatternMatcher pe = (PatternMatcher) this.mDataPaths.get(i);
            if (pe.getType() == path.getType() && pe.getPath().equals(path.getPath())) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> pathsIterator() {
        return this.mDataPaths != null ? this.mDataPaths.iterator() : null;
    }

    public final int matchDataAuthority(Uri data) {
        if (this.mDataAuthorities == null) {
            return NO_MATCH_DATA;
        }
        int numDataAuthorities = this.mDataAuthorities.size();
        for (int i = 0; i < numDataAuthorities; i++) {
            int match = ((AuthorityEntry) this.mDataAuthorities.get(i)).match(data);
            if (match >= 0) {
                return match;
            }
        }
        return NO_MATCH_DATA;
    }

    public final int matchData(String type, String scheme, Uri data) {
        ArrayList<String> types = this.mDataTypes;
        ArrayList<String> schemes = this.mDataSchemes;
        int match = MATCH_CATEGORY_EMPTY;
        if (types != null || schemes != null) {
            if (schemes != null) {
                if (scheme == null) {
                    scheme = ProxyInfo.LOCAL_EXCL_LIST;
                }
                if (!schemes.contains(scheme)) {
                    return NO_MATCH_DATA;
                }
                match = MATCH_CATEGORY_SCHEME;
                if (this.mDataSchemeSpecificParts != null) {
                    if (hasDataSchemeSpecificPart(data.getSchemeSpecificPart())) {
                        match = MATCH_CATEGORY_SCHEME_SPECIFIC_PART;
                    } else {
                        match = NO_MATCH_DATA;
                    }
                }
                if (!(match == MATCH_CATEGORY_SCHEME_SPECIFIC_PART || this.mDataAuthorities == null)) {
                    int authMatch = matchDataAuthority(data);
                    if (authMatch < 0) {
                        return NO_MATCH_DATA;
                    }
                    if (this.mDataPaths == null) {
                        match = authMatch;
                    } else if (!hasDataPath(data.getPath())) {
                        return NO_MATCH_DATA;
                    } else {
                        match = MATCH_CATEGORY_PATH;
                    }
                }
                if (match == NO_MATCH_DATA) {
                    return NO_MATCH_DATA;
                }
            } else if (!(scheme == null || ProxyInfo.LOCAL_EXCL_LIST.equals(scheme) || ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme))) {
                return NO_MATCH_DATA;
            }
            if (types != null) {
                if (!findMimeType(type)) {
                    return NO_MATCH_TYPE;
                }
                match = MATCH_CATEGORY_TYPE;
            } else if (type != null) {
                return NO_MATCH_TYPE;
            }
            return MATCH_ADJUSTMENT_NORMAL + match;
        } else if (type == null && data == null) {
            return 1081344;
        } else {
            return NO_MATCH_DATA;
        }
    }

    public final void addCategory(String category) {
        if (this.mCategories == null) {
            this.mCategories = new ArrayList();
        }
        if (!this.mCategories.contains(category)) {
            this.mCategories.add(category.intern());
        }
    }

    public final int countCategories() {
        return this.mCategories != null ? this.mCategories.size() : 0;
    }

    public final String getCategory(int index) {
        return (String) this.mCategories.get(index);
    }

    public final boolean hasCategory(String category) {
        return this.mCategories != null && this.mCategories.contains(category);
    }

    public final Iterator<String> categoriesIterator() {
        return this.mCategories != null ? this.mCategories.iterator() : null;
    }

    public final String matchCategories(Set<String> categories) {
        if (categories == null) {
            return null;
        }
        Iterator<String> it = categories.iterator();
        if (this.mCategories != null) {
            while (it.hasNext()) {
                String category = (String) it.next();
                if (!this.mCategories.contains(category)) {
                    return category;
                }
            }
            return null;
        } else if (it.hasNext()) {
            return (String) it.next();
        } else {
            return null;
        }
    }

    public final int match(ContentResolver resolver, Intent intent, boolean resolve, String logTag) {
        return match(intent.getAction(), resolve ? intent.resolveType(resolver) : intent.getType(), intent.getScheme(), intent.getData(), intent.getCategories(), logTag);
    }

    public final int match(String action, String type, String scheme, Uri data, Set<String> categories, String logTag) {
        if (action != null && !matchAction(action)) {
            return NO_MATCH_ACTION;
        }
        int dataMatch = matchData(type, scheme, data);
        if (dataMatch < 0 || matchCategories(categories) == null) {
            return dataMatch;
        }
        return NO_MATCH_CATEGORY;
    }

    public void writeToXml(XmlSerializer serializer) throws IOException {
        int i;
        int N = countActions();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, ACTION_STR);
            serializer.attribute(null, NAME_STR, (String) this.mActions.get(i));
            serializer.endTag(null, ACTION_STR);
        }
        N = countCategories();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, CAT_STR);
            serializer.attribute(null, NAME_STR, (String) this.mCategories.get(i));
            serializer.endTag(null, CAT_STR);
        }
        N = countDataTypes();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, TYPE_STR);
            String type = (String) this.mDataTypes.get(i);
            if (type.indexOf(47) < 0) {
                type = type + "/*";
            }
            serializer.attribute(null, NAME_STR, type);
            serializer.endTag(null, TYPE_STR);
        }
        N = countDataSchemes();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, SCHEME_STR);
            serializer.attribute(null, NAME_STR, (String) this.mDataSchemes.get(i));
            serializer.endTag(null, SCHEME_STR);
        }
        N = countDataSchemeSpecificParts();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, SSP_STR);
            PatternMatcher pe = (PatternMatcher) this.mDataSchemeSpecificParts.get(i);
            switch (pe.getType()) {
                case Toast.LENGTH_SHORT /*0*/:
                    serializer.attribute(null, LITERAL_STR, pe.getPath());
                    break;
                case Toast.LENGTH_LONG /*1*/:
                    serializer.attribute(null, PREFIX_STR, pe.getPath());
                    break;
                case Action.MERGE_IGNORE /*2*/:
                    serializer.attribute(null, SGLOB_STR, pe.getPath());
                    break;
                default:
                    break;
            }
            serializer.endTag(null, SSP_STR);
        }
        N = countDataAuthorities();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, AUTH_STR);
            AuthorityEntry ae = (AuthorityEntry) this.mDataAuthorities.get(i);
            serializer.attribute(null, HOST_STR, ae.getHost());
            if (ae.getPort() >= 0) {
                serializer.attribute(null, PORT_STR, Integer.toString(ae.getPort()));
            }
            serializer.endTag(null, AUTH_STR);
        }
        N = countDataPaths();
        for (i = 0; i < N; i++) {
            serializer.startTag(null, PATH_STR);
            pe = (PatternMatcher) this.mDataPaths.get(i);
            switch (pe.getType()) {
                case Toast.LENGTH_SHORT /*0*/:
                    serializer.attribute(null, LITERAL_STR, pe.getPath());
                    break;
                case Toast.LENGTH_LONG /*1*/:
                    serializer.attribute(null, PREFIX_STR, pe.getPath());
                    break;
                case Action.MERGE_IGNORE /*2*/:
                    serializer.attribute(null, SGLOB_STR, pe.getPath());
                    break;
                default:
                    break;
            }
            serializer.endTag(null, PATH_STR);
        }
    }

    public void readFromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                String tagName = parser.getName();
                String name;
                if (tagName.equals(ACTION_STR)) {
                    name = parser.getAttributeValue(null, NAME_STR);
                    if (name != null) {
                        addAction(name);
                    }
                } else if (tagName.equals(CAT_STR)) {
                    name = parser.getAttributeValue(null, NAME_STR);
                    if (name != null) {
                        addCategory(name);
                    }
                } else if (tagName.equals(TYPE_STR)) {
                    name = parser.getAttributeValue(null, NAME_STR);
                    if (name != null) {
                        try {
                            addDataType(name);
                        } catch (MalformedMimeTypeException e) {
                        }
                    }
                } else if (tagName.equals(SCHEME_STR)) {
                    name = parser.getAttributeValue(null, NAME_STR);
                    if (name != null) {
                        addDataScheme(name);
                    }
                } else if (tagName.equals(SSP_STR)) {
                    String ssp = parser.getAttributeValue(null, LITERAL_STR);
                    if (ssp != null) {
                        addDataSchemeSpecificPart(ssp, 0);
                    } else {
                        ssp = parser.getAttributeValue(null, PREFIX_STR);
                        if (ssp != null) {
                            addDataSchemeSpecificPart(ssp, 1);
                        } else {
                            ssp = parser.getAttributeValue(null, SGLOB_STR);
                            if (ssp != null) {
                                addDataSchemeSpecificPart(ssp, 2);
                            }
                        }
                    }
                } else if (tagName.equals(AUTH_STR)) {
                    String host = parser.getAttributeValue(null, HOST_STR);
                    String port = parser.getAttributeValue(null, PORT_STR);
                    if (host != null) {
                        addDataAuthority(host, port);
                    }
                } else if (tagName.equals(PATH_STR)) {
                    String path = parser.getAttributeValue(null, LITERAL_STR);
                    if (path != null) {
                        addDataPath(path, 0);
                    } else {
                        path = parser.getAttributeValue(null, PREFIX_STR);
                        if (path != null) {
                            addDataPath(path, 1);
                        } else {
                            path = parser.getAttributeValue(null, SGLOB_STR);
                            if (path != null) {
                                addDataPath(path, 2);
                            }
                        }
                    }
                } else {
                    Log.w("IntentFilter", "Unknown tag parsing IntentFilter: " + tagName);
                }
                XmlUtils.skipCurrentTag(parser);
            }
        }
    }

    public void dump(Printer du, String prefix) {
        Iterator<String> it;
        Iterator<PatternMatcher> it2;
        StringBuilder sb = new StringBuilder(InputMethodManager.CONTROL_START_INITIAL);
        if (this.mActions.size() > 0) {
            it = this.mActions.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Action: \"");
                sb.append((String) it.next());
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mCategories != null) {
            it = this.mCategories.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Category: \"");
                sb.append((String) it.next());
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mDataSchemes != null) {
            it = this.mDataSchemes.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Scheme: \"");
                sb.append((String) it.next());
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mDataSchemeSpecificParts != null) {
            it2 = this.mDataSchemeSpecificParts.iterator();
            while (it2.hasNext()) {
                PatternMatcher pe = (PatternMatcher) it2.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Ssp: \"");
                sb.append(pe);
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mDataAuthorities != null) {
            Iterator<AuthorityEntry> it3 = this.mDataAuthorities.iterator();
            while (it3.hasNext()) {
                AuthorityEntry ae = (AuthorityEntry) it3.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Authority: \"");
                sb.append(ae.mHost);
                sb.append("\": ");
                sb.append(ae.mPort);
                if (ae.mWild) {
                    sb.append(" WILD");
                }
                du.println(sb.toString());
            }
        }
        if (this.mDataPaths != null) {
            it2 = this.mDataPaths.iterator();
            while (it2.hasNext()) {
                pe = (PatternMatcher) it2.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Path: \"");
                sb.append(pe);
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mDataTypes != null) {
            it = this.mDataTypes.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Type: \"");
                sb.append((String) it.next());
                sb.append("\"");
                du.println(sb.toString());
            }
        }
        if (this.mPriority != 0 || this.mHasPartialTypes) {
            sb.setLength(0);
            sb.append(prefix);
            sb.append("mPriority=");
            sb.append(this.mPriority);
            sb.append(", mHasPartialTypes=");
            sb.append(this.mHasPartialTypes);
            du.println(sb.toString());
        }
    }

    static {
        CREATOR = new Creator<IntentFilter>() {
            public IntentFilter createFromParcel(Parcel source) {
                return new IntentFilter(null);
            }

            public IntentFilter[] newArray(int size) {
                return new IntentFilter[size];
            }
        };
    }

    public final int describeContents() {
        return 0;
    }

    public final void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2;
        dest.writeStringList(this.mActions);
        if (this.mCategories != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mCategories);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataSchemes != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mDataSchemes);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataTypes != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mDataTypes);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataSchemeSpecificParts != null) {
            int N;
            N = this.mDataSchemeSpecificParts.size();
            dest.writeInt(N);
            for (i = 0; i < N; i++) {
                ((PatternMatcher) this.mDataSchemeSpecificParts.get(i)).writeToParcel(dest, flags);
            }
        } else {
            dest.writeInt(0);
        }
        if (this.mDataAuthorities != null) {
            N = this.mDataAuthorities.size();
            dest.writeInt(N);
            for (i = 0; i < N; i++) {
                ((AuthorityEntry) this.mDataAuthorities.get(i)).writeToParcel(dest);
            }
        } else {
            dest.writeInt(0);
        }
        if (this.mDataPaths != null) {
            N = this.mDataPaths.size();
            dest.writeInt(N);
            for (i = 0; i < N; i++) {
                ((PatternMatcher) this.mDataPaths.get(i)).writeToParcel(dest, flags);
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.mPriority);
        if (this.mHasPartialTypes) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public boolean debugCheck() {
        return true;
    }

    private IntentFilter(Parcel source) {
        int i;
        boolean z = false;
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mActions = new ArrayList();
        source.readStringList(this.mActions);
        if (source.readInt() != 0) {
            this.mCategories = new ArrayList();
            source.readStringList(this.mCategories);
        }
        if (source.readInt() != 0) {
            this.mDataSchemes = new ArrayList();
            source.readStringList(this.mDataSchemes);
        }
        if (source.readInt() != 0) {
            this.mDataTypes = new ArrayList();
            source.readStringList(this.mDataTypes);
        }
        int N = source.readInt();
        if (N > 0) {
            this.mDataSchemeSpecificParts = new ArrayList(N);
            for (i = 0; i < N; i++) {
                this.mDataSchemeSpecificParts.add(new PatternMatcher(source));
            }
        }
        N = source.readInt();
        if (N > 0) {
            this.mDataAuthorities = new ArrayList(N);
            for (i = 0; i < N; i++) {
                this.mDataAuthorities.add(new AuthorityEntry(source));
            }
        }
        N = source.readInt();
        if (N > 0) {
            this.mDataPaths = new ArrayList(N);
            for (i = 0; i < N; i++) {
                this.mDataPaths.add(new PatternMatcher(source));
            }
        }
        this.mPriority = source.readInt();
        if (source.readInt() > 0) {
            z = true;
        }
        this.mHasPartialTypes = z;
    }

    private final boolean findMimeType(String type) {
        ArrayList<String> t = this.mDataTypes;
        if (type == null) {
            return false;
        }
        if (t.contains(type)) {
            return true;
        }
        int typeLength = type.length();
        if (typeLength == 3 && type.equals("*/*")) {
            if (t.isEmpty()) {
                return false;
            }
            return true;
        } else if (this.mHasPartialTypes && t.contains("*")) {
            return true;
        } else {
            int slashpos = type.indexOf(47);
            if (slashpos > 0) {
                if (this.mHasPartialTypes && t.contains(type.substring(0, slashpos))) {
                    return true;
                }
                if (typeLength == slashpos + 2 && type.charAt(slashpos + 1) == '*') {
                    int numTypes = t.size();
                    for (int i = 0; i < numTypes; i++) {
                        if (type.regionMatches(0, (String) t.get(i), 0, slashpos + 1)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}
