package java.math;

import dalvik.bytecode.Opcodes;
import java.net.HttpURLConnection;

class Primality {
    private static final BigInteger[] BIprimes;
    private static final int[] primes;

    static java.math.BigInteger nextProbablePrime(java.math.BigInteger r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.math.Primality.nextProbablePrime(java.math.BigInteger):java.math.BigInteger
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.math.Primality.nextProbablePrime(java.math.BigInteger):java.math.BigInteger
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.math.Primality.nextProbablePrime(java.math.BigInteger):java.math.BigInteger");
    }

    private Primality() {
    }

    static {
        primes = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, Opcodes.OP_SGET_CHAR, Opcodes.OP_SPUT, Opcodes.OP_SPUT_BYTE, Opcodes.OP_SPUT_SHORT, Opcodes.OP_INVOKE_STATIC, Float.MAX_EXPONENT, Opcodes.OP_INT_TO_DOUBLE, Opcodes.OP_FLOAT_TO_DOUBLE, Opcodes.OP_DOUBLE_TO_LONG, Opcodes.OP_AND_INT, Opcodes.OP_XOR_INT, Opcodes.OP_MUL_LONG, Opcodes.OP_SHL_LONG, Opcodes.OP_SUB_FLOAT, Opcodes.OP_MUL_DOUBLE, Opcodes.OP_DIV_INT_2ADDR, Opcodes.OP_AND_INT_2ADDR, Opcodes.OP_REM_LONG_2ADDR, Opcodes.OP_OR_LONG_2ADDR, Opcodes.OP_USHR_LONG_2ADDR, Opcodes.OP_SUB_FLOAT_2ADDR, Opcodes.OP_DIV_INT_LIT16, Opcodes.OP_XOR_INT_LIT8, 227, 229, Opcodes.OP_IPUT_WIDE_VOLATILE, Opcodes.OP_EXECUTE_INLINE_RANGE, 241, Opcodes.OP_INVOKE_SUPER_QUICK_RANGE, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_CONFLICT, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, HttpURLConnection.HTTP_UNAVAILABLE, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019, 1021};
        BIprimes = new BigInteger[primes.length];
        for (int i = 0; i < primes.length; i++) {
            BIprimes[i] = BigInteger.valueOf((long) primes[i]);
        }
    }
}
