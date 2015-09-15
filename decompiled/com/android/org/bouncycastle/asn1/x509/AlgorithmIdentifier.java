package com.android.org.bouncycastle.asn1.x509;

import com.android.org.bouncycastle.asn1.ASN1Encodable;
import com.android.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.org.bouncycastle.asn1.ASN1Object;
import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.org.bouncycastle.asn1.ASN1Primitive;
import com.android.org.bouncycastle.asn1.ASN1Sequence;
import com.android.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.org.bouncycastle.asn1.DERNull;
import com.android.org.bouncycastle.asn1.DERObjectIdentifier;
import com.android.org.bouncycastle.asn1.DERSequence;

public class AlgorithmIdentifier extends ASN1Object {
    private ASN1ObjectIdentifier objectId;
    private ASN1Encodable parameters;
    private boolean parametersDefined;

    public static AlgorithmIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AlgorithmIdentifier getInstance(Object obj) {
        if (obj == null || (obj instanceof AlgorithmIdentifier)) {
            return (AlgorithmIdentifier) obj;
        }
        if (obj instanceof ASN1ObjectIdentifier) {
            return new AlgorithmIdentifier((ASN1ObjectIdentifier) obj);
        }
        if (obj instanceof String) {
            return new AlgorithmIdentifier((String) obj);
        }
        return new AlgorithmIdentifier(ASN1Sequence.getInstance(obj));
    }

    public AlgorithmIdentifier(ASN1ObjectIdentifier objectId) {
        this.parametersDefined = false;
        this.objectId = objectId;
    }

    public AlgorithmIdentifier(String objectId) {
        this.parametersDefined = false;
        this.objectId = new ASN1ObjectIdentifier(objectId);
    }

    public AlgorithmIdentifier(DERObjectIdentifier objectId) {
        this.parametersDefined = false;
        this.objectId = new ASN1ObjectIdentifier(objectId.getId());
    }

    public AlgorithmIdentifier(DERObjectIdentifier objectId, ASN1Encodable parameters) {
        this.parametersDefined = false;
        this.parametersDefined = true;
        this.objectId = new ASN1ObjectIdentifier(objectId.getId());
        this.parameters = parameters;
    }

    public AlgorithmIdentifier(ASN1ObjectIdentifier objectId, ASN1Encodable parameters) {
        this.parametersDefined = false;
        this.parametersDefined = true;
        this.objectId = objectId;
        this.parameters = parameters;
    }

    public AlgorithmIdentifier(ASN1Sequence seq) {
        this.parametersDefined = false;
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.objectId = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.parametersDefined = true;
            this.parameters = seq.getObjectAt(1);
            return;
        }
        this.parameters = null;
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifier(this.objectId.getId());
    }

    public ASN1ObjectIdentifier getObjectId() {
        return this.objectId;
    }

    public ASN1Encodable getParameters() {
        return this.parameters;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.objectId);
        if (this.parametersDefined) {
            if (this.parameters != null) {
                v.add(this.parameters);
            } else {
                v.add(DERNull.INSTANCE);
            }
        }
        return new DERSequence(v);
    }
}
