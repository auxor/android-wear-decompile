package com.android.org.bouncycastle.asn1.kisa;

import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface KISAObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_mod_cms_seed;
    public static final ASN1ObjectIdentifier id_npki_app_cmsSeed_wrap;
    public static final ASN1ObjectIdentifier id_seedCBC;
    public static final ASN1ObjectIdentifier id_seedMAC;
    public static final ASN1ObjectIdentifier pbeWithSHA1AndSEED_CBC;

    static {
        id_seedCBC = new ASN1ObjectIdentifier("1.2.410.200004.1.4");
        id_seedMAC = new ASN1ObjectIdentifier("1.2.410.200004.1.7");
        pbeWithSHA1AndSEED_CBC = new ASN1ObjectIdentifier("1.2.410.200004.1.15");
        id_npki_app_cmsSeed_wrap = new ASN1ObjectIdentifier("1.2.410.200004.7.1.1.1");
        id_mod_cms_seed = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.0.24");
    }
}
