package android.os;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RecoverySystem {
    private static File COMMAND_FILE = null;
    private static final File DEFAULT_KEYSTORE;
    private static String LAST_PREFIX = null;
    private static File LOG_FILE = null;
    private static int LOG_FILE_MAX_LENGTH = 0;
    private static final long PUBLISH_PROGRESS_INTERVAL_MS = 500;
    private static File RECOVERY_DIR = null;
    private static final String TAG = "RecoverySystem";

    /* renamed from: android.os.RecoverySystem.1 */
    static class AnonymousClass1 extends BroadcastReceiver {
        final /* synthetic */ ConditionVariable val$condition;

        AnonymousClass1(android.os.ConditionVariable r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.RecoverySystem.1.<init>(android.os.ConditionVariable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.RecoverySystem.1.<init>(android.os.ConditionVariable):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.RecoverySystem.1.<init>(android.os.ConditionVariable):void");
        }

        public void onReceive(android.content.Context r1, android.content.Intent r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.RecoverySystem.1.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.RecoverySystem.1.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.RecoverySystem.1.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    public interface ProgressListener {
        void onProgress(int i);
    }

    static {
        DEFAULT_KEYSTORE = new File("/system/etc/security/otacerts.zip");
        RECOVERY_DIR = new File("/cache/recovery");
        COMMAND_FILE = new File(RECOVERY_DIR, "command");
        LOG_FILE = new File(RECOVERY_DIR, "log");
        LAST_PREFIX = "last_";
        LOG_FILE_MAX_LENGTH = AccessibilityNodeInfo.ACTION_CUT;
    }

    private static HashSet<X509Certificate> getTrustedCerts(File keystore) throws IOException, GeneralSecurityException {
        InputStream is;
        HashSet<X509Certificate> trusted = new HashSet();
        if (keystore == null) {
            keystore = DEFAULT_KEYSTORE;
        }
        ZipFile zip = new ZipFile(keystore);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                is = zip.getInputStream((ZipEntry) entries.nextElement());
                trusted.add((X509Certificate) cf.generateCertificate(is));
                is.close();
            }
            zip.close();
            return trusted;
        } catch (Throwable th) {
            zip.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void verifyPackage(java.io.File r50, android.os.RecoverySystem.ProgressListener r51, java.io.File r52) throws java.io.IOException, java.security.GeneralSecurityException {
        /*
        r16 = r50.length();
        r30 = new java.io.RandomAccessFile;
        r45 = "r";
        r0 = r30;
        r1 = r50;
        r2 = r45;
        r0.<init>(r1, r2);
        r24 = 0;
        r26 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0058 }
        if (r51 == 0) goto L_0x0021;
    L_0x001a:
        r0 = r51;
        r1 = r24;
        r0.onProgress(r1);	 Catch:{ all -> 0x0058 }
    L_0x0021:
        r46 = 6;
        r46 = r16 - r46;
        r0 = r30;
        r1 = r46;
        r0.seek(r1);	 Catch:{ all -> 0x0058 }
        r45 = 6;
        r0 = r45;
        r15 = new byte[r0];	 Catch:{ all -> 0x0058 }
        r0 = r30;
        r0.readFully(r15);	 Catch:{ all -> 0x0058 }
        r45 = 2;
        r45 = r15[r45];	 Catch:{ all -> 0x0058 }
        r46 = -1;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x004f;
    L_0x0043:
        r45 = 3;
        r45 = r15[r45];	 Catch:{ all -> 0x0058 }
        r46 = -1;
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x005d;
    L_0x004f:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "no signature in file (no footer)";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x0058:
        r45 = move-exception;
        r30.close();
        throw r45;
    L_0x005d:
        r45 = 4;
        r45 = r15[r45];	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r0 = r0 & 255;
        r45 = r0;
        r46 = 5;
        r46 = r15[r46];	 Catch:{ all -> 0x0058 }
        r0 = r46;
        r0 = r0 & 255;
        r46 = r0;
        r46 = r46 << 8;
        r10 = r45 | r46;
        r45 = 0;
        r45 = r15[r45];	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r0 = r0 & 255;
        r45 = r0;
        r46 = 1;
        r46 = r15[r46];	 Catch:{ all -> 0x0058 }
        r0 = r46;
        r0 = r0 & 255;
        r46 = r0;
        r46 = r46 << 8;
        r36 = r45 | r46;
        r45 = r10 + 22;
        r0 = r45;
        r14 = new byte[r0];	 Catch:{ all -> 0x0058 }
        r45 = r10 + 22;
        r0 = r45;
        r0 = (long) r0;	 Catch:{ all -> 0x0058 }
        r46 = r0;
        r46 = r16 - r46;
        r0 = r30;
        r1 = r46;
        r0.seek(r1);	 Catch:{ all -> 0x0058 }
        r0 = r30;
        r0.readFully(r14);	 Catch:{ all -> 0x0058 }
        r45 = 0;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 80;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x00d8;
    L_0x00b4:
        r45 = 1;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 75;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x00d8;
    L_0x00c0:
        r45 = 2;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 5;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x00d8;
    L_0x00cc:
        r45 = 3;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 6;
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x00e1;
    L_0x00d8:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "no signature in file (bad footer)";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x00e1:
        r18 = 4;
    L_0x00e3:
        r0 = r14.length;	 Catch:{ all -> 0x0058 }
        r45 = r0;
        r45 = r45 + -3;
        r0 = r18;
        r1 = r45;
        if (r0 >= r1) goto L_0x0127;
    L_0x00ee:
        r45 = r14[r18];	 Catch:{ all -> 0x0058 }
        r46 = 80;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x0124;
    L_0x00f8:
        r45 = r18 + 1;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 75;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x0124;
    L_0x0104:
        r45 = r18 + 2;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 5;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x0124;
    L_0x0110:
        r45 = r18 + 3;
        r45 = r14[r45];	 Catch:{ all -> 0x0058 }
        r46 = 6;
        r0 = r45;
        r1 = r46;
        if (r0 != r1) goto L_0x0124;
    L_0x011c:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "EOCD marker found after start of EOCD";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x0124:
        r18 = r18 + 1;
        goto L_0x00e3;
    L_0x0127:
        r5 = new org.apache.harmony.security.asn1.BerInputStream;	 Catch:{ all -> 0x0058 }
        r45 = new java.io.ByteArrayInputStream;	 Catch:{ all -> 0x0058 }
        r46 = r10 + 22;
        r46 = r46 - r36;
        r0 = r45;
        r1 = r46;
        r2 = r36;
        r0.<init>(r14, r1, r2);	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r5.<init>(r0);	 Catch:{ all -> 0x0058 }
        r45 = org.apache.harmony.security.pkcs7.ContentInfo.ASN1;	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r20 = r0.decode(r5);	 Catch:{ all -> 0x0058 }
        r20 = (org.apache.harmony.security.pkcs7.ContentInfo) r20;	 Catch:{ all -> 0x0058 }
        r37 = r20.getSignedData();	 Catch:{ all -> 0x0058 }
        if (r37 != 0) goto L_0x0156;
    L_0x014d:
        r45 = new java.io.IOException;	 Catch:{ all -> 0x0058 }
        r46 = "signedData is null";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x0156:
        r13 = r37.getCertificates();	 Catch:{ all -> 0x0058 }
        r45 = r13.isEmpty();	 Catch:{ all -> 0x0058 }
        if (r45 == 0) goto L_0x0168;
    L_0x0160:
        r45 = new java.io.IOException;	 Catch:{ all -> 0x0058 }
        r46 = "encCerts is empty";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x0168:
        r23 = r13.iterator();	 Catch:{ all -> 0x0058 }
        r8 = 0;
        r45 = r23.hasNext();	 Catch:{ all -> 0x0058 }
        if (r45 == 0) goto L_0x01e3;
    L_0x0173:
        r45 = "X.509";
        r9 = java.security.cert.CertificateFactory.getInstance(r45);	 Catch:{ all -> 0x0058 }
        r22 = new java.io.ByteArrayInputStream;	 Catch:{ all -> 0x0058 }
        r45 = r23.next();	 Catch:{ all -> 0x0058 }
        r45 = (org.apache.harmony.security.x509.Certificate) r45;	 Catch:{ all -> 0x0058 }
        r45 = r45.getEncoded();	 Catch:{ all -> 0x0058 }
        r0 = r22;
        r1 = r45;
        r0.<init>(r1);	 Catch:{ all -> 0x0058 }
        r0 = r22;
        r8 = r9.generateCertificate(r0);	 Catch:{ all -> 0x0058 }
        r8 = (java.security.cert.X509Certificate) r8;	 Catch:{ all -> 0x0058 }
        r34 = r37.getSignerInfos();	 Catch:{ all -> 0x0058 }
        r45 = r34.isEmpty();	 Catch:{ all -> 0x0058 }
        if (r45 != 0) goto L_0x01ec;
    L_0x019e:
        r45 = 0;
        r0 = r34;
        r1 = r45;
        r33 = r0.get(r1);	 Catch:{ all -> 0x0058 }
        r33 = (org.apache.harmony.security.pkcs7.SignerInfo) r33;	 Catch:{ all -> 0x0058 }
        if (r52 != 0) goto L_0x01ae;
    L_0x01ac:
        r52 = DEFAULT_KEYSTORE;	 Catch:{ all -> 0x0058 }
    L_0x01ae:
        r39 = getTrustedCerts(r52);	 Catch:{ all -> 0x0058 }
        r35 = r8.getPublicKey();	 Catch:{ all -> 0x0058 }
        r44 = 0;
        r19 = r39.iterator();	 Catch:{ all -> 0x0058 }
    L_0x01bc:
        r45 = r19.hasNext();	 Catch:{ all -> 0x0058 }
        if (r45 == 0) goto L_0x01d8;
    L_0x01c2:
        r7 = r19.next();	 Catch:{ all -> 0x0058 }
        r7 = (java.security.cert.X509Certificate) r7;	 Catch:{ all -> 0x0058 }
        r45 = r7.getPublicKey();	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r1 = r35;
        r45 = r0.equals(r1);	 Catch:{ all -> 0x0058 }
        if (r45 == 0) goto L_0x01bc;
    L_0x01d6:
        r44 = 1;
    L_0x01d8:
        if (r44 != 0) goto L_0x01f5;
    L_0x01da:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "signature doesn't match any trusted key";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x01e3:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "signature contains no certificates";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x01ec:
        r45 = new java.io.IOException;	 Catch:{ all -> 0x0058 }
        r46 = "no signer infos!";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x01f5:
        r11 = r33.getDigestAlgorithm();	 Catch:{ all -> 0x0058 }
        r12 = r33.getDigestEncryptionAlgorithm();	 Catch:{ all -> 0x0058 }
        r4 = 0;
        if (r11 == 0) goto L_0x0202;
    L_0x0200:
        if (r12 != 0) goto L_0x024b;
    L_0x0202:
        r4 = r8.getSigAlgName();	 Catch:{ all -> 0x0058 }
    L_0x0206:
        r32 = java.security.Signature.getInstance(r4);	 Catch:{ all -> 0x0058 }
        r0 = r32;
        r0.initVerify(r8);	 Catch:{ all -> 0x0058 }
        r0 = (long) r10;	 Catch:{ all -> 0x0058 }
        r46 = r0;
        r46 = r16 - r46;
        r48 = 2;
        r42 = r46 - r48;
        r40 = 0;
        r46 = 0;
        r0 = r30;
        r1 = r46;
        r0.seek(r1);	 Catch:{ all -> 0x0058 }
        r45 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r45;
        r6 = new byte[r0];	 Catch:{ all -> 0x0058 }
        r21 = 0;
    L_0x022b:
        r45 = (r40 > r42 ? 1 : (r40 == r42 ? 0 : -1));
        if (r45 >= 0) goto L_0x0235;
    L_0x022f:
        r21 = java.lang.Thread.interrupted();	 Catch:{ all -> 0x0058 }
        if (r21 == 0) goto L_0x0268;
    L_0x0235:
        if (r51 == 0) goto L_0x0240;
    L_0x0237:
        r45 = 100;
        r0 = r51;
        r1 = r45;
        r0.onProgress(r1);	 Catch:{ all -> 0x0058 }
    L_0x0240:
        if (r21 == 0) goto L_0x02c7;
    L_0x0242:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "verification was interrupted";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x024b:
        r45 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0058 }
        r45.<init>();	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r45 = r0.append(r11);	 Catch:{ all -> 0x0058 }
        r46 = "with";
        r45 = r45.append(r46);	 Catch:{ all -> 0x0058 }
        r0 = r45;
        r45 = r0.append(r12);	 Catch:{ all -> 0x0058 }
        r4 = r45.toString();	 Catch:{ all -> 0x0058 }
        goto L_0x0206;
    L_0x0268:
        r0 = r6.length;	 Catch:{ all -> 0x0058 }
        r38 = r0;
        r0 = r38;
        r0 = (long) r0;	 Catch:{ all -> 0x0058 }
        r46 = r0;
        r46 = r46 + r40;
        r45 = (r46 > r42 ? 1 : (r46 == r42 ? 0 : -1));
        if (r45 <= 0) goto L_0x027d;
    L_0x0276:
        r46 = r42 - r40;
        r0 = r46;
        r0 = (int) r0;	 Catch:{ all -> 0x0058 }
        r38 = r0;
    L_0x027d:
        r45 = 0;
        r0 = r30;
        r1 = r45;
        r2 = r38;
        r31 = r0.read(r6, r1, r2);	 Catch:{ all -> 0x0058 }
        r45 = 0;
        r0 = r32;
        r1 = r45;
        r2 = r31;
        r0.update(r6, r1, r2);	 Catch:{ all -> 0x0058 }
        r0 = r31;
        r0 = (long) r0;	 Catch:{ all -> 0x0058 }
        r46 = r0;
        r40 = r40 + r46;
        if (r51 == 0) goto L_0x022b;
    L_0x029d:
        r28 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0058 }
        r46 = 100;
        r46 = r46 * r40;
        r46 = r46 / r42;
        r0 = r46;
        r0 = (int) r0;	 Catch:{ all -> 0x0058 }
        r25 = r0;
        r0 = r25;
        r1 = r24;
        if (r0 <= r1) goto L_0x022b;
    L_0x02b2:
        r46 = r28 - r26;
        r48 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r45 = (r46 > r48 ? 1 : (r46 == r48 ? 0 : -1));
        if (r45 <= 0) goto L_0x022b;
    L_0x02ba:
        r24 = r25;
        r26 = r28;
        r0 = r51;
        r1 = r24;
        r0.onProgress(r1);	 Catch:{ all -> 0x0058 }
        goto L_0x022b;
    L_0x02c7:
        r45 = r33.getEncryptedDigest();	 Catch:{ all -> 0x0058 }
        r0 = r32;
        r1 = r45;
        r45 = r0.verify(r1);	 Catch:{ all -> 0x0058 }
        if (r45 != 0) goto L_0x02de;
    L_0x02d5:
        r45 = new java.security.SignatureException;	 Catch:{ all -> 0x0058 }
        r46 = "signature digest verification failed";
        r45.<init>(r46);	 Catch:{ all -> 0x0058 }
        throw r45;	 Catch:{ all -> 0x0058 }
    L_0x02de:
        r30.close();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.RecoverySystem.verifyPackage(java.io.File, android.os.RecoverySystem$ProgressListener, java.io.File):void");
    }

    public static void installPackage(Context context, File packageFile) throws IOException {
        String filename = packageFile.getCanonicalPath();
        Log.w(TAG, "!!! REBOOTING TO INSTALL " + filename + " !!!");
        String filenameArg = "--update_package=" + filename;
        String localeArg = "--locale=" + Locale.getDefault().toString();
        bootCommand(context, filenameArg, localeArg);
    }

    public static void rebootWipeUserData(Context context) throws IOException {
        rebootWipeUserData(context, false, context.getPackageName());
    }

    public static void rebootWipeUserData(Context context, String reason) throws IOException {
        rebootWipeUserData(context, false, reason);
    }

    public static void rebootWipeUserData(Context context, boolean shutdown) throws IOException {
        rebootWipeUserData(context, shutdown, context.getPackageName());
    }

    public static void rebootWipeUserData(Context context, boolean shutdown, String reason) throws IOException {
        if (((UserManager) context.getSystemService(Context.USER_SERVICE)).hasUserRestriction(UserManager.DISALLOW_FACTORY_RESET)) {
            throw new SecurityException("Wiping data is not allowed for this user.");
        }
        ConditionVariable condition = new ConditionVariable();
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR_NOTIFICATION");
        intent.addFlags(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        context.sendOrderedBroadcastAsUser(intent, UserHandle.OWNER, permission.MASTER_CLEAR, new AnonymousClass1(condition), null, 0, null, null);
        condition.block();
        String shutdownArg = null;
        if (shutdown) {
            shutdownArg = "--shutdown_after";
        }
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            reasonArg = "--reason=" + sanitizeArg(reason);
        }
        String localeArg = "--locale=" + Locale.getDefault().toString();
        bootCommand(context, shutdownArg, "--wipe_data", reasonArg, localeArg);
    }

    public static void rebootWipeCache(Context context) throws IOException {
        rebootWipeCache(context, context.getPackageName());
    }

    public static void rebootWipeCache(Context context, String reason) throws IOException {
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            reasonArg = "--reason=" + sanitizeArg(reason);
        }
        String localeArg = "--locale=" + Locale.getDefault().toString();
        bootCommand(context, "--wipe_cache", reasonArg, localeArg);
    }

    private static void bootCommand(Context context, String... args) throws IOException {
        RECOVERY_DIR.mkdirs();
        COMMAND_FILE.delete();
        LOG_FILE.delete();
        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            for (String arg : args) {
                if (!TextUtils.isEmpty(arg)) {
                    command.write(arg);
                    command.write("\n");
                }
            }
            ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).reboot(PowerManager.REBOOT_RECOVERY);
            throw new IOException("Reboot failed (no permissions?)");
        } finally {
            command.close();
        }
    }

    public static String handleAftermath() {
        String log = null;
        try {
            log = FileUtils.readTextFile(LOG_FILE, -LOG_FILE_MAX_LENGTH, "...\n");
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No recovery log file");
        } catch (IOException e2) {
            Log.e(TAG, "Error reading recovery log", e2);
        }
        String[] names = RECOVERY_DIR.list();
        int i = 0;
        while (names != null && i < names.length) {
            if (!names[i].startsWith(LAST_PREFIX)) {
                File f = new File(RECOVERY_DIR, names[i]);
                if (f.delete()) {
                    Log.i(TAG, "Deleted: " + f);
                } else {
                    Log.e(TAG, "Can't delete: " + f);
                }
            }
            i++;
        }
        return log;
    }

    private static String sanitizeArg(String arg) {
        return arg.replace('\u0000', '?').replace('\n', '?');
    }

    private void RecoverySystem() {
    }
}
