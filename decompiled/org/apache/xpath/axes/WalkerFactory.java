package org.apache.xpath.axes;

import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.OpCodes;

public class WalkerFactory {
    public static final int BITMASK_TRAVERSES_OUTSIDE_SUBTREE = 234381312;
    public static final int BITS_COUNT = 255;
    public static final int BITS_RESERVED = 3840;
    public static final int BIT_ANCESTOR = 8192;
    public static final int BIT_ANCESTOR_OR_SELF = 16384;
    public static final int BIT_ANY_DESCENDANT_FROM_ROOT = 536870912;
    public static final int BIT_ATTRIBUTE = 32768;
    public static final int BIT_BACKWARDS_SELF = 268435456;
    public static final int BIT_CHILD = 65536;
    public static final int BIT_DESCENDANT = 131072;
    public static final int BIT_DESCENDANT_OR_SELF = 262144;
    public static final int BIT_FILTER = 67108864;
    public static final int BIT_FOLLOWING = 524288;
    public static final int BIT_FOLLOWING_SIBLING = 1048576;
    public static final int BIT_MATCH_PATTERN = Integer.MIN_VALUE;
    public static final int BIT_NAMESPACE = 2097152;
    public static final int BIT_NODETEST_ANY = 1073741824;
    public static final int BIT_PARENT = 4194304;
    public static final int BIT_PRECEDING = 8388608;
    public static final int BIT_PRECEDING_SIBLING = 16777216;
    public static final int BIT_PREDICATE = 4096;
    public static final int BIT_ROOT = 134217728;
    public static final int BIT_SELF = 33554432;
    static final boolean DEBUG_ITERATOR_CREATION = false;
    static final boolean DEBUG_PATTERN_CREATION = false;
    static final boolean DEBUG_WALKER_CREATION = false;

