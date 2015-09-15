package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathValidatorResult implements CertPathValidatorResult {
    private final PolicyNode policyTree;
    private final PublicKey subjectPublicKey;
    private final TrustAnchor trustAnchor;

    public PKIXCertPathValidatorResult(TrustAnchor trustAnchor, PolicyNode policyTree, PublicKey subjectPublicKey) {
        this.trustAnchor = trustAnchor;
        this.policyTree = policyTree;
        this.subjectPublicKey = subjectPublicKey;
        if (this.trustAnchor == null) {
            throw new NullPointerException("trustAnchor == null");
        } else if (this.subjectPublicKey == null) {
            throw new NullPointerException("subjectPublicKey == null");
        }
    }

    public PolicyNode getPolicyTree() {
        return this.policyTree;
    }

    public PublicKey getPublicKey() {
        return this.subjectPublicKey;
    }

    public TrustAnchor getTrustAnchor() {
        return this.trustAnchor;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(": [\n Trust Anchor: ");
        sb.append(this.trustAnchor.toString());
        sb.append("\n Policy Tree: ");
        sb.append(this.policyTree == null ? "no valid policy tree\n" : this.policyTree.toString());
        sb.append("\n Subject Public Key: ");
        sb.append(this.subjectPublicKey.toString());
        sb.append("\n]");
        return sb.toString();
    }
}
