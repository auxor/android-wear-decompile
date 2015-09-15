package com.android.org.bouncycastle.asn1.misc;

import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface MiscObjectIdentifiers {
    public static final ASN1ObjectIdentifier entrust;
    public static final ASN1ObjectIdentifier entrustVersionExtension;
    public static final ASN1ObjectIdentifier netscape;
    public static final ASN1ObjectIdentifier netscapeBaseURL;
    public static final ASN1ObjectIdentifier netscapeCARevocationURL;
    public static final ASN1ObjectIdentifier netscapeCApolicyURL;
    public static final ASN1ObjectIdentifier netscapeCertComment;
    public static final ASN1ObjectIdentifier netscapeCertType;
    public static final ASN1ObjectIdentifier netscapeRenewalURL;
    public static final ASN1ObjectIdentifier netscapeRevocationURL;
    public static final ASN1ObjectIdentifier netscapeSSLServerName;
    public static final ASN1ObjectIdentifier novell;
    public static final ASN1ObjectIdentifier novellSecurityAttribs;
    public static final ASN1ObjectIdentifier verisign;
    public static final ASN1ObjectIdentifier verisignCzagExtension;
    public static final ASN1ObjectIdentifier verisignDnbDunsNumber;

    static {
        netscape = new ASN1ObjectIdentifier("2.16.840.1.113730.1");
        netscapeCertType = netscape.branch("1");
        netscapeBaseURL = netscape.branch("2");
        netscapeRevocationURL = netscape.branch("3");
        netscapeCARevocationURL = netscape.branch("4");
        netscapeRenewalURL = netscape.branch("7");
        netscapeCApolicyURL = netscape.branch("8");
        netscapeSSLServerName = netscape.branch("12");
        netscapeCertComment = netscape.branch("13");
        verisign = new ASN1ObjectIdentifier("2.16.840.1.113733.1");
        verisignCzagExtension = verisign.branch("6.3");
        verisignDnbDunsNumber = verisign.branch("6.15");
        novell = new ASN1ObjectIdentifier("2.16.840.1.113719");
        novellSecurityAttribs = novell.branch("1.9.4.1");
        entrust = new ASN1ObjectIdentifier("1.2.840.113533.7");
        entrustVersionExtension = entrust.branch("65.0");
    }
}
