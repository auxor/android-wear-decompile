package com.android.org.bouncycastle.jce.provider;

import com.android.org.bouncycastle.asn1.ASN1Encodable;
import com.android.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.org.bouncycastle.asn1.ASN1InputStream;
import com.android.org.bouncycastle.asn1.ASN1Primitive;
import com.android.org.bouncycastle.asn1.ASN1Sequence;
import com.android.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.org.bouncycastle.asn1.DERInteger;
import com.android.org.bouncycastle.asn1.DERObjectIdentifier;
import com.android.org.bouncycastle.asn1.DERSequence;
import com.android.org.bouncycastle.asn1.x509.BasicConstraints;
import com.android.org.bouncycastle.asn1.x509.CRLDistPoint;
import com.android.org.bouncycastle.asn1.x509.DistributionPoint;
import com.android.org.bouncycastle.asn1.x509.DistributionPointName;
import com.android.org.bouncycastle.asn1.x509.GeneralName;
import com.android.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.org.bouncycastle.asn1.x509.GeneralSubtree;
import com.android.org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import com.android.org.bouncycastle.asn1.x509.NameConstraints;
import com.android.org.bouncycastle.asn1.x509.PolicyInformation;
import com.android.org.bouncycastle.asn1.x509.X509Extensions;
import com.android.org.bouncycastle.asn1.x509.X509Name;
import com.android.org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import com.android.org.bouncycastle.math.ec.ECCurve;
import com.android.org.bouncycastle.util.Arrays;
import com.android.org.bouncycastle.x509.ExtendedPKIXParameters;
import com.android.org.bouncycastle.x509.X509CRLStoreSelector;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