    private static int analyze(org.apache.xpath.compiler.Compiler r1, int r2, int r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.analyze(org.apache.xpath.compiler.Compiler, int, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.analyze(org.apache.xpath.compiler.Compiler, int, int):int
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.analyze(org.apache.xpath.compiler.Compiler, int, int):int");
    }

    static boolean analyzePredicate(org.apache.xpath.compiler.Compiler r1, int r2, int r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.analyzePredicate(org.apache.xpath.compiler.Compiler, int, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.analyzePredicate(org.apache.xpath.compiler.Compiler, int, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.analyzePredicate(org.apache.xpath.compiler.Compiler, int, int):boolean");
    }

    private static org.apache.xpath.patterns.StepPattern createDefaultStepPattern(org.apache.xpath.compiler.Compiler r1, int r2, org.apache.xpath.axes.MatchPatternIterator r3, int r4, org.apache.xpath.patterns.StepPattern r5, org.apache.xpath.patterns.StepPattern r6) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.createDefaultStepPattern(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.MatchPatternIterator, int, org.apache.xpath.patterns.StepPattern, org.apache.xpath.patterns.StepPattern):org.apache.xpath.patterns.StepPattern
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.createDefaultStepPattern(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.MatchPatternIterator, int, org.apache.xpath.patterns.StepPattern, org.apache.xpath.patterns.StepPattern):org.apache.xpath.patterns.StepPattern
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.createDefaultStepPattern(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.MatchPatternIterator, int, org.apache.xpath.patterns.StepPattern, org.apache.xpath.patterns.StepPattern):org.apache.xpath.patterns.StepPattern");
    }

    private static org.apache.xpath.axes.AxesWalker createDefaultWalker(org.apache.xpath.compiler.Compiler r1, int r2, org.apache.xpath.axes.WalkingIterator r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.createDefaultWalker(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.WalkingIterator, int):org.apache.xpath.axes.AxesWalker
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.createDefaultWalker(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.WalkingIterator, int):org.apache.xpath.axes.AxesWalker
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.createDefaultWalker(org.apache.xpath.compiler.Compiler, int, org.apache.xpath.axes.WalkingIterator, int):org.apache.xpath.axes.AxesWalker");
    }

    public static void diagnoseIterator(java.lang.String r1, int r2, org.apache.xpath.compiler.Compiler r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.diagnoseIterator(java.lang.String, int, org.apache.xpath.compiler.Compiler):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.diagnoseIterator(java.lang.String, int, org.apache.xpath.compiler.Compiler):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.diagnoseIterator(java.lang.String, int, org.apache.xpath.compiler.Compiler):void");
    }

    static boolean functionProximateOrContainsProximate(org.apache.xpath.compiler.Compiler r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.functionProximateOrContainsProximate(org.apache.xpath.compiler.Compiler, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.functionProximateOrContainsProximate(org.apache.xpath.compiler.Compiler, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.functionProximateOrContainsProximate(org.apache.xpath.compiler.Compiler, int):boolean");
    }

    public static java.lang.String getAnalysisString(int r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.getAnalysisString(int):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.getAnalysisString(int):java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.getAnalysisString(int):java.lang.String");
    }

    public static int getAxisFromStep(org.apache.xpath.compiler.Compiler r1, int r2) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.getAxisFromStep(org.apache.xpath.compiler.Compiler, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.getAxisFromStep(org.apache.xpath.compiler.Compiler, int):int
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.getAxisFromStep(org.apache.xpath.compiler.Compiler, int):int");
    }

    private static boolean isNaturalDocOrder(org.apache.xpath.compiler.Compiler r1, int r2, int r3, int r4) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.isNaturalDocOrder(org.apache.xpath.compiler.Compiler, int, int, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.isNaturalDocOrder(org.apache.xpath.compiler.Compiler, int, int, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.isNaturalDocOrder(org.apache.xpath.compiler.Compiler, int, int, int):boolean");
    }

    private static boolean isOptimizableForDescendantIterator(org.apache.xpath.compiler.Compiler r1, int r2, int r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.isOptimizableForDescendantIterator(org.apache.xpath.compiler.Compiler, int, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.isOptimizableForDescendantIterator(org.apache.xpath.compiler.Compiler, int, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.isOptimizableForDescendantIterator(org.apache.xpath.compiler.Compiler, int, int):boolean");
    }

    static boolean isProximateInnerExpr(org.apache.xpath.compiler.Compiler r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.isProximateInnerExpr(org.apache.xpath.compiler.Compiler, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.isProximateInnerExpr(org.apache.xpath.compiler.Compiler, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.isProximateInnerExpr(org.apache.xpath.compiler.Compiler, int):boolean");
    }

    static org.apache.xpath.axes.AxesWalker loadOneWalker(org.apache.xpath.axes.WalkingIterator r1, org.apache.xpath.compiler.Compiler r2, int r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.loadOneWalker(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int):org.apache.xpath.axes.AxesWalker
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.loadOneWalker(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int):org.apache.xpath.axes.AxesWalker
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.loadOneWalker(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int):org.apache.xpath.axes.AxesWalker");
    }

    static org.apache.xpath.patterns.StepPattern loadSteps(org.apache.xpath.axes.MatchPatternIterator r1, org.apache.xpath.compiler.Compiler r2, int r3, int r4) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.loadSteps(org.apache.xpath.axes.MatchPatternIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.patterns.StepPattern
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.loadSteps(org.apache.xpath.axes.MatchPatternIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.patterns.StepPattern
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.loadSteps(org.apache.xpath.axes.MatchPatternIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.patterns.StepPattern");
    }

    static org.apache.xpath.axes.AxesWalker loadWalkers(org.apache.xpath.axes.WalkingIterator r1, org.apache.xpath.compiler.Compiler r2, int r3, int r4) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.loadWalkers(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.axes.AxesWalker
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.loadWalkers(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.axes.AxesWalker
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.loadWalkers(org.apache.xpath.axes.WalkingIterator, org.apache.xpath.compiler.Compiler, int, int):org.apache.xpath.axes.AxesWalker");
    }

    public static boolean mightBeProximate(org.apache.xpath.compiler.Compiler r1, int r2, int r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.mightBeProximate(org.apache.xpath.compiler.Compiler, int, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.mightBeProximate(org.apache.xpath.compiler.Compiler, int, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.mightBeProximate(org.apache.xpath.compiler.Compiler, int, int):boolean");
    }

    public static org.apache.xml.dtm.DTMIterator newDTMIterator(org.apache.xpath.compiler.Compiler r1, int r2, boolean r3) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.axes.WalkerFactory.newDTMIterator(org.apache.xpath.compiler.Compiler, int, boolean):org.apache.xml.dtm.DTMIterator
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.axes.WalkerFactory.newDTMIterator(org.apache.xpath.compiler.Compiler, int, boolean):org.apache.xml.dtm.DTMIterator
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.axes.WalkerFactory.newDTMIterator(org.apache.xpath.compiler.Compiler, int, boolean):org.apache.xml.dtm.DTMIterator");
    }

    public static boolean isSet(int analysis, int bits) {
        return (analysis & bits) != 0;
    }

    public static int getAnalysisBitFromAxes(int axis) {
        switch (axis) {
            case FunctionTable.FUNC_CURRENT /*0*/:
                return BIT_ANCESTOR;
            case OpCodes.OP_XPATH /*1*/:
                return BIT_ANCESTOR_OR_SELF;
            case OpCodes.OP_OR /*2*/:
                return BIT_ATTRIBUTE;
            case OpCodes.OP_AND /*3*/:
                return BIT_CHILD;
            case OpCodes.OP_NOTEQUALS /*4*/:
                return BIT_DESCENDANT;
            case OpCodes.OP_EQUALS /*5*/:
            case OpCodes.OP_MOD /*14*/:
                return BIT_DESCENDANT_OR_SELF;
            case OpCodes.OP_LTE /*6*/:
                return BIT_FOLLOWING;
            case OpCodes.OP_LT /*7*/:
                return BIT_FOLLOWING_SIBLING;
            case OpCodes.OP_GTE /*8*/:
            case OpCodes.OP_GT /*9*/:
                return BIT_NAMESPACE;
            case OpCodes.OP_PLUS /*10*/:
                return BIT_PARENT;
            case OpCodes.OP_MINUS /*11*/:
                return BIT_PRECEDING;
            case OpCodes.OP_MULT /*12*/:
                return BIT_PRECEDING_SIBLING;
            case OpCodes.OP_DIV /*13*/:
                return BIT_SELF;
            case OpCodes.OP_NEG /*16*/:
            case OpCodes.OP_STRING /*17*/:
            case OpCodes.OP_BOOL /*18*/:
                return BIT_ANY_DESCENDANT_FROM_ROOT;
            case OpCodes.OP_NUMBER /*19*/:
                return BIT_ROOT;
            case OpCodes.OP_UNION /*20*/:
                return BIT_FILTER;
            default:
                return BIT_FILTER;
        }
    }

    public static boolean isDownwardAxisOfMany(int axis) {
        return 5 == axis || 4 == axis || 6 == axis || 11 == axis;
    }

    public static boolean hasPredicate(int analysis) {
        return (analysis & BIT_PREDICATE) != 0;
    }

    public static boolean isWild(int analysis) {
        return (BIT_NODETEST_ANY & analysis) != 0;
    }

    public static boolean walksAncestors(int analysis) {
        return isSet(analysis, 24576);
    }

    public static boolean walksAttributes(int analysis) {
        return (BIT_ATTRIBUTE & analysis) != 0;
    }

    public static boolean walksNamespaces(int analysis) {
        return (BIT_NAMESPACE & analysis) != 0;
    }

    public static boolean walksChildren(int analysis) {
        return (BIT_CHILD & analysis) != 0;
    }

    public static boolean walksDescendants(int analysis) {
        return isSet(analysis, 393216);
    }

    public static boolean walksSubtree(int analysis) {
        return isSet(analysis, 458752);
    }

    public static boolean walksSubtreeOnlyMaybeAbsolute(int analysis) {
        return (!walksSubtree(analysis) || walksExtraNodes(analysis) || walksUp(analysis) || walksSideways(analysis)) ? false : true;
    }

    public static boolean walksSubtreeOnly(int analysis) {
        return walksSubtreeOnlyMaybeAbsolute(analysis) && !isAbsolute(analysis);
    }

    public static boolean walksFilteredList(int analysis) {
        return isSet(analysis, BIT_FILTER);
    }

    public static boolean walksSubtreeOnlyFromRootOrContext(int analysis) {
        return (!walksSubtree(analysis) || walksExtraNodes(analysis) || walksUp(analysis) || walksSideways(analysis) || isSet(analysis, BIT_FILTER)) ? false : true;
    }

    public static boolean walksInDocOrder(int analysis) {
        return (walksSubtreeOnlyMaybeAbsolute(analysis) || walksExtraNodesOnly(analysis) || walksFollowingOnlyMaybeAbsolute(analysis)) && !isSet(analysis, BIT_FILTER);
    }

    public static boolean walksFollowingOnlyMaybeAbsolute(int analysis) {
        return (!isSet(analysis, 35127296) || walksSubtree(analysis) || walksUp(analysis) || walksSideways(analysis)) ? false : true;
    }

    public static boolean walksUp(int analysis) {
        return isSet(analysis, 4218880);
    }

    public static boolean walksSideways(int analysis) {
        return isSet(analysis, 26738688);
    }

    public static boolean walksExtraNodes(int analysis) {
        return isSet(analysis, 2129920);
    }

    public static boolean walksExtraNodesOnly(int analysis) {
        return (!walksExtraNodes(analysis) || isSet(analysis, BIT_SELF) || walksSubtree(analysis) || walksUp(analysis) || walksSideways(analysis) || isAbsolute(analysis)) ? false : true;
    }

    public static boolean isAbsolute(int analysis) {
        return isSet(analysis, 201326592);
    }

    public static boolean walksChildrenOnly(int analysis) {
        return (!walksChildren(analysis) || isSet(analysis, BIT_SELF) || walksExtraNodes(analysis) || walksDescendants(analysis) || walksUp(analysis) || walksSideways(analysis) || (isAbsolute(analysis) && !isSet(analysis, BIT_ROOT))) ? false : true;
    }

    public static boolean walksChildrenAndExtraAndSelfOnly(int analysis) {
        return (!walksChildren(analysis) || walksDescendants(analysis) || walksUp(analysis) || walksSideways(analysis) || (isAbsolute(analysis) && !isSet(analysis, BIT_ROOT))) ? false : true;
    }

    public static boolean walksDescendantsAndExtraAndSelfOnly(int analysis) {
        return (walksChildren(analysis) || !walksDescendants(analysis) || walksUp(analysis) || walksSideways(analysis) || (isAbsolute(analysis) && !isSet(analysis, BIT_ROOT))) ? false : true;
    }

    public static boolean walksSelfOnly(int analysis) {
        return (!isSet(analysis, BIT_SELF) || walksSubtree(analysis) || walksUp(analysis) || walksSideways(analysis) || isAbsolute(analysis)) ? false : true;
    }

    public static boolean walksUpOnly(int analysis) {
        return (walksSubtree(analysis) || !walksUp(analysis) || walksSideways(analysis) || isAbsolute(analysis)) ? false : true;
    }

    public static boolean walksDownOnly(int analysis) {
        return (!walksSubtree(analysis) || walksUp(analysis) || walksSideways(analysis) || isAbsolute(analysis)) ? false : true;
    }

    public static boolean walksDownExtraOnly(int analysis) {
        return (!walksSubtree(analysis) || !walksExtraNodes(analysis) || walksUp(analysis) || walksSideways(analysis) || isAbsolute(analysis)) ? false : true;
    }

    public static boolean canSkipSubtrees(int analysis) {
        return isSet(analysis, BIT_CHILD) | walksSideways(analysis);
    }

    public static boolean canCrissCross(int analysis) {
        if (walksSelfOnly(analysis)) {
            return false;
        }
        if ((walksDownOnly(analysis) && !canSkipSubtrees(analysis)) || walksChildrenAndExtraAndSelfOnly(analysis) || walksDescendantsAndExtraAndSelfOnly(analysis) || walksUpOnly(analysis) || walksExtraNodesOnly(analysis) || !walksSubtree(analysis)) {
            return false;
        }
        if (walksSideways(analysis) || walksUp(analysis) || canSkipSubtrees(analysis)) {
            return true;
        }
        return false;
    }

    public static boolean isNaturalDocOrder(int analysis) {
        if (canCrissCross(analysis) || isSet(analysis, BIT_NAMESPACE) || walksFilteredList(analysis) || !walksInDocOrder(analysis)) {
            return false;
        }
        return true;
    }

    public static boolean isOneStep(int analysis) {
        return (analysis & BITS_COUNT) == 1;
    }

    public static int getStepCount(int analysis) {
        return analysis & BITS_COUNT;
    }
}
