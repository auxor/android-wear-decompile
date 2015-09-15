package com.android.org.conscrypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OpenSSLX509CertPath extends CertPath {
    private static final List<String> ALL_ENCODINGS;
    private static final Encoding DEFAULT_ENCODING;
    private static final byte[] PKCS7_MARKER;
    private static final int PUSHBACK_SIZE = 64;
    private final List<? extends X509Certificate> mCertificates;

    /* renamed from: com.android.org.conscrypt.OpenSSLX509CertPath.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding;

        static {
            $SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding = new int[Encoding.values().length];
            try {
                $SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding[Encoding.PKI_PATH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding[Encoding.PKCS7.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private enum Encoding {
        PKI_PATH("PkiPath"),
        PKCS7("PKCS7");
        
        private final String apiName;

        private Encoding(String apiName) {
            this.apiName = apiName;
        }

        static Encoding findByApiName(String apiName) throws CertificateEncodingException {
            for (Encoding element : values()) {
                if (element.apiName.equals(apiName)) {
                    return element;
                }
            }
            return null;
        }
    }

    static {
        PKCS7_MARKER = new byte[]{(byte) 45, (byte) 45, (byte) 45, (byte) 45, (byte) 45, NativeCrypto.TLS_CT_ECDSA_FIXED_ECDH, (byte) 69, (byte) 71, (byte) 73, (byte) 78, (byte) 32, (byte) 80, (byte) 75, (byte) 67, (byte) 83, (byte) 55};
        ALL_ENCODINGS = Collections.unmodifiableList(Arrays.asList(new String[]{Encoding.PKI_PATH.apiName, Encoding.PKCS7.apiName}));
        DEFAULT_ENCODING = Encoding.PKI_PATH;
    }

    static Iterator<String> getEncodingsIterator() {
        return ALL_ENCODINGS.iterator();
    }

    protected OpenSSLX509CertPath(List<? extends X509Certificate> certificates) {
        super("X.509");
        this.mCertificates = certificates;
    }

    public List<? extends Certificate> getCertificates() {
        return Collections.unmodifiableList(this.mCertificates);
    }

    private byte[] getEncoded(Encoding encoding) throws CertificateEncodingException {
        OpenSSLX509Certificate[] certs = new OpenSSLX509Certificate[this.mCertificates.size()];
        long[] certRefs = new long[certs.length];
        int i = 0;
        for (int j = certs.length - 1; j >= 0; j--) {
            X509Certificate cert = (X509Certificate) this.mCertificates.get(i);
            if (cert instanceof OpenSSLX509Certificate) {
                certs[j] = (OpenSSLX509Certificate) cert;
            } else {
                certs[j] = OpenSSLX509Certificate.fromX509Der(cert.getEncoded());
            }
            certRefs[j] = certs[j].getContext();
            i++;
        }
        switch (AnonymousClass1.$SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding[encoding.ordinal()]) {
            case NativeCrypto.SSL_kRSA /*1*/:
                return NativeCrypto.ASN1_seq_pack_X509(certRefs);
            case NativeCrypto.SSL_kDHr /*2*/:
                return NativeCrypto.i2d_PKCS7(certRefs);
            default:
                throw new CertificateEncodingException("Unknown encoding");
        }
    }

    public byte[] getEncoded() throws CertificateEncodingException {
        return getEncoded(DEFAULT_ENCODING);
    }

    public byte[] getEncoded(String encoding) throws CertificateEncodingException {
        Encoding enc = Encoding.findByApiName(encoding);
        if (enc != null) {
            return getEncoded(enc);
        }
        throw new CertificateEncodingException("Invalid encoding: " + encoding);
    }

    public Iterator<String> getEncodings() {
        return getEncodingsIterator();
    }

    private static CertPath fromPkiPathEncoding(InputStream inStream) throws CertificateException {
        OpenSSLBIOInputStream bis = new OpenSSLBIOInputStream(inStream);
        boolean markable = inStream.markSupported();
        if (markable) {
            inStream.mark(PUSHBACK_SIZE);
        }
        try {
            long[] certRefs = NativeCrypto.ASN1_seq_unpack_X509_bio(bis.getBioContext());
            bis.release();
            if (certRefs == null) {
                return new OpenSSLX509CertPath(Collections.emptyList());
            }
            List<OpenSSLX509Certificate> certs = new ArrayList(certRefs.length);
            for (int i = certRefs.length - 1; i >= 0; i--) {
                if (certRefs[i] != 0) {
                    certs.add(new OpenSSLX509Certificate(certRefs[i]));
                }
            }
            return new OpenSSLX509CertPath(certs);
        } catch (Exception e) {
            if (markable) {
                try {
                    inStream.reset();
                } catch (IOException e2) {
                }
            }
            throw new CertificateException(e);
        } catch (Throwable th) {
            bis.release();
        }
    }

    private static CertPath fromPkcs7Encoding(InputStream inStream) throws CertificateException {
        if (inStream != null) {
            try {
                if (inStream.available() != 0) {
                    boolean markable = inStream.markSupported();
                    if (markable) {
                        inStream.mark(PUSHBACK_SIZE);
                    }
                    PushbackInputStream pbis = new PushbackInputStream(inStream, PUSHBACK_SIZE);
                    try {
                        byte[] buffer = new byte[PKCS7_MARKER.length];
                        int len = pbis.read(buffer);
                        if (len < 0) {
                            throw new ParsingException("inStream is empty");
                        }
                        pbis.unread(buffer, 0, len);
                        if (len == PKCS7_MARKER.length && Arrays.equals(PKCS7_MARKER, buffer)) {
                            return new OpenSSLX509CertPath(OpenSSLX509Certificate.fromPkcs7PemInputStream(pbis));
                        }
                        return new OpenSSLX509CertPath(OpenSSLX509Certificate.fromPkcs7DerInputStream(pbis));
                    } catch (Exception e) {
                        if (markable) {
                            try {
                                inStream.reset();
                            } catch (IOException e2) {
                            }
                        }
                        throw new CertificateException(e);
                    }
                }
            } catch (IOException e3) {
                throw new CertificateException("Problem reading input stream", e3);
            }
        }
        return new OpenSSLX509CertPath(Collections.emptyList());
    }

    private static CertPath fromEncoding(InputStream inStream, Encoding encoding) throws CertificateException {
        switch (AnonymousClass1.$SwitchMap$org$conscrypt$OpenSSLX509CertPath$Encoding[encoding.ordinal()]) {
            case NativeCrypto.SSL_kRSA /*1*/:
                return fromPkiPathEncoding(inStream);
            case NativeCrypto.SSL_kDHr /*2*/:
                return fromPkcs7Encoding(inStream);
            default:
                throw new CertificateEncodingException("Unknown encoding");
        }
    }

    public static CertPath fromEncoding(InputStream inStream, String encoding) throws CertificateException {
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        Encoding enc = Encoding.findByApiName(encoding);
        if (enc != null) {
            return fromEncoding(inStream, enc);
        }
        throw new CertificateException("Invalid encoding: " + encoding);
    }

    public static CertPath fromEncoding(InputStream inStream) throws CertificateException {
        return fromEncoding(inStream, DEFAULT_ENCODING);
    }
}
