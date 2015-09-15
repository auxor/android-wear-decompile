package com.android.org.conscrypt;

import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

public final class TrustedCertificateIndex {
    private final Map<X500Principal, List<TrustAnchor>> subjectToTrustAnchors;

    public TrustedCertificateIndex() {
        this.subjectToTrustAnchors = new HashMap();
    }

    public TrustedCertificateIndex(Set<TrustAnchor> anchors) {
        this.subjectToTrustAnchors = new HashMap();
        index((Set) anchors);
    }

    private void index(Set<TrustAnchor> anchors) {
        for (TrustAnchor anchor : anchors) {
            index(anchor);
        }
    }

    public TrustAnchor index(X509Certificate cert) {
        TrustAnchor anchor = new TrustAnchor(cert, null);
        index(anchor);
        return anchor;
    }

    public void index(TrustAnchor anchor) {
        X500Principal subject;
        X509Certificate cert = anchor.getTrustedCert();
        if (cert != null) {
            subject = cert.getSubjectX500Principal();
        } else {
            subject = anchor.getCA();
        }
        synchronized (this.subjectToTrustAnchors) {
            List<TrustAnchor> anchors = (List) this.subjectToTrustAnchors.get(subject);
            if (anchors == null) {
                anchors = new ArrayList(1);
                this.subjectToTrustAnchors.put(subject, anchors);
            }
            anchors.add(anchor);
        }
    }

    public void reset() {
        synchronized (this.subjectToTrustAnchors) {
            this.subjectToTrustAnchors.clear();
        }
    }

    public void reset(Set<TrustAnchor> anchors) {
        synchronized (this.subjectToTrustAnchors) {
            reset();
            index((Set) anchors);
        }
    }

    public TrustAnchor findByIssuerAndSignature(X509Certificate cert) {
        X500Principal issuer = cert.getIssuerX500Principal();
        synchronized (this.subjectToTrustAnchors) {
            List<TrustAnchor> anchors = (List) this.subjectToTrustAnchors.get(issuer);
            if (anchors == null) {
                return null;
            }
            for (TrustAnchor anchor : anchors) {
                try {
                    PublicKey publicKey;
                    X509Certificate caCert = anchor.getTrustedCert();
                    if (caCert != null) {
                        publicKey = caCert.getPublicKey();
                    } else {
                        publicKey = anchor.getCAPublicKey();
                    }
                    cert.verify(publicKey);
                    return anchor;
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    public TrustAnchor findBySubjectAndPublicKey(X509Certificate cert) {
        TrustAnchor trustAnchor;
        X500Principal subject = cert.getSubjectX500Principal();
        synchronized (this.subjectToTrustAnchors) {
            List<TrustAnchor> anchors = (List) this.subjectToTrustAnchors.get(subject);
            if (anchors == null) {
                trustAnchor = null;
            } else {
                trustAnchor = findBySubjectAndPublicKey(cert, anchors);
            }
        }
        return trustAnchor;
    }

    private static TrustAnchor findBySubjectAndPublicKey(X509Certificate cert, Collection<TrustAnchor> anchors) {
        PublicKey certPublicKey = cert.getPublicKey();
        for (TrustAnchor anchor : anchors) {
            try {
                PublicKey caPublicKey;
                X509Certificate caCert = anchor.getTrustedCert();
                if (caCert != null) {
                    caPublicKey = caCert.getPublicKey();
                } else {
                    caPublicKey = anchor.getCAPublicKey();
                }
                if (caPublicKey.equals(certPublicKey)) {
                    return anchor;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