public class RFC3280CertPathUtilities {
    public static final String ANY_POLICY = "2.5.29.32.0";
    public static final String AUTHORITY_KEY_IDENTIFIER = null;
    public static final String BASIC_CONSTRAINTS = null;
    public static final String CERTIFICATE_POLICIES = null;
    public static final String CRL_DISTRIBUTION_POINTS = null;
    public static final String CRL_NUMBER = null;
    protected static final int CRL_SIGN = 6;
    private static final PKIXCRLUtil CRL_UTIL = null;
    public static final String DELTA_CRL_INDICATOR = null;
    public static final String FRESHEST_CRL = null;
    public static final String INHIBIT_ANY_POLICY = null;
    public static final String ISSUING_DISTRIBUTION_POINT = null;
    protected static final int KEY_CERT_SIGN = 5;
    public static final String KEY_USAGE = null;
    public static final String NAME_CONSTRAINTS = null;
    public static final String POLICY_CONSTRAINTS = null;
    public static final String POLICY_MAPPINGS = null;
    public static final String SUBJECT_ALTERNATIVE_NAME = null;
    protected static final String[] crlReasons = null;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.<clinit>():void");
    }

    protected static void processCRLB2(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        try {
            IssuingDistributionPoint idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
            if (idp != null) {
                if (idp.getDistributionPoint() != null) {
                    GeneralName[] genNames;
                    int j;
                    ASN1EncodableVector vec;
                    Enumeration e;
                    DistributionPointName dpName = IssuingDistributionPoint.getInstance(idp).getDistributionPoint();
                    List names = new ArrayList();
                    if (dpName.getType() == 0) {
                        genNames = GeneralNames.getInstance(dpName.getName()).getNames();
                        for (Object add : genNames) {
                            names.add(add);
                        }
                    }
                    if (dpName.getType() == 1) {
                        vec = new ASN1EncodableVector();
                        try {
                            e = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(CertPathValidatorUtilities.getIssuerPrincipal(crl).getEncoded())).getObjects();
                            while (e.hasMoreElements()) {
                                vec.add((ASN1Encodable) e.nextElement());
                            }
                            vec.add(dpName.getName());
                            names.add(new GeneralName(X509Name.getInstance(new DERSequence(vec))));
                        } catch (Enumeration e2) {
                            throw new AnnotatedException("Could not read CRL issuer.", e2);
                        }
                    }
                    boolean matches = false;
                    if (dp.getDistributionPoint() != null) {
                        dpName = dp.getDistributionPoint();
                        genNames = null;
                        if (dpName.getType() == 0) {
                            genNames = GeneralNames.getInstance(dpName.getName()).getNames();
                        }
                        if (dpName.getType() == 1) {
                            if (dp.getCRLIssuer() != null) {
                                genNames = dp.getCRLIssuer().getNames();
                            } else {
                                genNames = new GeneralName[1];
                                try {
                                    genNames[0] = new GeneralName(new X509Name((ASN1Sequence) ASN1Primitive.fromByteArray(CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert).getEncoded())));
                                } catch (Enumeration e22) {
                                    throw new AnnotatedException("Could not read certificate issuer.", e22);
                                }
                            }
                            for (j = 0; j < genNames.length; j++) {
                                e22 = ASN1Sequence.getInstance(genNames[j].getName().toASN1Primitive()).getObjects();
                                vec = new ASN1EncodableVector();
                                while (e22.hasMoreElements()) {
                                    vec.add((ASN1Encodable) e22.nextElement());
                                }
                                vec.add(dpName.getName());
                                genNames[j] = new GeneralName(new X509Name(new DERSequence(vec)));
                            }
                        }
                        if (genNames != null) {
                            for (Object add2 : genNames) {
                                if (names.contains(add2)) {
                                    matches = true;
                                    break;
                                }
                            }
                        }
                        if (!matches) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    } else if (dp.getCRLIssuer() == null) {
                        throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                    } else {
                        genNames = dp.getCRLIssuer().getNames();
                        for (Object add22 : genNames) {
                            if (names.contains(add22)) {
                                matches = true;
                                break;
                            }
                        }
                        if (!matches) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    }
                }
                try {
                    BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension) cert, BASIC_CONSTRAINTS));
                    if (cert instanceof X509Certificate) {
                        if (idp.onlyContainsUserCerts() && bc != null && bc.isCA()) {
                            throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                        } else if (idp.onlyContainsCACerts() && (bc == null || !bc.isCA())) {
                            throw new AnnotatedException("End CRL only contains CA certificates.");
                        }
                    }
                    if (idp.onlyContainsAttributeCerts()) {
                        throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
                    }
                } catch (Exception e3) {
                    throw new AnnotatedException("Basic constraints extension could not be decoded.", e3);
                }
            }
        } catch (Exception e32) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e32);
        }
    }

    protected static void processCRLB1(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        ASN1Primitive idp = CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT);
        boolean isIndirect = false;
        if (idp != null && IssuingDistributionPoint.getInstance(idp).isIndirectCRL()) {
            isIndirect = true;
        }
        byte[] issuerBytes = CertPathValidatorUtilities.getIssuerPrincipal(crl).getEncoded();
        boolean matchIssuer = false;
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; j++) {
                if (genNames[j].getTagNo() == 4) {
                    try {
                        if (Arrays.areEqual(genNames[j].getName().toASN1Primitive().getEncoded(), issuerBytes)) {
                            matchIssuer = true;
                        }
                    } catch (IOException e) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                    }
                }
            }
            if (matchIssuer && !isIndirect) {
                throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
            } else if (!matchIssuer) {
                throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
            }
        } else if (CertPathValidatorUtilities.getIssuerPrincipal(crl).equals(CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert))) {
            matchIssuer = true;
        }
        if (!matchIssuer) {
            throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
        }
    }

    protected static ReasonsMask processCRLD(X509CRL crl, DistributionPoint dp) throws AnnotatedException {
        try {
            IssuingDistributionPoint idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
            if (idp != null && idp.getOnlySomeReasons() != null && dp.getReasons() != null) {
                return new ReasonsMask(dp.getReasons()).intersect(new ReasonsMask(idp.getOnlySomeReasons()));
            }
            if ((idp == null || idp.getOnlySomeReasons() == null) && dp.getReasons() == null) {
                return ReasonsMask.allReasons;
            }
            return (dp.getReasons() == null ? ReasonsMask.allReasons : new ReasonsMask(dp.getReasons())).intersect(idp == null ? ReasonsMask.allReasons : new ReasonsMask(idp.getOnlySomeReasons()));
        } catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static java.util.Set processCRLF(java.security.cert.X509CRL r22, java.lang.Object r23, java.security.cert.X509Certificate r24, java.security.PublicKey r25, com.android.org.bouncycastle.x509.ExtendedPKIXParameters r26, java.util.List r27) throws com.android.org.bouncycastle.jce.provider.AnnotatedException {
        /*
        r13 = new com.android.org.bouncycastle.x509.X509CertStoreSelector;
        r13.<init>();
        r20 = com.android.org.bouncycastle.jce.provider.CertPathValidatorUtilities.getIssuerPrincipal(r22);	 Catch:{ IOException -> 0x0070 }
        r9 = r20.getEncoded();	 Catch:{ IOException -> 0x0070 }
        r13.setSubject(r9);	 Catch:{ IOException -> 0x0070 }
        r20 = r26.getStores();	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r20;
        r6 = com.android.org.bouncycastle.jce.provider.CertPathValidatorUtilities.findCertificates(r13, r0);	 Catch:{ AnnotatedException -> 0x007d }
        r20 = r26.getAdditionalStores();	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r20;
        r20 = com.android.org.bouncycastle.jce.provider.CertPathValidatorUtilities.findCertificates(r13, r0);	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r20;
        r6.addAll(r0);	 Catch:{ AnnotatedException -> 0x007d }
        r20 = r26.getCertStores();	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r20;
        r20 = com.android.org.bouncycastle.jce.provider.CertPathValidatorUtilities.findCertificates(r13, r0);	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r20;
        r6.addAll(r0);	 Catch:{ AnnotatedException -> 0x007d }
        r0 = r24;
        r6.add(r0);
        r3 = r6.iterator();
        r18 = new java.util.ArrayList;
        r18.<init>();
        r19 = new java.util.ArrayList;
        r19.<init>();
    L_0x004b:
        r20 = r3.hasNext();
        if (r20 == 0) goto L_0x010d;
    L_0x0051:
        r16 = r3.next();
        r16 = (java.security.cert.X509Certificate) r16;
        r0 = r16;
        r1 = r24;
        r20 = r0.equals(r1);
        if (r20 == 0) goto L_0x008a;
    L_0x0061:
        r0 = r18;
        r1 = r16;
        r0.add(r1);
        r0 = r19;
        r1 = r25;
        r0.add(r1);
        goto L_0x004b;
    L_0x0070:
        r7 = move-exception;
        r20 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r21 = "Subject criteria for certificate selector to find issuer certificate for CRL could not be set.";
        r0 = r20;
        r1 = r21;
        r0.<init>(r1, r7);
        throw r20;
    L_0x007d:
        r7 = move-exception;
        r20 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r21 = "Issuer certificate for CRL cannot be searched.";
        r0 = r20;
        r1 = r21;
        r0.<init>(r1, r7);
        throw r20;
    L_0x008a:
        r20 = "PKIX";
        r21 = "BC";
        r2 = java.security.cert.CertPathBuilder.getInstance(r20, r21);	 Catch:{ CertPathBuilderException -> 0x0173, CertPathValidatorException -> 0x00f5, Exception -> 0x0102 }
        r14 = new com.android.org.bouncycastle.x509.X509CertStoreSelector;	 Catch:{ CertPathBuilderException -> 0x0173, CertPathValidatorException -> 0x00f5, Exception -> 0x0102 }
        r14.<init>();	 Catch:{ CertPathBuilderException -> 0x0173, CertPathValidatorException -> 0x00f5, Exception -> 0x0102 }
        r0 = r16;
        r14.setCertificate(r0);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r17 = r26.clone();	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r17 = (com.android.org.bouncycastle.x509.ExtendedPKIXParameters) r17;	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r0 = r17;
        r0.setTargetCertConstraints(r14);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r12 = com.android.org.bouncycastle.x509.ExtendedPKIXBuilderParameters.getInstance(r17);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r12 = (com.android.org.bouncycastle.x509.ExtendedPKIXBuilderParameters) r12;	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r0 = r27;
        r1 = r16;
        r20 = r0.contains(r1);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        if (r20 == 0) goto L_0x00df;
    L_0x00b7:
        r20 = 0;
        r0 = r20;
        r12.setRevocationEnabled(r0);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
    L_0x00be:
        r20 = r2.build(r12);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r20 = r20.getCertPath();	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r4 = r20.getCertificates();	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r0 = r18;
        r1 = r16;
        r0.add(r1);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r20 = 0;
        r0 = r20;
        r20 = com.android.org.bouncycastle.jce.provider.CertPathValidatorUtilities.getNextWorkingKey(r4, r0);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r19.add(r20);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        r13 = r14;
        goto L_0x004b;
    L_0x00df:
        r20 = 1;
        r0 = r20;
        r12.setRevocationEnabled(r0);	 Catch:{ CertPathBuilderException -> 0x00e7, CertPathValidatorException -> 0x0170, Exception -> 0x016d }
        goto L_0x00be;
    L_0x00e7:
        r7 = move-exception;
        r13 = r14;
    L_0x00e9:
        r20 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r21 = "Internal error.";
        r0 = r20;
        r1 = r21;
        r0.<init>(r1, r7);
        throw r20;
    L_0x00f5:
        r7 = move-exception;
    L_0x00f6:
        r20 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r21 = "Public key of issuer certificate of CRL could not be retrieved.";
        r0 = r20;
        r1 = r21;
        r0.<init>(r1, r7);
        throw r20;
    L_0x0102:
        r7 = move-exception;
    L_0x0103:
        r20 = new java.lang.RuntimeException;
        r21 = r7.getMessage();
        r20.<init>(r21);
        throw r20;
    L_0x010d:
        r5 = new java.util.HashSet;
        r5.<init>();
        r11 = 0;
        r8 = 0;
    L_0x0114:
        r20 = r18.size();
        r0 = r20;
        if (r8 >= r0) goto L_0x0153;
    L_0x011c:
        r0 = r18;
        r15 = r0.get(r8);
        r15 = (java.security.cert.X509Certificate) r15;
        r10 = r15.getKeyUsage();
        if (r10 == 0) goto L_0x0147;
    L_0x012a:
        r0 = r10.length;
        r20 = r0;
        r21 = 7;
        r0 = r20;
        r1 = r21;
        if (r0 < r1) goto L_0x013b;
    L_0x0135:
        r20 = 6;
        r20 = r10[r20];
        if (r20 != 0) goto L_0x0147;
    L_0x013b:
        r11 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r20 = "Issuer certificate key usage extension does not permit CRL signing.";
        r0 = r20;
        r11.<init>(r0);
    L_0x0144:
        r8 = r8 + 1;
        goto L_0x0114;
    L_0x0147:
        r0 = r19;
        r20 = r0.get(r8);
        r0 = r20;
        r5.add(r0);
        goto L_0x0144;
    L_0x0153:
        r20 = r5.isEmpty();
        if (r20 == 0) goto L_0x0163;
    L_0x0159:
        if (r11 != 0) goto L_0x0163;
    L_0x015b:
        r20 = new com.android.org.bouncycastle.jce.provider.AnnotatedException;
        r21 = "Cannot find a valid issuer certificate.";
        r20.<init>(r21);
        throw r20;
    L_0x0163:
        r20 = r5.isEmpty();
        if (r20 == 0) goto L_0x016c;
    L_0x0169:
        if (r11 == 0) goto L_0x016c;
    L_0x016b:
        throw r11;
    L_0x016c:
        return r5;
    L_0x016d:
        r7 = move-exception;
        r13 = r14;
        goto L_0x0103;
    L_0x0170:
        r7 = move-exception;
        r13 = r14;
        goto L_0x00f6;
    L_0x0173:
        r7 = move-exception;
        goto L_0x00e9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.processCRLF(java.security.cert.X509CRL, java.lang.Object, java.security.cert.X509Certificate, java.security.PublicKey, com.android.org.bouncycastle.x509.ExtendedPKIXParameters, java.util.List):java.util.Set");
    }

    protected static PublicKey processCRLG(X509CRL crl, Set keys) throws AnnotatedException {
        Exception lastException = null;
        for (PublicKey key : keys) {
            try {
                crl.verify(key);
                return key;
            } catch (Exception e) {
                lastException = e;
            }
        }
        throw new AnnotatedException("Cannot verify CRL.", lastException);
    }

    protected static X509CRL processCRLH(Set deltacrls, PublicKey key) throws AnnotatedException {
        Exception lastException = null;
        for (X509CRL crl : deltacrls) {
            try {
                crl.verify(key);
                return crl;
            } catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException == null) {
            return null;
        }
        throw new AnnotatedException("Cannot verify delta CRL.", lastException);
    }

    protected static Set processCRLA1i(Date currentDate, ExtendedPKIXParameters paramsPKIX, X509Certificate cert, X509CRL crl) throws AnnotatedException {
        Set set = new HashSet();
        if (paramsPKIX.isUseDeltasEnabled()) {
            try {
                CRLDistPoint freshestCRL = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, FRESHEST_CRL));
                if (freshestCRL == null) {
                    try {
                        freshestCRL = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, FRESHEST_CRL));
                    } catch (AnnotatedException e) {
                        throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", e);
                    }
                }
                if (freshestCRL != null) {
                    try {
                        CertPathValidatorUtilities.addAdditionalStoresFromCRLDistributionPoint(freshestCRL, paramsPKIX);
                        try {
                            set.addAll(CertPathValidatorUtilities.getDeltaCRLs(currentDate, paramsPKIX, crl));
                        } catch (AnnotatedException e2) {
                            throw new AnnotatedException("Exception obtaining delta CRLs.", e2);
                        }
                    } catch (AnnotatedException e22) {
                        throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", e22);
                    }
                }
            } catch (AnnotatedException e222) {
                throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", e222);
            }
        }
        return set;
    }

    protected static Set[] processCRLA1ii(Date currentDate, ExtendedPKIXParameters paramsPKIX, X509Certificate cert, X509CRL crl) throws AnnotatedException {
        Set deltaSet = new HashSet();
        X509CRLStoreSelector crlselect = new X509CRLStoreSelector();
        crlselect.setCertificateChecking(cert);
        try {
            crlselect.addIssuerName(crl.getIssuerX500Principal().getEncoded());
            crlselect.setCompleteCRLEnabled(true);
            Set completeSet = CRL_UTIL.findCRLs(crlselect, paramsPKIX, currentDate);
            if (paramsPKIX.isUseDeltasEnabled()) {
                try {
                    deltaSet.addAll(CertPathValidatorUtilities.getDeltaCRLs(currentDate, paramsPKIX, crl));
                } catch (AnnotatedException e) {
                    throw new AnnotatedException("Exception obtaining delta CRLs.", e);
                }
            }
            return new Set[]{completeSet, deltaSet};
        } catch (IOException e2) {
            throw new AnnotatedException("Cannot extract issuer from CRL." + e2, e2);
        }
    }

    protected static void processCRLC(X509CRL deltaCRL, X509CRL completeCRL, ExtendedPKIXParameters pkixParams) throws AnnotatedException {
        if (deltaCRL != null) {
            try {
                IssuingDistributionPoint completeidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(completeCRL, ISSUING_DISTRIBUTION_POINT));
                if (!pkixParams.isUseDeltasEnabled()) {
                    return;
                }
                if (deltaCRL.getIssuerX500Principal().equals(completeCRL.getIssuerX500Principal())) {
                    try {
                        IssuingDistributionPoint deltaidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(deltaCRL, ISSUING_DISTRIBUTION_POINT));
                        boolean match = false;
                        if (completeidp == null) {
                            if (deltaidp == null) {
                                match = true;
                            }
                        } else if (completeidp.equals(deltaidp)) {
                            match = true;
                        }
                        if (match) {
                            try {
                                ASN1Primitive completeKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(completeCRL, AUTHORITY_KEY_IDENTIFIER);
                                try {
                                    ASN1Primitive deltaKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(deltaCRL, AUTHORITY_KEY_IDENTIFIER);
                                    if (completeKeyIdentifier == null) {
                                        throw new AnnotatedException("CRL authority key identifier is null.");
                                    } else if (deltaKeyIdentifier == null) {
                                        throw new AnnotatedException("Delta CRL authority key identifier is null.");
                                    } else if (!completeKeyIdentifier.equals(deltaKeyIdentifier)) {
                                        throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
                                    } else {
                                        return;
                                    }
                                } catch (AnnotatedException e) {
                                    throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", e);
                                }
                            } catch (AnnotatedException e2) {
                                throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e2);
                            }
                        }
                        throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
                    } catch (Exception e3) {
                        throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e3);
                    }
                }
                throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
            } catch (Exception e32) {
                throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e32);
            }
        }
    }

    protected static void processCRLI(Date validDate, X509CRL deltacrl, Object cert, CertStatus certStatus, ExtendedPKIXParameters pkixParams) throws AnnotatedException {
        if (pkixParams.isUseDeltasEnabled() && deltacrl != null) {
            CertPathValidatorUtilities.getCertStatus(validDate, deltacrl, cert, certStatus);
        }
    }

    protected static void processCRLJ(Date validDate, X509CRL completecrl, Object cert, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(validDate, completecrl, cert, certStatus);
        }
    }

    protected static PKIXPolicyNode prepareCertB(CertPath certPath, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, int policyMapping) throws CertPathValidatorException {
        Enumeration e;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int i = certs.size() - index;
        try {
            ASN1Sequence pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_MAPPINGS));
            PKIXPolicyNode _validPolicyTree = validPolicyTree;
            if (pm != null) {
                String id_p;
                ASN1Sequence mappings = pm;
                Map m_idp = new HashMap();
                Set<String> s_idp = new HashSet();
                for (int j = 0; j < mappings.size(); j++) {
                    ASN1Sequence mapping = (ASN1Sequence) mappings.getObjectAt(j);
                    id_p = ((DERObjectIdentifier) mapping.getObjectAt(0)).getId();
                    String sd_p = ((DERObjectIdentifier) mapping.getObjectAt(1)).getId();
                    if (m_idp.containsKey(id_p)) {
                        ((Set) m_idp.get(id_p)).add(sd_p);
                    } else {
                        Set tmp = new HashSet();
                        tmp.add(sd_p);
                        m_idp.put(id_p, tmp);
                        s_idp.add(id_p);
                    }
                }
                for (String id_p2 : s_idp) {
                    Iterator nodes_i;
                    PKIXPolicyNode node;
                    if (policyMapping > 0) {
                        boolean idp_found = false;
                        for (PKIXPolicyNode node2 : policyNodes[i]) {
                            if (node2.getValidPolicy().equals(id_p2)) {
                                idp_found = true;
                                node2.expectedPolicies = (Set) m_idp.get(id_p2);
                                break;
                            }
                        }
                        if (idp_found) {
                            continue;
                        } else {
                            for (PKIXPolicyNode node22 : policyNodes[i]) {
                                if (ANY_POLICY.equals(node22.getValidPolicy())) {
                                    Set pq = null;
                                    try {
                                        e = ((ASN1Sequence) CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES)).getObjects();
                                        while (e.hasMoreElements()) {
                                            try {
                                                PolicyInformation pinfo = PolicyInformation.getInstance(e.nextElement());
                                                if (ANY_POLICY.equals(pinfo.getPolicyIdentifier().getId())) {
                                                    try {
                                                        pq = CertPathValidatorUtilities.getQualifierSet(pinfo.getPolicyQualifiers());
                                                        break;
                                                    } catch (CertPathValidatorException ex) {
                                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", ex, certPath, index);
                                                    }
                                                }
                                            } catch (Exception ex2) {
                                                throw new CertPathValidatorException("Policy information could not be decoded.", ex2, certPath, index);
                                            }
                                        }
                                        boolean ci = false;
                                        if (cert.getCriticalExtensionOIDs() != null) {
                                            ci = cert.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                                        }
                                        PKIXPolicyNode p_node = (PKIXPolicyNode) node22.getParent();
                                        if (ANY_POLICY.equals(p_node.getValidPolicy())) {
                                            PKIXPolicyNode c_node = new PKIXPolicyNode(new ArrayList(), i, (Set) m_idp.get(id_p2), p_node, pq, id_p2, ci);
                                            p_node.addChild(c_node);
                                            policyNodes[i].add(c_node);
                                        }
                                    } catch (Enumeration e2) {
                                        throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", e2, certPath, index);
                                    }
                                }
                            }
                            continue;
                        }
                    } else if (policyMapping <= 0) {
                        nodes_i = policyNodes[i].iterator();
                        while (nodes_i.hasNext()) {
                            node22 = (PKIXPolicyNode) nodes_i.next();
                            if (node22.getValidPolicy().equals(id_p2)) {
                                ((PKIXPolicyNode) node22.getParent()).removeChild(node22);
                                nodes_i.remove();
                                for (int k = i - 1; k >= 0; k--) {
                                    List nodes = policyNodes[k];
                                    for (int l = 0; l < nodes.size(); l++) {
                                        PKIXPolicyNode node23 = (PKIXPolicyNode) nodes.get(l);
                                        if (!node23.hasChildren()) {
                                            _validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node23);
                                            if (_validPolicyTree == null) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return _validPolicyTree;
        } catch (AnnotatedException ex3) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex3, certPath, index);
        }
    }

    protected static void prepareNextCertA(CertPath certPath, int index) throws CertPathValidatorException {
        try {
            ASN1Sequence pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), POLICY_MAPPINGS));
            if (pm != null) {
                ASN1Sequence mappings = pm;
                int j = 0;
                while (j < mappings.size()) {
                    try {
                        ASN1Sequence mapping = ASN1Sequence.getInstance(mappings.getObjectAt(j));
                        DERObjectIdentifier issuerDomainPolicy = DERObjectIdentifier.getInstance(mapping.getObjectAt(0));
                        DERObjectIdentifier subjectDomainPolicy = DERObjectIdentifier.getInstance(mapping.getObjectAt(1));
                        if (ANY_POLICY.equals(issuerDomainPolicy.getId())) {
                            throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, index);
                        } else if (ANY_POLICY.equals(subjectDomainPolicy.getId())) {
                            throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy,", null, certPath, index);
                        } else {
                            j++;
                        }
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", e, certPath, index);
                    }
                }
            }
        } catch (AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex, certPath, index);
        }
    }

    protected static void processCertF(CertPath certPath, int index, PKIXPolicyNode validPolicyTree, int explicitPolicy) throws CertPathValidatorException {
        if (explicitPolicy <= 0 && validPolicyTree == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, index);
        }
    }

    protected static PKIXPolicyNode processCertE(CertPath certPath, int index, PKIXPolicyNode validPolicyTree) throws CertPathValidatorException {
        try {
            if (ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), CERTIFICATE_POLICIES)) == null) {
                return null;
            }
            return validPolicyTree;
        } catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e, certPath, index);
        }
    }

    protected static void processCertBC(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator) throws CertPathValidatorException {
        Enumeration e;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int n = certs.size();
        int i = n - index;
        if (!CertPathValidatorUtilities.isSelfIssued(cert) || i >= n) {
            try {
                ASN1Sequence dns = ASN1Sequence.getInstance(new ASN1InputStream(CertPathValidatorUtilities.getSubjectPrincipal(cert).getEncoded()).readObject());
                try {
                    nameConstraintValidator.checkPermittedDN(dns);
                    nameConstraintValidator.checkExcludedDN(dns);
                    try {
                        GeneralNames altName = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, SUBJECT_ALTERNATIVE_NAME));
                        e = new X509Name(dns).getValues(X509Name.EmailAddress).elements();
                        while (e.hasMoreElements()) {
                            GeneralName emailAsGeneralName = new GeneralName(1, (String) e.nextElement());
                            try {
                                nameConstraintValidator.checkPermitted(emailAsGeneralName);
                                nameConstraintValidator.checkExcluded(emailAsGeneralName);
                            } catch (PKIXNameConstraintValidatorException ex) {
                                throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", ex, certPath, index);
                            }
                        }
                        if (altName != null) {
                            try {
                                GeneralName[] genNames = altName.getNames();
                                int j = 0;
                                while (j < genNames.length) {
                                    try {
                                        nameConstraintValidator.checkPermitted(genNames[j]);
                                        nameConstraintValidator.checkExcluded(genNames[j]);
                                        j++;
                                    } catch (PKIXNameConstraintValidatorException e2) {
                                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", e2, certPath, index);
                                    }
                                }
                            } catch (Exception e3) {
                                throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", e3, certPath, index);
                            }
                        }
                    } catch (Enumeration e4) {
                        throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", e4, certPath, index);
                    }
                } catch (PKIXNameConstraintValidatorException e22) {
                    throw new CertPathValidatorException("Subtree check for certificate subject failed.", e22, certPath, index);
                }
            } catch (Exception e32) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", e32, certPath, index);
            }
        }
    }

    protected static PKIXPolicyNode processCertD(CertPath certPath, int index, Set acceptablePolicies, PKIXPolicyNode validPolicyTree, List[] policyNodes, int inhibitAnyPolicy) throws CertPathValidatorException {
        Enumeration e;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int n = certs.size();
        int i = n - index;
        try {
            ASN1Sequence certPolicies = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
            if (certPolicies == null || validPolicyTree == null) {
                return null;
            }
            PolicyInformation pInfo;
            Set _apq;
            List _nodes;
            int k;
            PKIXPolicyNode _node;
            String _policy;
            boolean _found;
            Iterator _childrenIter;
            Set _newChildExpectedPolicies;
            PKIXPolicyNode _newChild;
            PKIXPolicyNode _validPolicyTree;
            int j;
            List nodes;
            PKIXPolicyNode node;
            Set criticalExtensionOids;
            boolean critical;
            e = certPolicies.getObjects();
            Set pols = new HashSet();
            while (e.hasMoreElements()) {
                pInfo = PolicyInformation.getInstance(e.nextElement());
                DERObjectIdentifier pOid = pInfo.getPolicyIdentifier();
                pols.add(pOid.getId());
                if (!ANY_POLICY.equals(pOid.getId())) {
                    try {
                        Set pq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                        if (!CertPathValidatorUtilities.processCertD1i(i, policyNodes, pOid, pq)) {
                            CertPathValidatorUtilities.processCertD1ii(i, policyNodes, pOid, pq);
                        }
                    } catch (Throwable ex) {
                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", ex, certPath, index);
                    }
                }
            }
            if (!acceptablePolicies.isEmpty()) {
                if (!acceptablePolicies.contains(ANY_POLICY)) {
                    Set t1 = new HashSet();
                    for (Object o : acceptablePolicies) {
                        if (pols.contains(o)) {
                            t1.add(o);
                        }
                    }
                    acceptablePolicies.clear();
                    acceptablePolicies.addAll(t1);
                    if (inhibitAnyPolicy > 0 || (i < n && CertPathValidatorUtilities.isSelfIssued(cert))) {
                        e = certPolicies.getObjects();
                        while (e.hasMoreElements()) {
                            pInfo = PolicyInformation.getInstance(e.nextElement());
                            if (ANY_POLICY.equals(pInfo.getPolicyIdentifier().getId())) {
                                _apq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                                _nodes = policyNodes[i - 1];
                                for (k = 0; k < _nodes.size(); k++) {
                                    _node = (PKIXPolicyNode) _nodes.get(k);
                                    for (String _tmp : _node.getExpectedPolicies()) {
                                        if (_tmp instanceof String) {
                                            _policy = _tmp;
                                        } else if (_tmp instanceof DERObjectIdentifier) {
                                            _policy = ((DERObjectIdentifier) _tmp).getId();
                                        }
                                        _found = false;
                                        _childrenIter = _node.getChildren();
                                        while (_childrenIter.hasNext()) {
                                            if (_policy.equals(((PKIXPolicyNode) _childrenIter.next()).getValidPolicy())) {
                                                _found = true;
                                            }
                                        }
                                        if (!_found) {
                                            _newChildExpectedPolicies = new HashSet();
                                            _newChildExpectedPolicies.add(_policy);
                                            _newChild = new PKIXPolicyNode(new ArrayList(), i, _newChildExpectedPolicies, _node, _apq, _policy, false);
                                            _node.addChild(_newChild);
                                            policyNodes[i].add(_newChild);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    _validPolicyTree = validPolicyTree;
                    for (j = i - 1; j >= 0; j--) {
                        nodes = policyNodes[j];
                        for (k = 0; k < nodes.size(); k++) {
                            node = (PKIXPolicyNode) nodes.get(k);
                            if (node.hasChildren()) {
                                _validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node);
                                if (_validPolicyTree != null) {
                                    break;
                                }
                            }
                        }
                    }
                    criticalExtensionOids = cert.getCriticalExtensionOIDs();
                    if (criticalExtensionOids != null) {
                        return _validPolicyTree;
                    }
                    critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
                    nodes = policyNodes[i];
                    for (j = 0; j < nodes.size(); j++) {
                        ((PKIXPolicyNode) nodes.get(j)).setCritical(critical);
                    }
                    return _validPolicyTree;
                }
            }
            acceptablePolicies.clear();
            acceptablePolicies.addAll(pols);
            e = certPolicies.getObjects();
            while (e.hasMoreElements()) {
                pInfo = PolicyInformation.getInstance(e.nextElement());
                if (ANY_POLICY.equals(pInfo.getPolicyIdentifier().getId())) {
                    _apq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                    _nodes = policyNodes[i - 1];
                    for (k = 0; k < _nodes.size(); k++) {
                        _node = (PKIXPolicyNode) _nodes.get(k);
                        for (String _tmp2 : _node.getExpectedPolicies()) {
                            if (_tmp2 instanceof String) {
                                _policy = _tmp2;
                            } else if (_tmp2 instanceof DERObjectIdentifier) {
                                _policy = ((DERObjectIdentifier) _tmp2).getId();
                            }
                            _found = false;
                            _childrenIter = _node.getChildren();
                            while (_childrenIter.hasNext()) {
                                if (_policy.equals(((PKIXPolicyNode) _childrenIter.next()).getValidPolicy())) {
                                    _found = true;
                                }
                            }
                            if (!_found) {
                                _newChildExpectedPolicies = new HashSet();
                                _newChildExpectedPolicies.add(_policy);
                                _newChild = new PKIXPolicyNode(new ArrayList(), i, _newChildExpectedPolicies, _node, _apq, _policy, false);
                                _node.addChild(_newChild);
                                policyNodes[i].add(_newChild);
                            }
                        }
                    }
                    _validPolicyTree = validPolicyTree;
                    for (j = i - 1; j >= 0; j--) {
                        nodes = policyNodes[j];
                        for (k = 0; k < nodes.size(); k++) {
                            node = (PKIXPolicyNode) nodes.get(k);
                            if (node.hasChildren()) {
                                _validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node);
                                if (_validPolicyTree != null) {
                                    break;
                                }
                            }
                        }
                    }
                    criticalExtensionOids = cert.getCriticalExtensionOIDs();
                    if (criticalExtensionOids != null) {
                        return _validPolicyTree;
                    }
                    critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
                    nodes = policyNodes[i];
                    for (j = 0; j < nodes.size(); j++) {
                        ((PKIXPolicyNode) nodes.get(j)).setCritical(critical);
                    }
                    return _validPolicyTree;
                }
            }
            _validPolicyTree = validPolicyTree;
            for (j = i - 1; j >= 0; j--) {
                nodes = policyNodes[j];
                for (k = 0; k < nodes.size(); k++) {
                    node = (PKIXPolicyNode) nodes.get(k);
                    if (node.hasChildren()) {
                        _validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node);
                        if (_validPolicyTree != null) {
                            break;
                        }
                    }
                }
            }
            criticalExtensionOids = cert.getCriticalExtensionOIDs();
            if (criticalExtensionOids != null) {
                return _validPolicyTree;
            }
            critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
            nodes = policyNodes[i];
            for (j = 0; j < nodes.size(); j++) {
                ((PKIXPolicyNode) nodes.get(j)).setCritical(critical);
            }
            return _validPolicyTree;
        } catch (Enumeration e2) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e2, certPath, index);
        }
    }

    protected static void processCertA(CertPath certPath, ExtendedPKIXParameters paramsPKIX, int index, PublicKey workingPublicKey, boolean verificationAlreadyPerformed, X500Principal workingIssuerName, X509Certificate sign) throws ExtCertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!verificationAlreadyPerformed) {
            try {
                CertPathValidatorUtilities.verifyX509Certificate(cert, workingPublicKey, paramsPKIX.getSigProvider());
            } catch (GeneralSecurityException e) {
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", e, certPath, index);
            }
        }
        try {
            cert.checkValidity(CertPathValidatorUtilities.getValidCertDateFromValidityModel(paramsPKIX, certPath, index));
            if (paramsPKIX.isRevocationEnabled()) {
                try {
                    checkCRLs(paramsPKIX, cert, CertPathValidatorUtilities.getValidCertDateFromValidityModel(paramsPKIX, certPath, index), sign, workingPublicKey, certs);
                } catch (Throwable e2) {
                    Throwable cause = e2;
                    if (e2.getCause() != null) {
                        cause = e2.getCause();
                    }
                    throw new ExtCertPathValidatorException(e2.getMessage(), cause, certPath, index);
                }
            }
            if (!CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert).equals(workingIssuerName)) {
                throw new ExtCertPathValidatorException("IssuerName(" + CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert) + ") does not match SubjectName(" + workingIssuerName + ") of signing certificate.", null, certPath, index);
            }
        } catch (CertificateExpiredException e3) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + e3.getMessage(), e3, certPath, index);
        } catch (CertificateNotYetValidException e4) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + e4.getMessage(), e4, certPath, index);
        } catch (AnnotatedException e5) {
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", e5, certPath, index);
        }
    }

    protected static int prepareNextCertI1(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), POLICY_CONSTRAINTS));
            if (pc != null) {
                Enumeration policyConstraints = pc.getObjects();
                while (policyConstraints.hasMoreElements()) {
                    try {
                        ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                        if (constraint.getTagNo() == 0) {
                            int tmpInt = DERInteger.getInstance(constraint, false).getValue().intValue();
                            if (tmpInt < explicitPolicy) {
                                return tmpInt;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, index);
                    }
                }
            }
            return explicitPolicy;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, index);
        }
    }

    protected static int prepareNextCertI2(CertPath certPath, int index, int policyMapping) throws CertPathValidatorException {
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), POLICY_CONSTRAINTS));
            if (pc != null) {
                Enumeration policyConstraints = pc.getObjects();
                while (policyConstraints.hasMoreElements()) {
                    try {
                        ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                        if (constraint.getTagNo() == 1) {
                            int tmpInt = DERInteger.getInstance(constraint, false).getValue().intValue();
                            if (tmpInt < policyMapping) {
                                return tmpInt;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, index);
                    }
                }
            }
            return policyMapping;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, index);
        }
    }

    protected static void prepareNextCertG(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator) throws CertPathValidatorException {
        NameConstraints nc = null;
        try {
            ASN1Sequence ncSeq = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), NAME_CONSTRAINTS));
            if (ncSeq != null) {
                nc = NameConstraints.getInstance(ncSeq);
            }
            if (nc != null) {
                GeneralSubtree[] permitted = nc.getPermittedSubtrees();
                if (permitted != null) {
                    try {
                        nameConstraintValidator.intersectPermittedSubtree(permitted);
                    } catch (Exception ex) {
                        throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", ex, certPath, index);
                    }
                }
                GeneralSubtree[] excluded = nc.getExcludedSubtrees();
                if (excluded != null) {
                    int i = 0;
                    while (i != excluded.length) {
                        try {
                            nameConstraintValidator.addExcludedSubtree(excluded[i]);
                            i++;
                        } catch (Exception ex2) {
                            throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", ex2, certPath, index);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", e, certPath, index);
        }
    }

    private static void checkCRL(DistributionPoint dp, ExtendedPKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, CertStatus certStatus, ReasonsMask reasonMask, List certPathCerts) throws AnnotatedException {
        Date currentDate = new Date(System.currentTimeMillis());
        if (validDate.getTime() > currentDate.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        boolean validCrlFound = false;
        AnnotatedException lastException = null;
        Iterator crl_iter = CertPathValidatorUtilities.getCompleteCRLs(dp, cert, currentDate, paramsPKIX).iterator();
        while (crl_iter.hasNext() && certStatus.getCertStatus() == 11 && !reasonMask.isAllReasons()) {
            try {
                X509CRL crl = (X509CRL) crl_iter.next();
                ReasonsMask interimReasonsMask = processCRLD(crl, dp);
                if (interimReasonsMask.hasNewReasons(reasonMask)) {
                    PublicKey key = processCRLG(crl, processCRLF(crl, cert, defaultCRLSignCert, defaultCRLSignKey, paramsPKIX, certPathCerts));
                    X509CRL deltaCRL = null;
                    if (paramsPKIX.isUseDeltasEnabled()) {
                        deltaCRL = processCRLH(CertPathValidatorUtilities.getDeltaCRLs(currentDate, paramsPKIX, crl), key);
                    }
                    if (paramsPKIX.getValidityModel() == 1 || cert.getNotAfter().getTime() >= crl.getThisUpdate().getTime()) {
                        Set criticalExtensions;
                        processCRLB1(dp, cert, crl);
                        processCRLB2(dp, cert, crl);
                        processCRLC(deltaCRL, crl, paramsPKIX);
                        processCRLI(validDate, deltaCRL, cert, certStatus, paramsPKIX);
                        processCRLJ(validDate, crl, cert, certStatus);
                        if (certStatus.getCertStatus() == 8) {
                            certStatus.setCertStatus(11);
                        }
                        reasonMask.addReasons(interimReasonsMask);
                        Set criticalExtensions2 = crl.getCriticalExtensionOIDs();
                        if (criticalExtensions2 != null) {
                            criticalExtensions = new HashSet(criticalExtensions2);
                            criticalExtensions.remove(X509Extensions.IssuingDistributionPoint.getId());
                            criticalExtensions.remove(X509Extensions.DeltaCRLIndicator.getId());
                            if (criticalExtensions.isEmpty()) {
                                criticalExtensions2 = criticalExtensions;
                            } else {
                                throw new AnnotatedException("CRL contains unsupported critical extensions.");
                            }
                        }
                        if (deltaCRL != null) {
                            criticalExtensions2 = deltaCRL.getCriticalExtensionOIDs();
                            if (criticalExtensions2 != null) {
                                criticalExtensions = new HashSet(criticalExtensions2);
                                criticalExtensions.remove(X509Extensions.IssuingDistributionPoint.getId());
                                criticalExtensions.remove(X509Extensions.DeltaCRLIndicator.getId());
                                if (criticalExtensions.isEmpty()) {
                                    criticalExtensions2 = criticalExtensions;
                                } else {
                                    throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                                }
                            }
                        }
                        validCrlFound = true;
                    } else {
                        throw new AnnotatedException("No valid CRL for current time found.");
                    }
                }
                continue;
            } catch (AnnotatedException e) {
                lastException = e;
            }
        }
        if (!validCrlFound) {
            throw lastException;
        }
    }

    protected static void checkCRLs(ExtendedPKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate sign, PublicKey workingPublicKey, List certPathCerts) throws AnnotatedException {
        AnnotatedException lastException = null;
        try {
            CRLDistPoint crldp = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CRL_DISTRIBUTION_POINTS));
            try {
                CertPathValidatorUtilities.addAdditionalStoresFromCRLDistributionPoint(crldp, paramsPKIX);
                CertStatus certStatus = new CertStatus();
                ReasonsMask reasonsMask = new ReasonsMask();
                boolean z = false;
                if (crldp != null) {
                    try {
                        DistributionPoint[] dps = crldp.getDistributionPoints();
                        if (dps != null) {
                            for (int i = 0; i < dps.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); i++) {
                                try {
                                    checkCRL(dps[i], (ExtendedPKIXParameters) paramsPKIX.clone(), cert, validDate, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts);
                                    z = true;
                                } catch (AnnotatedException e) {
                                    lastException = e;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        throw new AnnotatedException("Distribution points could not be read.", e2);
                    }
                }
                if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                    try {
                        checkCRL(new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, new ASN1InputStream(CertPathValidatorUtilities.getEncodedIssuerPrincipal(cert).getEncoded()).readObject()))), null, null), (ExtendedPKIXParameters) paramsPKIX.clone(), cert, validDate, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts);
                        z = true;
                    } catch (Exception e22) {
                        throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", e22);
                    } catch (AnnotatedException e3) {
                        lastException = e3;
                    }
                }
                if (z) {
                    if (certStatus.getCertStatus() != 11) {
                        throw new AnnotatedException(("Certificate revocation after " + certStatus.getRevocationDate()) + ", reason: " + crlReasons[certStatus.getCertStatus()]);
                    }
                    if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
                        certStatus.setCertStatus(12);
                    }
                    if (certStatus.getCertStatus() == 12) {
                        throw new AnnotatedException("Certificate status could not be determined.");
                    }
                } else if (lastException instanceof AnnotatedException) {
                    throw lastException;
                } else {
                    throw new AnnotatedException("No valid CRL found.", lastException);
                }
            } catch (AnnotatedException e32) {
                throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", e32);
            }
        } catch (Exception e222) {
            throw new AnnotatedException("CRL distribution point extension could not be read.", e222);
        }
    }

    protected static int prepareNextCertJ(CertPath certPath, int index, int inhibitAnyPolicy) throws CertPathValidatorException {
        try {
            DERInteger iap = DERInteger.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), INHIBIT_ANY_POLICY));
            if (iap != null) {
                int _inhibitAnyPolicy = iap.getValue().intValue();
                if (_inhibitAnyPolicy < inhibitAnyPolicy) {
                    return _inhibitAnyPolicy;
                }
            }
            return inhibitAnyPolicy;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", e, certPath, index);
        }
    }

    protected static void prepareNextCertK(CertPath certPath, int index) throws CertPathValidatorException {
        try {
            BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), BASIC_CONSTRAINTS));
            if (bc == null) {
                throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints");
            } else if (!bc.isCA()) {
                throw new CertPathValidatorException("Not a CA certificate");
            }
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, index);
        }
    }

    protected static int prepareNextCertL(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(index))) {
            return maxPathLength;
        }
        if (maxPathLength > 0) {
            return maxPathLength - 1;
        }
        throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, index);
    }

    protected static int prepareNextCertM(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        try {
            BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), BASIC_CONSTRAINTS));
            if (bc != null) {
                BigInteger _pathLengthConstraint = bc.getPathLenConstraint();
                if (_pathLengthConstraint != null) {
                    int _plc = _pathLengthConstraint.intValue();
                    if (_plc < maxPathLength) {
                        return _plc;
                    }
                }
            }
            return maxPathLength;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, index);
        }
    }

    protected static void prepareNextCertN(CertPath certPath, int index) throws CertPathValidatorException {
        boolean[] _usage = ((X509Certificate) certPath.getCertificates().get(index)).getKeyUsage();
        if (_usage != null && !_usage[KEY_CERT_SIGN]) {
            throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, index);
        }
    }

    protected static void prepareNextCertO(CertPath certPath, int index, Set criticalExtensions, List pathCheckers) throws CertPathValidatorException {
        X509Certificate cert = (X509Certificate) certPath.getCertificates().get(index);
        for (PKIXCertPathChecker check : pathCheckers) {
            try {
                check.check(cert, criticalExtensions);
            } catch (CertPathValidatorException e) {
                throw new CertPathValidatorException(e.getMessage(), e.getCause(), certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    protected static int prepareNextCertH1(CertPath certPath, int index, int explicitPolicy) {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(index)) || explicitPolicy == 0) {
            return explicitPolicy;
        }
        return explicitPolicy - 1;
    }

    protected static int prepareNextCertH2(CertPath certPath, int index, int policyMapping) {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(index)) || policyMapping == 0) {
            return policyMapping;
        }
        return policyMapping - 1;
    }

    protected static int prepareNextCertH3(CertPath certPath, int index, int inhibitAnyPolicy) {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(index)) || inhibitAnyPolicy == 0) {
            return inhibitAnyPolicy;
        }
        return inhibitAnyPolicy - 1;
    }

    protected static int wrapupCertA(int explicitPolicy, X509Certificate cert) {
        if (CertPathValidatorUtilities.isSelfIssued(cert) || explicitPolicy == 0) {
            return explicitPolicy;
        }
        return explicitPolicy - 1;
    }

    protected static int wrapupCertB(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(index), POLICY_CONSTRAINTS));
            if (pc == null) {
                return explicitPolicy;
            }
            Enumeration policyConstraints = pc.getObjects();
            while (policyConstraints.hasMoreElements()) {
                ASN1TaggedObject constraint = (ASN1TaggedObject) policyConstraints.nextElement();
                switch (constraint.getTagNo()) {
                    case ECCurve.COORD_AFFINE /*0*/:
                        try {
                            if (DERInteger.getInstance(constraint, false).getValue().intValue() != 0) {
                                break;
                            }
                            return 0;
                        } catch (Exception e) {
                            throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", e, certPath, index);
                        }
                    default:
                        break;
                }
            }
            return explicitPolicy;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", e2, certPath, index);
        }
    }

    protected static void wrapupCertF(CertPath certPath, int index, List pathCheckers, Set criticalExtensions) throws CertPathValidatorException {
        X509Certificate cert = (X509Certificate) certPath.getCertificates().get(index);
        for (PKIXCertPathChecker check : pathCheckers) {
            try {
                check.check(cert, criticalExtensions);
            } catch (CertPathValidatorException e) {
                throw new ExtCertPathValidatorException("Additional certificate path checker failed.", e, certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    protected static PKIXPolicyNode wrapupCertG(CertPath certPath, ExtendedPKIXParameters paramsPKIX, Set userInitialPolicySet, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, Set acceptablePolicies) throws CertPathValidatorException {
        int n = certPath.getCertificates().size();
        if (validPolicyTree == null) {
            if (!paramsPKIX.isExplicitPolicyRequired()) {
                return null;
            }
            throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
        } else if (CertPathValidatorUtilities.isAnyPolicy(userInitialPolicySet)) {
            if (paramsPKIX.isExplicitPolicyRequired()) {
                if (acceptablePolicies.isEmpty()) {
                    throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
                }
                _validPolicyNodeSet = new HashSet();
                j = 0;
                while (true) {
                    r18 = policyNodes.length;
                    if (j >= r0) {
                        break;
                    }
                    _nodeDepth = policyNodes[j];
                    for (k = 0; k < _nodeDepth.size(); k++) {
                        _node = (PKIXPolicyNode) _nodeDepth.get(k);
                        if (ANY_POLICY.equals(_node.getValidPolicy())) {
                            _iter = _node.getChildren();
                            while (_iter.hasNext()) {
                                _validPolicyNodeSet.add(_iter.next());
                            }
                        }
                    }
                    j++;
                }
                for (PKIXPolicyNode _node : _validPolicyNodeSet) {
                    if (acceptablePolicies.contains(_node.getValidPolicy())) {
                    }
                }
                if (validPolicyTree != null) {
                    for (j = n - 1; j >= 0; j--) {
                        nodes = policyNodes[j];
                        for (k = 0; k < nodes.size(); k++) {
                            node = (PKIXPolicyNode) nodes.get(k);
                            if (!node.hasChildren()) {
                                validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, node);
                            }
                        }
                    }
                }
            }
            return validPolicyTree;
        } else {
            _validPolicyNodeSet = new HashSet();
            j = 0;
            while (true) {
                r18 = policyNodes.length;
                if (j >= r0) {
                    break;
                }
                _nodeDepth = policyNodes[j];
                for (k = 0; k < _nodeDepth.size(); k++) {
                    _node = (PKIXPolicyNode) _nodeDepth.get(k);
                    if (ANY_POLICY.equals(_node.getValidPolicy())) {
                        _iter = _node.getChildren();
                        while (_iter.hasNext()) {
                            PKIXPolicyNode _c_node = (PKIXPolicyNode) _iter.next();
                            if (!ANY_POLICY.equals(_c_node.getValidPolicy())) {
                                _validPolicyNodeSet.add(_c_node);
                            }
                        }
                    }
                }
                j++;
            }
            for (PKIXPolicyNode _node2 : _validPolicyNodeSet) {
                if (!userInitialPolicySet.contains(_node2.getValidPolicy())) {
                    validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, _node2);
                }
            }
            if (validPolicyTree != null) {
                for (j = n - 1; j >= 0; j--) {
                    nodes = policyNodes[j];
                    for (k = 0; k < nodes.size(); k++) {
                        node = (PKIXPolicyNode) nodes.get(k);
                        if (!node.hasChildren()) {
                            validPolicyTree = CertPathValidatorUtilities.removePolicyNode(validPolicyTree, policyNodes, node);
                        }
                    }
                }
            }
            return validPolicyTree;
        }
    }
}
