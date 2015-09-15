package android.util;

import com.android.internal.R;
import com.android.internal.http.multipart.StringPart;
import java.io.UnsupportedEncodingException;
import javax.microedition.khronos.opengles.GL10;

public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int CRLF = 4;
    public static final int DEFAULT = 0;
    public static final int NO_CLOSE = 16;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;

    static abstract class Coder {
        public int op;
        public byte[] output;

        public abstract int maxOutputSize(int i);

        public abstract boolean process(byte[] bArr, int i, int i2, boolean z);

        Coder() {
        }
    }

    static class Decoder extends Coder {
        private static final int[] DECODE;
        private static final int[] DECODE_WEBSAFE;
        private static final int EQUALS = -2;
        private static final int SKIP = -1;
        private final int[] alphabet;
        private int state;
        private int value;

        static {
            DECODE = new int[]{SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, 62, SKIP, SKIP, SKIP, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, SKIP, SKIP, SKIP, EQUALS, SKIP, SKIP, SKIP, Base64.DEFAULT, Base64.NO_PADDING, Base64.NO_WRAP, 3, Base64.CRLF, 5, 6, 7, Base64.URL_SAFE, 9, 10, 11, 12, 13, 14, 15, Base64.NO_CLOSE, 17, 18, 19, 20, 21, 22, 23, 24, 25, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP};
            DECODE_WEBSAFE = new int[]{SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, 62, SKIP, SKIP, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, SKIP, SKIP, SKIP, EQUALS, SKIP, SKIP, SKIP, Base64.DEFAULT, Base64.NO_PADDING, Base64.NO_WRAP, 3, Base64.CRLF, 5, 6, 7, Base64.URL_SAFE, 9, 10, 11, 12, 13, 14, 15, Base64.NO_CLOSE, 17, 18, 19, 20, 21, 22, 23, 24, 25, SKIP, SKIP, SKIP, SKIP, 63, SKIP, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP};
        }

        public Decoder(int flags, byte[] output) {
            this.output = output;
            this.alphabet = (flags & Base64.URL_SAFE) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = Base64.DEFAULT;
            this.value = Base64.DEFAULT;
        }

        public int maxOutputSize(int len) {
            return ((len * 3) / Base64.CRLF) + 10;
        }

        public boolean process(byte[] input, int offset, int len, boolean finish) {
            if (this.state == 6) {
                return Base64.$assertionsDisabled;
            }
            int op;
            int p = offset;
            len += offset;
            int state = this.state;
            int value = this.value;
            int op2 = Base64.DEFAULT;
            byte[] output = this.output;
            int[] alphabet = this.alphabet;
            while (p < len) {
                if (state == 0) {
                    while (p + Base64.CRLF <= len) {
                        value = (((alphabet[input[p] & R.styleable.Theme_windowSharedElementReturnTransition] << 18) | (alphabet[input[p + Base64.NO_PADDING] & R.styleable.Theme_windowSharedElementReturnTransition] << 12)) | (alphabet[input[p + Base64.NO_WRAP] & R.styleable.Theme_windowSharedElementReturnTransition] << 6)) | alphabet[input[p + 3] & R.styleable.Theme_windowSharedElementReturnTransition];
                        if (value >= 0) {
                            output[op2 + Base64.NO_WRAP] = (byte) value;
                            output[op2 + Base64.NO_PADDING] = (byte) (value >> Base64.URL_SAFE);
                            output[op2] = (byte) (value >> Base64.NO_CLOSE);
                            op2 += 3;
                            p += Base64.CRLF;
                        } else if (p >= len) {
                            op = op2;
                            if (finish) {
                                switch (state) {
                                    case Base64.DEFAULT /*0*/:
                                        op2 = op;
                                        break;
                                    case Base64.NO_PADDING /*1*/:
                                        this.state = 6;
                                        return Base64.$assertionsDisabled;
                                    case Base64.NO_WRAP /*2*/:
                                        op2 = op + Base64.NO_PADDING;
                                        output[op] = (byte) (value >> Base64.CRLF);
                                        break;
                                    case GL10.GL_LINE_STRIP /*3*/:
                                        op2 = op + Base64.NO_PADDING;
                                        output[op] = (byte) (value >> 10);
                                        op = op2 + Base64.NO_PADDING;
                                        output[op2] = (byte) (value >> Base64.NO_WRAP);
                                        op2 = op;
                                        break;
                                    case Base64.CRLF /*4*/:
                                        this.state = 6;
                                        return Base64.$assertionsDisabled;
                                    default:
                                        op2 = op;
                                        break;
                                }
                                this.state = state;
                                this.op = op2;
                                return true;
                            }
                            this.state = state;
                            this.value = value;
                            this.op = op;
                            return true;
                        }
                    }
                    if (p >= len) {
                        op = op2;
                        if (finish) {
                            switch (state) {
                                case Base64.DEFAULT /*0*/:
                                    op2 = op;
                                    break;
                                case Base64.NO_PADDING /*1*/:
                                    this.state = 6;
                                    return Base64.$assertionsDisabled;
                                case Base64.NO_WRAP /*2*/:
                                    op2 = op + Base64.NO_PADDING;
                                    output[op] = (byte) (value >> Base64.CRLF);
                                    break;
                                case GL10.GL_LINE_STRIP /*3*/:
                                    op2 = op + Base64.NO_PADDING;
                                    output[op] = (byte) (value >> 10);
                                    op = op2 + Base64.NO_PADDING;
                                    output[op2] = (byte) (value >> Base64.NO_WRAP);
                                    op2 = op;
                                    break;
                                case Base64.CRLF /*4*/:
                                    this.state = 6;
                                    return Base64.$assertionsDisabled;
                                default:
                                    op2 = op;
                                    break;
                            }
                            this.state = state;
                            this.op = op2;
                            return true;
                        }
                        this.state = state;
                        this.value = value;
                        this.op = op;
                        return true;
                    }
                }
                int p2 = p + Base64.NO_PADDING;
                int d = alphabet[input[p] & R.styleable.Theme_windowSharedElementReturnTransition];
                switch (state) {
                    case Base64.DEFAULT /*0*/:
                        if (d < 0) {
                            if (d == SKIP) {
                                break;
                            }
                            this.state = 6;
                            return Base64.$assertionsDisabled;
                        }
                        value = d;
                        state += Base64.NO_PADDING;
                        break;
                    case Base64.NO_PADDING /*1*/:
                        if (d < 0) {
                            if (d == SKIP) {
                                break;
                            }
                            this.state = 6;
                            return Base64.$assertionsDisabled;
                        }
                        value = (value << 6) | d;
                        state += Base64.NO_PADDING;
                        break;
                    case Base64.NO_WRAP /*2*/:
                        if (d < 0) {
                            if (d != EQUALS) {
                                if (d == SKIP) {
                                    break;
                                }
                                this.state = 6;
                                return Base64.$assertionsDisabled;
                            }
                            op = op2 + Base64.NO_PADDING;
                            output[op2] = (byte) (value >> Base64.CRLF);
                            state = Base64.CRLF;
                            op2 = op;
                            break;
                        }
                        value = (value << 6) | d;
                        state += Base64.NO_PADDING;
                        break;
                    case GL10.GL_LINE_STRIP /*3*/:
                        if (d < 0) {
                            if (d != EQUALS) {
                                if (d == SKIP) {
                                    break;
                                }
                                this.state = 6;
                                return Base64.$assertionsDisabled;
                            }
                            output[op2 + Base64.NO_PADDING] = (byte) (value >> Base64.NO_WRAP);
                            output[op2] = (byte) (value >> 10);
                            op2 += Base64.NO_WRAP;
                            state = 5;
                            break;
                        }
                        value = (value << 6) | d;
                        output[op2 + Base64.NO_WRAP] = (byte) value;
                        output[op2 + Base64.NO_PADDING] = (byte) (value >> Base64.URL_SAFE);
                        output[op2] = (byte) (value >> Base64.NO_CLOSE);
                        op2 += 3;
                        state = Base64.DEFAULT;
                        break;
                    case Base64.CRLF /*4*/:
                        if (d != EQUALS) {
                            if (d == SKIP) {
                                break;
                            }
                            this.state = 6;
                            return Base64.$assertionsDisabled;
                        }
                        state += Base64.NO_PADDING;
                        break;
                    case GL10.GL_TRIANGLE_STRIP /*5*/:
                        if (d == SKIP) {
                            break;
                        }
                        this.state = 6;
                        return Base64.$assertionsDisabled;
                    default:
                        break;
                }
                p = p2;
            }
            op = op2;
            if (finish) {
                this.state = state;
                this.value = value;
                this.op = op;
                return true;
            }
            switch (state) {
                case Base64.DEFAULT /*0*/:
                    op2 = op;
                    break;
                case Base64.NO_PADDING /*1*/:
                    this.state = 6;
                    return Base64.$assertionsDisabled;
                case Base64.NO_WRAP /*2*/:
                    op2 = op + Base64.NO_PADDING;
                    output[op] = (byte) (value >> Base64.CRLF);
                    break;
                case GL10.GL_LINE_STRIP /*3*/:
                    op2 = op + Base64.NO_PADDING;
                    output[op] = (byte) (value >> 10);
                    op = op2 + Base64.NO_PADDING;
                    output[op2] = (byte) (value >> Base64.NO_WRAP);
                    op2 = op;
                    break;
                case Base64.CRLF /*4*/:
                    this.state = 6;
                    return Base64.$assertionsDisabled;
                default:
                    op2 = op;
                    break;
            }
            this.state = state;
            this.op = op2;
            return true;
        }
    }

    static class Encoder extends Coder {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final byte[] ENCODE;
        private static final byte[] ENCODE_WEBSAFE;
        public static final int LINE_GROUPS = 19;
        private final byte[] alphabet;
        private int count;
        public final boolean do_cr;
        public final boolean do_newline;
        public final boolean do_padding;
        private final byte[] tail;
        int tailLen;

        static {
            boolean z;
            if (Base64.class.desiredAssertionStatus()) {
                z = $assertionsDisabled;
            } else {
                z = true;
            }
            $assertionsDisabled = z;
            ENCODE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
            ENCODE_WEBSAFE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
        }

        public Encoder(int flags, byte[] output) {
            boolean z;
            boolean z2 = true;
            this.output = output;
            this.do_padding = (flags & Base64.NO_PADDING) == 0 ? true : $assertionsDisabled;
            if ((flags & Base64.NO_WRAP) == 0) {
                z = true;
            } else {
                z = $assertionsDisabled;
            }
            this.do_newline = z;
            if ((flags & Base64.CRLF) == 0) {
                z2 = $assertionsDisabled;
            }
            this.do_cr = z2;
            this.alphabet = (flags & Base64.URL_SAFE) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[Base64.NO_WRAP];
            this.tailLen = Base64.DEFAULT;
            this.count = this.do_newline ? LINE_GROUPS : -1;
        }

        public int maxOutputSize(int len) {
            return ((len * Base64.URL_SAFE) / 5) + 10;
        }

        public boolean process(byte[] r15, int r16, int r17, boolean r18) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:42)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:66)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r14 = this;
            r1 = r14.alphabet;
            r5 = r14.output;
            r3 = 0;
            r2 = r14.count;
            r6 = r16;
            r17 = r17 + r16;
            r10 = -1;
            r11 = r14.tailLen;
            switch(r11) {
                case 0: goto L_0x0011;
                case 1: goto L_0x00b0;
                case 2: goto L_0x00d5;
                default: goto L_0x0011;
            };
        L_0x0011:
            r11 = -1;
            if (r10 == r11) goto L_0x0247;
        L_0x0014:
            r4 = r3 + 1;
            r11 = r10 >> 18;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 >> 12;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r2 = r2 + -1;
            if (r2 != 0) goto L_0x0247;
        L_0x003e:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x0049;
        L_0x0042:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x0049:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            r2 = 19;
            r7 = r6;
        L_0x0052:
            r11 = r7 + 3;
            r0 = r17;
            if (r11 > r0) goto L_0x00fc;
        L_0x0058:
            r11 = r15[r7];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r12 = r7 + 1;
            r12 = r15[r12];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r12 = r7 + 2;
            r12 = r15[r12];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = r10 >> 18;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r11 = r4 + 1;
            r12 = r10 >> 12;
            r12 = r12 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r11 = r4 + 2;
            r12 = r10 >> 6;
            r12 = r12 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r11 = r4 + 3;
            r12 = r10 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r6 = r7 + 3;
            r3 = r4 + 4;
            r2 = r2 + -1;
            if (r2 != 0) goto L_0x0247;
        L_0x009b:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x00a6;
        L_0x009f:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x00a6:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            r2 = 19;
            r7 = r6;
            goto L_0x0052;
        L_0x00b0:
            r11 = r6 + 2;
            r0 = r17;
            if (r11 > r0) goto L_0x0011;
        L_0x00b6:
            r11 = r14.tail;
            r12 = 0;
            r11 = r11[r12];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r7 = r6 + 1;
            r12 = r15[r6];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r6 = r7 + 1;
            r12 = r15[r7];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = 0;
            r14.tailLen = r11;
            goto L_0x0011;
        L_0x00d5:
            r11 = r6 + 1;
            r0 = r17;
            if (r11 > r0) goto L_0x0011;
        L_0x00db:
            r11 = r14.tail;
            r12 = 0;
            r11 = r11[r12];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r12 = r14.tail;
            r13 = 1;
            r12 = r12[r13];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r7 = r6 + 1;
            r12 = r15[r6];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = 0;
            r14.tailLen = r11;
            r6 = r7;
            goto L_0x0011;
        L_0x00fc:
            if (r18 == 0) goto L_0x020c;
        L_0x00fe:
            r11 = r14.tailLen;
            r11 = r7 - r11;
            r12 = r17 + -1;
            if (r11 != r12) goto L_0x0168;
        L_0x0106:
            r8 = 0;
            r11 = r14.tailLen;
            if (r11 <= 0) goto L_0x0163;
        L_0x010b:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
            r6 = r7;
        L_0x0113:
            r11 = r11 & 255;
            r10 = r11 << 4;
            r11 = r14.tailLen;
            r11 = r11 - r8;
            r14.tailLen = r11;
            r3 = r4 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r11 = r14.do_padding;
            if (r11 == 0) goto L_0x013e;
        L_0x0132:
            r3 = r4 + 1;
            r11 = 61;
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = 61;
            r5[r3] = r11;
        L_0x013e:
            r3 = r4;
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x0155;
        L_0x0143:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x014e;
        L_0x0147:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x014e:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
        L_0x0154:
            r3 = r4;
        L_0x0155:
            r11 = $assertionsDisabled;
            if (r11 != 0) goto L_0x01fe;
        L_0x0159:
            r11 = r14.tailLen;
            if (r11 == 0) goto L_0x01fe;
        L_0x015d:
            r11 = new java.lang.AssertionError;
            r11.<init>();
            throw r11;
        L_0x0163:
            r6 = r7 + 1;
            r11 = r15[r7];
            goto L_0x0113;
        L_0x0168:
            r11 = r14.tailLen;
            r11 = r7 - r11;
            r12 = r17 + -2;
            if (r11 != r12) goto L_0x01e0;
        L_0x0170:
            r8 = 0;
            r11 = r14.tailLen;
            r12 = 1;
            if (r11 <= r12) goto L_0x01d5;
        L_0x0176:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
            r6 = r7;
        L_0x017e:
            r11 = r11 & 255;
            r12 = r11 << 10;
            r11 = r14.tailLen;
            if (r11 <= 0) goto L_0x01da;
        L_0x0186:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
        L_0x018d:
            r11 = r11 & 255;
            r11 = r11 << 2;
            r10 = r12 | r11;
            r11 = r14.tailLen;
            r11 = r11 - r8;
            r14.tailLen = r11;
            r3 = r4 + 1;
            r11 = r10 >> 12;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r11 = r14.do_padding;
            if (r11 == 0) goto L_0x01bf;
        L_0x01b8:
            r4 = r3 + 1;
            r11 = 61;
            r5[r3] = r11;
            r3 = r4;
        L_0x01bf:
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x0155;
        L_0x01c3:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x01ce;
        L_0x01c7:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x01ce:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            goto L_0x0154;
        L_0x01d5:
            r6 = r7 + 1;
            r11 = r15[r7];
            goto L_0x017e;
        L_0x01da:
            r7 = r6 + 1;
            r11 = r15[r6];
            r6 = r7;
            goto L_0x018d;
        L_0x01e0:
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x01fa;
        L_0x01e4:
            if (r4 <= 0) goto L_0x01fa;
        L_0x01e6:
            r11 = 19;
            if (r2 == r11) goto L_0x01fa;
        L_0x01ea:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x0245;
        L_0x01ee:
            r3 = r4 + 1;
            r11 = 13;
            r5[r4] = r11;
        L_0x01f4:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
        L_0x01fa:
            r6 = r7;
            r3 = r4;
            goto L_0x0155;
        L_0x01fe:
            r11 = $assertionsDisabled;
            if (r11 != 0) goto L_0x021e;
        L_0x0202:
            r0 = r17;
            if (r6 == r0) goto L_0x021e;
        L_0x0206:
            r11 = new java.lang.AssertionError;
            r11.<init>();
            throw r11;
        L_0x020c:
            r11 = r17 + -1;
            if (r7 != r11) goto L_0x0224;
        L_0x0210:
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r15[r7];
            r11[r12] = r13;
            r6 = r7;
            r3 = r4;
        L_0x021e:
            r14.op = r3;
            r14.count = r2;
            r11 = 1;
            return r11;
        L_0x0224:
            r11 = r17 + -2;
            if (r7 != r11) goto L_0x0242;
        L_0x0228:
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r15[r7];
            r11[r12] = r13;
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r7 + 1;
            r13 = r15[r13];
            r11[r12] = r13;
        L_0x0242:
            r6 = r7;
            r3 = r4;
            goto L_0x021e;
        L_0x0245:
            r3 = r4;
            goto L_0x01f4;
        L_0x0247:
            r7 = r6;
            r4 = r3;
            goto L_0x0052;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.util.Base64.Encoder.process(byte[], int, int, boolean):boolean");
        }
    }

    static {
        $assertionsDisabled = !Base64.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(input, DEFAULT, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        Decoder decoder = new Decoder(flags, new byte[((len * 3) / CRLF)]);
        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        } else if (decoder.op == decoder.output.length) {
            return decoder.output;
        } else {
            byte[] temp = new byte[decoder.op];
            System.arraycopy(decoder.output, DEFAULT, temp, DEFAULT, decoder.op);
            return temp;
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), StringPart.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(encode(input, offset, len, flags), StringPart.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, DEFAULT, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);
        int output_len = (len / 3) * CRLF;
        if (!encoder.do_padding) {
            switch (len % 3) {
                case DEFAULT /*0*/:
                    break;
                case NO_PADDING /*1*/:
                    output_len += NO_WRAP;
                    break;
                case NO_WRAP /*2*/:
                    output_len += 3;
                    break;
                default:
                    break;
            }
        } else if (len % 3 > 0) {
            output_len += CRLF;
        }
        if (encoder.do_newline && len > 0) {
            int i;
            int i2 = ((len - 1) / 57) + NO_PADDING;
            if (encoder.do_cr) {
                i = NO_WRAP;
            } else {
                i = NO_PADDING;
            }
            output_len += i * i2;
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        if ($assertionsDisabled || encoder.op == output_len) {
            return encoder.output;
        }
        throw new AssertionError();
    }

    private Base64() {
    }
}
