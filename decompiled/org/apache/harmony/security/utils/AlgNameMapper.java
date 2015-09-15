package org.apache.harmony.security.utils;

import java.security.Provider;
import java.security.Security;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.harmony.security.asn1.ObjectIdentifier;
import org.apache.harmony.security.fortress.Services;

public class AlgNameMapper {
    private static final Map<String, String> alg2OidMap = null;
    private static final Map<String, String> algAliasesMap = null;
    private static volatile int cacheVersion;
    private static final String[][] knownAlgMappings = null;
    private static final Map<String, String> oid2AlgMap = null;
    private static final String[] serviceName = null;
    private static AlgNameMapperSource source;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.security.utils.AlgNameMapper.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.security.utils.AlgNameMapper.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.utils.AlgNameMapper.<clinit>():void");
    }

    private AlgNameMapper() {
    }

    private static synchronized void checkCacheVersion() {
        synchronized (AlgNameMapper.class) {
            int newCacheVersion = Services.getCacheVersion();
            if (newCacheVersion != cacheVersion) {
                for (Provider element : Security.getProviders()) {
                    selectEntries(element);
                }
                cacheVersion = newCacheVersion;
            }
        }
    }

    public static String map2OID(String algName) {
        checkCacheVersion();
        String result = (String) alg2OidMap.get(algName.toUpperCase(Locale.US));
        if (result != null) {
            return result;
        }
        AlgNameMapperSource s = source;
        if (s != null) {
            return s.mapNameToOid(algName);
        }
        return null;
    }

    public static String map2AlgName(String oid) {
        checkCacheVersion();
        String algUC = (String) oid2AlgMap.get(oid);
        if (algUC != null) {
            return (String) algAliasesMap.get(algUC);
        }
        AlgNameMapperSource s = source;
        if (s != null) {
            return s.mapOidToName(oid);
        }
        return null;
    }

    public static String getStandardName(String algName) {
        return (String) algAliasesMap.get(algName.toUpperCase(Locale.US));
    }

    private static void selectEntries(Provider p) {
        Set<Entry<Object, Object>> entrySet = p.entrySet();
        for (String service : serviceName) {
            String keyPrfix2find = "Alg.Alias." + service + ".";
            for (Entry<Object, Object> me : entrySet) {
                String key = (String) me.getKey();
                if (key.startsWith(keyPrfix2find)) {
                    String alias = key.substring(keyPrfix2find.length());
                    String alg = (String) me.getValue();
                    String algUC = alg.toUpperCase(Locale.US);
                    if (isOID(alias)) {
                        if (alias.startsWith("OID.")) {
                            alias = alias.substring(4);
                        }
                        boolean oid2AlgContains = oid2AlgMap.containsKey(alias);
                        boolean alg2OidContains = alg2OidMap.containsKey(algUC);
                        if (!oid2AlgContains || !alg2OidContains) {
                            if (!oid2AlgContains) {
                                oid2AlgMap.put(alias, algUC);
                            }
                            if (!alg2OidContains) {
                                alg2OidMap.put(algUC, alias);
                            }
                            algAliasesMap.put(algUC, alg);
                        }
                    } else if (!algAliasesMap.containsKey(alias.toUpperCase(Locale.US))) {
                        algAliasesMap.put(alias.toUpperCase(Locale.US), alg);
                    }
                }
            }
        }
    }

    public static boolean isOID(String alias) {
        return ObjectIdentifier.isOID(normalize(alias));
    }

    public static String normalize(String oid) {
        return oid.startsWith("OID.") ? oid.substring(4) : oid;
    }

    public static void setSource(AlgNameMapperSource source) {
        source = source;
    }
}
