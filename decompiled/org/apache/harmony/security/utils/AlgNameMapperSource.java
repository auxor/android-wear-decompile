package org.apache.harmony.security.utils;

public interface AlgNameMapperSource {
    String mapNameToOid(String str);

    String mapOidToName(String str);
}
