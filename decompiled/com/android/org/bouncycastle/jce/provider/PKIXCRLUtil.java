package com.android.org.bouncycastle.jce.provider;

import com.android.org.bouncycastle.x509.ExtendedPKIXParameters;
import com.android.org.bouncycastle.x509.X509CRLStoreSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PKIXCRLUtil {
    public Set findCRLs(X509CRLStoreSelector crlselect, ExtendedPKIXParameters paramsPKIX, Date currentDate) throws AnnotatedException {
        Set<X509CRL> initialSet = new HashSet();
        try {
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getAdditionalStores()));
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getStores()));
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getCertStores()));
            Set finalSet = new HashSet();
            Date validityDate = currentDate;
            if (paramsPKIX.getDate() != null) {
                validityDate = paramsPKIX.getDate();
            }
            for (X509CRL crl : initialSet) {
                if (crl.getNextUpdate().after(validityDate)) {
                    X509Certificate cert = crlselect.getCertificateChecking();
                    if (cert == null) {
                        finalSet.add(crl);
                    } else if (crl.getThisUpdate().before(cert.getNotAfter())) {
                        finalSet.add(crl);
                    }
                }
            }
            return finalSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    public Set findCRLs(X509CRLStoreSelector crlselect, PKIXParameters paramsPKIX) throws AnnotatedException {
        Set completeSet = new HashSet();
        try {
            completeSet.addAll(findCRLs(crlselect, paramsPKIX.getCertStores()));
            return completeSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    private final Collection findCRLs(X509CRLStoreSelector crlSelect, List crlStores) throws AnnotatedException {
        Set crls = new HashSet();
        AnnotatedException lastException = null;
        boolean foundValidStore = false;
        for (CertStore store : crlStores) {
            try {
                crls.addAll(store.getCRLs(crlSelect));
                foundValidStore = true;
            } catch (CertStoreException e) {
                lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
            }
        }
        if (foundValidStore || lastException == null) {
            return crls;
        }
        throw lastException;
    }
}
