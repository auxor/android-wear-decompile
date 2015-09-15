package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.KeyStore.Entry;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

public abstract class KeyStoreSpi {
    public abstract Enumeration<String> engineAliases();

    public abstract boolean engineContainsAlias(String str);

    public abstract void engineDeleteEntry(String str) throws KeyStoreException;

    public abstract Certificate engineGetCertificate(String str);

    public abstract String engineGetCertificateAlias(Certificate certificate);

    public abstract Certificate[] engineGetCertificateChain(String str);

    public abstract Date engineGetCreationDate(String str);

    public abstract Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException;

    public abstract boolean engineIsCertificateEntry(String str);

    public abstract boolean engineIsKeyEntry(String str);

    public abstract void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException;

    public abstract void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException;

    public abstract void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException;

    public abstract void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException;

    public abstract int engineSize();

    public abstract void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException;

    public void engineStore(LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException();
    }

    public void engineLoad(LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (param == null) {
            engineLoad(null, null);
            return;
        }
        ProtectionParameter pp = param.getProtectionParameter();
        if (pp instanceof PasswordProtection) {
            try {
                engineLoad(null, ((PasswordProtection) pp).getPassword());
            } catch (Throwable e) {
                throw new IllegalArgumentException(e);
            }
        } else if (pp instanceof CallbackHandlerProtection) {
            try {
                engineLoad(null, getPasswordFromCallBack(pp));
            } catch (Throwable e2) {
                throw new IllegalArgumentException(e2);
            }
        } else {
            throw new UnsupportedOperationException("protectionParameter is neither PasswordProtection nor CallbackHandlerProtection instance");
        }
    }

    public Entry engineGetEntry(String alias, ProtectionParameter protParam) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        if (engineIsCertificateEntry(alias)) {
            return new TrustedCertificateEntry(engineGetCertificate(alias));
        }
        char[] passW = null;
        if (protParam != null) {
            if (protParam instanceof PasswordProtection) {
                try {
                    passW = ((PasswordProtection) protParam).getPassword();
                } catch (IllegalStateException ee) {
                    throw new KeyStoreException("Password was destroyed", ee);
                }
            } else if (protParam instanceof CallbackHandlerProtection) {
                passW = getPasswordFromCallBack(protParam);
            } else {
                throw new UnrecoverableEntryException("ProtectionParameter object is not PasswordProtection: " + protParam);
            }
        }
        if (engineIsKeyEntry(alias)) {
            Key key = engineGetKey(alias, passW);
            if (key instanceof PrivateKey) {
                return new PrivateKeyEntry((PrivateKey) key, engineGetCertificateChain(alias));
            }
            if (key instanceof SecretKey) {
                return new SecretKeyEntry((SecretKey) key);
            }
        }
        throw new NoSuchAlgorithmException("Unknown KeyStore.Entry object");
    }

    public void engineSetEntry(String alias, Entry entry, ProtectionParameter protParam) throws KeyStoreException {
        if (entry == null) {
            throw new KeyStoreException("entry == null");
        }
        if (engineContainsAlias(alias)) {
            engineDeleteEntry(alias);
        }
        if (entry instanceof TrustedCertificateEntry) {
            engineSetCertificateEntry(alias, ((TrustedCertificateEntry) entry).getTrustedCertificate());
            return;
        }
        char[] passW = null;
        if (protParam != null) {
            if (protParam instanceof PasswordProtection) {
                try {
                    passW = ((PasswordProtection) protParam).getPassword();
                } catch (IllegalStateException ee) {
                    throw new KeyStoreException("Password was destroyed", ee);
                }
            } else if (protParam instanceof CallbackHandlerProtection) {
                try {
                    passW = getPasswordFromCallBack(protParam);
                } catch (Throwable e) {
                    throw new KeyStoreException(e);
                }
            } else {
                throw new KeyStoreException("protParam should be PasswordProtection or CallbackHandlerProtection");
            }
        }
        if (entry instanceof PrivateKeyEntry) {
            PrivateKeyEntry prE = (PrivateKeyEntry) entry;
            engineSetKeyEntry(alias, prE.getPrivateKey(), passW, prE.getCertificateChain());
        } else if (entry instanceof SecretKeyEntry) {
            engineSetKeyEntry(alias, ((SecretKeyEntry) entry).getSecretKey(), passW, null);
        } else {
            throw new KeyStoreException("Entry object is neither PrivateKeyObject nor SecretKeyEntry nor TrustedCertificateEntry: " + entry);
        }
    }

    public boolean engineEntryInstanceOf(String alias, Class<? extends Entry> entryClass) {
        boolean z = true;
        if (!engineContainsAlias(alias)) {
            return false;
        }
        try {
            if (engineIsCertificateEntry(alias)) {
                return entryClass.isAssignableFrom(Class.forName("java.security.KeyStore$TrustedCertificateEntry"));
            }
            if (!engineIsKeyEntry(alias)) {
                return false;
            }
            if (entryClass.isAssignableFrom(Class.forName("java.security.KeyStore$PrivateKeyEntry"))) {
                if (engineGetCertificate(alias) == null) {
                    z = false;
                }
                return z;
            } else if (!entryClass.isAssignableFrom(Class.forName("java.security.KeyStore$SecretKeyEntry"))) {
                return false;
            } else {
                if (engineGetCertificate(alias) != null) {
                    z = false;
                }
                return z;
            }
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static char[] getPasswordFromCallBack(ProtectionParameter protParam) throws UnrecoverableEntryException {
        if (protParam == null) {
            return null;
        }
        if (protParam instanceof CallbackHandlerProtection) {
            String clName = Security.getProperty("auth.login.defaultCallbackHandler");
            if (clName == null) {
                throw new UnrecoverableEntryException("Default CallbackHandler was not defined");
            }
            try {
                PasswordCallback[] pwCb = new PasswordCallback[]{new PasswordCallback("password: ", true)};
                ((CallbackHandler) Class.forName(clName).newInstance()).handle(pwCb);
                return pwCb[0].getPassword();
            } catch (Exception e) {
                throw new UnrecoverableEntryException(e.toString());
            }
        }
        throw new UnrecoverableEntryException("Incorrect ProtectionParameter");
    }
}
