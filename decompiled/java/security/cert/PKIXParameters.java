package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PKIXParameters implements CertPathParameters {
    private boolean anyPolicyInhibited;
    private List<PKIXCertPathChecker> certPathCheckers;
    private List<CertStore> certStores;
    private Date date;
    private boolean explicitPolicyRequired;
    private Set<String> initialPolicies;
    private boolean policyMappingInhibited;
    private boolean policyQualifiersRejected;
    private boolean revocationEnabled;
    private String sigProvider;
    private CertSelector targetCertConstraints;
    private Set<TrustAnchor> trustAnchors;

    public PKIXParameters(Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        this.revocationEnabled = true;
        this.explicitPolicyRequired = false;
        this.policyMappingInhibited = false;
        this.anyPolicyInhibited = false;
        this.policyQualifiersRejected = true;
        if (trustAnchors == null) {
            throw new NullPointerException("trustAnchors == null");
        }
        checkTrustAnchors(trustAnchors);
        this.trustAnchors = new HashSet((Collection) trustAnchors);
    }

    public PKIXParameters(KeyStore keyStore) throws KeyStoreException, InvalidAlgorithmParameterException {
        this.revocationEnabled = true;
        this.explicitPolicyRequired = false;
        this.policyMappingInhibited = false;
        this.anyPolicyInhibited = false;
        this.policyQualifiersRejected = true;
        if (keyStore == null) {
            throw new NullPointerException("keyStore == null");
        } else if (keyStore.size() == 0) {
            throw new InvalidAlgorithmParameterException("keyStore.size() == 0");
        } else {
            this.trustAnchors = new HashSet();
            Enumeration i = keyStore.aliases();
            while (i.hasMoreElements()) {
                String alias = (String) i.nextElement();
                if (keyStore.isCertificateEntry(alias)) {
                    Certificate c = keyStore.getCertificate(alias);
                    if (c instanceof X509Certificate) {
                        this.trustAnchors.add(new TrustAnchor((X509Certificate) c, null));
                    }
                }
            }
            checkTrustAnchors(this.trustAnchors);
        }
    }

    public Set<TrustAnchor> getTrustAnchors() {
        return Collections.unmodifiableSet(this.trustAnchors);
    }

    public void setTrustAnchors(Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        if (trustAnchors == null) {
            throw new NullPointerException("trustAnchors == null");
        }
        checkTrustAnchors(trustAnchors);
        this.trustAnchors = new HashSet((Collection) trustAnchors);
    }

    public boolean isAnyPolicyInhibited() {
        return this.anyPolicyInhibited;
    }

    public void setAnyPolicyInhibited(boolean anyPolicyInhibited) {
        this.anyPolicyInhibited = anyPolicyInhibited;
    }

    public List<PKIXCertPathChecker> getCertPathCheckers() {
        if (this.certPathCheckers == null) {
            this.certPathCheckers = new ArrayList();
        }
        if (this.certPathCheckers.isEmpty()) {
            return Collections.unmodifiableList(this.certPathCheckers);
        }
        ArrayList<PKIXCertPathChecker> modifiableList = new ArrayList();
        for (PKIXCertPathChecker certPathChecker : this.certPathCheckers) {
            modifiableList.add((PKIXCertPathChecker) certPathChecker.clone());
        }
        return Collections.unmodifiableList(modifiableList);
    }

    public void setCertPathCheckers(List<PKIXCertPathChecker> certPathCheckers) {
        if (certPathCheckers != null && !certPathCheckers.isEmpty()) {
            this.certPathCheckers = new ArrayList();
            for (PKIXCertPathChecker certPathChecker : certPathCheckers) {
                this.certPathCheckers.add((PKIXCertPathChecker) certPathChecker.clone());
            }
        } else if (this.certPathCheckers != null && !this.certPathCheckers.isEmpty()) {
            this.certPathCheckers = null;
        }
    }

    public void addCertPathChecker(PKIXCertPathChecker checker) {
        if (checker != null) {
            if (this.certPathCheckers == null) {
                this.certPathCheckers = new ArrayList();
            }
            this.certPathCheckers.add((PKIXCertPathChecker) checker.clone());
        }
    }

    public List<CertStore> getCertStores() {
        if (this.certStores == null) {
            this.certStores = new ArrayList();
        }
        if (this.certStores.isEmpty()) {
            return Collections.unmodifiableList(this.certStores);
        }
        return Collections.unmodifiableList(new ArrayList(this.certStores));
    }

    public void setCertStores(List<CertStore> certStores) {
        if (certStores != null && !certStores.isEmpty()) {
            this.certStores = new ArrayList((Collection) certStores);
        } else if (this.certStores != null && !this.certStores.isEmpty()) {
            this.certStores = null;
        }
    }

    public void addCertStore(CertStore store) {
        if (store != null) {
            if (this.certStores == null) {
                this.certStores = new ArrayList();
            }
            this.certStores.add(store);
        }
    }

    public Date getDate() {
        return this.date == null ? null : (Date) this.date.clone();
    }

    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }

    public boolean isExplicitPolicyRequired() {
        return this.explicitPolicyRequired;
    }

    public void setExplicitPolicyRequired(boolean explicitPolicyRequired) {
        this.explicitPolicyRequired = explicitPolicyRequired;
    }

    public Set<String> getInitialPolicies() {
        if (this.initialPolicies == null) {
            this.initialPolicies = new HashSet();
        }
        if (this.initialPolicies.isEmpty()) {
            return Collections.unmodifiableSet(this.initialPolicies);
        }
        return Collections.unmodifiableSet(new HashSet(this.initialPolicies));
    }

    public void setInitialPolicies(Set<String> initialPolicies) {
        if (initialPolicies != null && !initialPolicies.isEmpty()) {
            this.initialPolicies = new HashSet((Collection) initialPolicies);
        } else if (this.initialPolicies != null && !this.initialPolicies.isEmpty()) {
            this.initialPolicies = null;
        }
    }

    public boolean isPolicyMappingInhibited() {
        return this.policyMappingInhibited;
    }

    public void setPolicyMappingInhibited(boolean policyMappingInhibited) {
        this.policyMappingInhibited = policyMappingInhibited;
    }

    public boolean getPolicyQualifiersRejected() {
        return this.policyQualifiersRejected;
    }

    public void setPolicyQualifiersRejected(boolean policyQualifiersRejected) {
        this.policyQualifiersRejected = policyQualifiersRejected;
    }

    public boolean isRevocationEnabled() {
        return this.revocationEnabled;
    }

    public void setRevocationEnabled(boolean revocationEnabled) {
        this.revocationEnabled = revocationEnabled;
    }

    public String getSigProvider() {
        return this.sigProvider;
    }

    public void setSigProvider(String sigProvider) {
        this.sigProvider = sigProvider;
    }

    public CertSelector getTargetCertConstraints() {
        return this.targetCertConstraints == null ? null : (CertSelector) this.targetCertConstraints.clone();
    }

    public void setTargetCertConstraints(CertSelector targetCertConstraints) {
        this.targetCertConstraints = targetCertConstraints == null ? null : (CertSelector) targetCertConstraints.clone();
    }

    public Object clone() {
        try {
            PKIXParameters ret = (PKIXParameters) super.clone();
            if (this.certStores != null) {
                ret.certStores = new ArrayList(this.certStores);
            }
            if (this.certPathCheckers != null) {
                ret.certPathCheckers = new ArrayList(this.certPathCheckers);
            }
            return ret;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[\n Trust Anchors: ");
        sb.append(this.trustAnchors);
        sb.append("\n Revocation Enabled: ");
        sb.append(this.revocationEnabled);
        sb.append("\n Explicit Policy Required: ");
        sb.append(this.explicitPolicyRequired);
        sb.append("\n Policy Mapping Inhibited: ");
        sb.append(this.policyMappingInhibited);
        sb.append("\n Any Policy Inhibited: ");
        sb.append(this.anyPolicyInhibited);
        sb.append("\n Policy Qualifiers Rejected: ");
        sb.append(this.policyQualifiersRejected);
        sb.append("\n Initial Policy OIDs: ");
        String obj = (this.initialPolicies == null || this.initialPolicies.isEmpty()) ? "any" : this.initialPolicies.toString();
        sb.append(obj);
        sb.append("\n Cert Stores: ");
        obj = (this.certStores == null || this.certStores.isEmpty()) ? "no" : this.certStores.toString();
        sb.append(obj);
        sb.append("\n Validity Date: ");
        sb.append(this.date);
        sb.append("\n Cert Path Checkers: ");
        obj = (this.certPathCheckers == null || this.certPathCheckers.isEmpty()) ? "no" : this.certPathCheckers.toString();
        sb.append(obj);
        sb.append("\n Signature Provider: ");
        sb.append(this.sigProvider);
        sb.append("\n Target Certificate Constraints: ");
        sb.append(this.targetCertConstraints);
        sb.append("\n]");
        return sb.toString();
    }

    private void checkTrustAnchors(Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        if (trustAnchors.isEmpty()) {
            throw new InvalidAlgorithmParameterException("trustAnchors.isEmpty()");
        }
    }
}
