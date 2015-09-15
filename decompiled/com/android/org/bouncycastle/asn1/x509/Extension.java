package com.android.org.bouncycastle.asn1.x509;

import com.android.org.bouncycastle.asn1.ASN1Boolean;
import com.android.org.bouncycastle.asn1.ASN1Encodable;
import com.android.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.org.bouncycastle.asn1.ASN1Object;
import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.org.bouncycastle.asn1.ASN1OctetString;
import com.android.org.bouncycastle.asn1.ASN1Primitive;
import com.android.org.bouncycastle.asn1.ASN1Sequence;
import com.android.org.bouncycastle.asn1.DERBoolean;
import com.android.org.bouncycastle.asn1.DERObjectIdentifier;
import com.android.org.bouncycastle.asn1.DEROctetString;
import com.android.org.bouncycastle.asn1.DERSequence;
import java.io.IOException;

public class Extension extends ASN1Object {
    public static final ASN1ObjectIdentifier auditIdentity;
    public static final ASN1ObjectIdentifier authorityInfoAccess;
    public static final ASN1ObjectIdentifier authorityKeyIdentifier;
    public static final ASN1ObjectIdentifier basicConstraints;
    public static final ASN1ObjectIdentifier biometricInfo;
    public static final ASN1ObjectIdentifier cRLDistributionPoints;
    public static final ASN1ObjectIdentifier cRLNumber;
    public static final ASN1ObjectIdentifier certificateIssuer;
    public static final ASN1ObjectIdentifier certificatePolicies;
    public static final ASN1ObjectIdentifier deltaCRLIndicator;
    public static final ASN1ObjectIdentifier extendedKeyUsage;
    public static final ASN1ObjectIdentifier freshestCRL;
    public static final ASN1ObjectIdentifier inhibitAnyPolicy;
    public static final ASN1ObjectIdentifier instructionCode;
    public static final ASN1ObjectIdentifier invalidityDate;
    public static final ASN1ObjectIdentifier issuerAlternativeName;
    public static final ASN1ObjectIdentifier issuingDistributionPoint;
    public static final ASN1ObjectIdentifier keyUsage;
    public static final ASN1ObjectIdentifier logoType;
    public static final ASN1ObjectIdentifier nameConstraints;
    public static final ASN1ObjectIdentifier noRevAvail;
    public static final ASN1ObjectIdentifier policyConstraints;
    public static final ASN1ObjectIdentifier policyMappings;
    public static final ASN1ObjectIdentifier privateKeyUsagePeriod;
    public static final ASN1ObjectIdentifier qCStatements;
    public static final ASN1ObjectIdentifier reasonCode;
    public static final ASN1ObjectIdentifier subjectAlternativeName;
    public static final ASN1ObjectIdentifier subjectDirectoryAttributes;
    public static final ASN1ObjectIdentifier subjectInfoAccess;
    public static final ASN1ObjectIdentifier subjectKeyIdentifier;
    public static final ASN1ObjectIdentifier targetInformation;
    private boolean critical;
    private ASN1ObjectIdentifier extnId;
    private ASN1OctetString value;

    static {
        subjectDirectoryAttributes = new ASN1ObjectIdentifier("2.5.29.9");
        subjectKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.14");
        keyUsage = new ASN1ObjectIdentifier("2.5.29.15");
        privateKeyUsagePeriod = new ASN1ObjectIdentifier("2.5.29.16");
        subjectAlternativeName = new ASN1ObjectIdentifier("2.5.29.17");
        issuerAlternativeName = new ASN1ObjectIdentifier("2.5.29.18");
        basicConstraints = new ASN1ObjectIdentifier("2.5.29.19");
        cRLNumber = new ASN1ObjectIdentifier("2.5.29.20");
        reasonCode = new ASN1ObjectIdentifier("2.5.29.21");
        instructionCode = new ASN1ObjectIdentifier("2.5.29.23");
        invalidityDate = new ASN1ObjectIdentifier("2.5.29.24");
        deltaCRLIndicator = new ASN1ObjectIdentifier("2.5.29.27");
        issuingDistributionPoint = new ASN1ObjectIdentifier("2.5.29.28");
        certificateIssuer = new ASN1ObjectIdentifier("2.5.29.29");
        nameConstraints = new ASN1ObjectIdentifier("2.5.29.30");
        cRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31");
        certificatePolicies = new ASN1ObjectIdentifier("2.5.29.32");
        policyMappings = new ASN1ObjectIdentifier("2.5.29.33");
        authorityKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.35");
        policyConstraints = new ASN1ObjectIdentifier("2.5.29.36");
        extendedKeyUsage = new ASN1ObjectIdentifier("2.5.29.37");
        freshestCRL = new ASN1ObjectIdentifier("2.5.29.46");
        inhibitAnyPolicy = new ASN1ObjectIdentifier("2.5.29.54");
        authorityInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1");
        subjectInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.11");
        logoType = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.12");
        biometricInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.2");
        qCStatements = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.3");
        auditIdentity = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.4");
        noRevAvail = new ASN1ObjectIdentifier("2.5.29.56");
        targetInformation = new ASN1ObjectIdentifier("2.5.29.55");
    }

    public Extension(ASN1ObjectIdentifier extnId, ASN1Boolean critical, ASN1OctetString value) {
        this(extnId, critical.isTrue(), value);
    }

    public Extension(ASN1ObjectIdentifier extnId, boolean critical, byte[] value) {
        this(extnId, critical, new DEROctetString(value));
    }

    public Extension(ASN1ObjectIdentifier extnId, boolean critical, ASN1OctetString value) {
        this.extnId = extnId;
        this.critical = critical;
        this.value = value;
    }

    private Extension(ASN1Sequence seq) {
        if (seq.size() == 2) {
            this.extnId = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
            this.critical = false;
            this.value = ASN1OctetString.getInstance(seq.getObjectAt(1));
        } else if (seq.size() == 3) {
            this.extnId = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
            this.critical = DERBoolean.getInstance((Object) seq.getObjectAt(1)).isTrue();
            this.value = ASN1OctetString.getInstance(seq.getObjectAt(2));
        } else {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
    }

    public static Extension getInstance(Object obj) {
        if (obj instanceof Extension) {
            return (Extension) obj;
        }
        if (obj != null) {
            return new Extension(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getExtnId() {
        return this.extnId;
    }

    public boolean isCritical() {
        return this.critical;
    }

    public ASN1OctetString getExtnValue() {
        return this.value;
    }

    public ASN1Encodable getParsedValue() {
        return convertValueToObject(this);
    }

    public int hashCode() {
        if (isCritical()) {
            return getExtnValue().hashCode() ^ getExtnId().hashCode();
        }
        return (getExtnValue().hashCode() ^ getExtnId().hashCode()) ^ -1;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Extension)) {
            return false;
        }
        Extension other = (Extension) o;
        if (other.getExtnId().equals(getExtnId()) && other.getExtnValue().equals(getExtnValue()) && other.isCritical() == isCritical()) {
            return true;
        }
        return false;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.extnId);
        if (this.critical) {
            v.add(DERBoolean.getInstance(true));
        }
        v.add(this.value);
        return new DERSequence(v);
    }

    private static ASN1Primitive convertValueToObject(Extension ext) throws IllegalArgumentException {
        try {
            return ASN1Primitive.fromByteArray(ext.getExtnValue().getOctets());
        } catch (IOException e) {
            throw new IllegalArgumentException("can't convert extension: " + e);
        }
    }
}
