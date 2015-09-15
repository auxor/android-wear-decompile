package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    static final int NCPUS;
    static final int maxTimedSpins;
    static final int maxUntimedSpins;
    private static final long serialVersionUID = -3223113410248163686L;
    static final long spinForTimeoutThreshold = 1000;
    private ReentrantLock qlock;
    private volatile transient Transferer<E> transferer;
    private WaitQueue waitingConsumers;
    private WaitQueue waitingProducers;

    private static class EmptyIterator<E> implements Iterator<E> {
        static final EmptyIterator<Object> EMPTY_ITERATOR;

        private EmptyIterator() {
        }

        static {
            EMPTY_ITERATOR = new EmptyIterator();
        }

        public boolean hasNext() {
            return false;
        }

        public E next() {
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new IllegalStateException();
        }
    }

    static class WaitQueue implements Serializable {
        WaitQueue() {
        }
    }

    static class FifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3623113410248163686L;

        FifoWaitQueue() {
        }
    }

    static class LifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3633113410248163686L;

        LifoWaitQueue() {
        }
    }

    static abstract class Transferer<E> {
        abstract E transfer(E e, boolean z, long j);

        Transferer() {
        }
    }

    static final class TransferQueue<E> extends Transferer<E> {
        private static final Unsafe UNSAFE;
        private static final long cleanMeOffset;
        private static final long headOffset;
        private static final long tailOffset;
        volatile transient QNode cleanMe;
        volatile transient QNode head;
        volatile transient QNode tail;

        static final class QNode {
            private static final Unsafe UNSAFE;
            private static final long itemOffset;
            private static final long nextOffset;
            final boolean isData;
            volatile Object item;
            volatile QNode next;
            volatile Thread waiter;

            QNode(java.lang.Object r1, boolean r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.<init>(java.lang.Object, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.<init>(java.lang.Object, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.<init>(java.lang.Object, boolean):void");
            }

            boolean casItem(java.lang.Object r1, java.lang.Object r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casItem(java.lang.Object, java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casItem(java.lang.Object, java.lang.Object):boolean
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casItem(java.lang.Object, java.lang.Object):boolean");
            }

            boolean casNext(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, java.util.concurrent.SynchronousQueue.TransferQueue.QNode r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casNext(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casNext(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.casNext(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean");
            }

            void tryCancel(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.tryCancel(java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.tryCancel(java.lang.Object):void
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.QNode.tryCancel(java.lang.Object):void");
            }

            boolean isCancelled() {
                return this.item == this;
            }

            boolean isOffList() {
                return this.next == this;
            }

            static {
                try {
                    UNSAFE = Unsafe.getUnsafe();
                    Class<?> k = QNode.class;
                    itemOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
                    nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
                } catch (Throwable e) {
                    throw new Error(e);
                }
            }
        }

        void advanceHead(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, java.util.concurrent.SynchronousQueue.TransferQueue.QNode r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.advanceHead(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.advanceHead(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.advanceHead(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void");
        }

        void advanceTail(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, java.util.concurrent.SynchronousQueue.TransferQueue.QNode r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.advanceTail(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.advanceTail(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.advanceTail(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void");
        }

        java.lang.Object awaitFulfill(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, E r2, boolean r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.awaitFulfill(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.awaitFulfill(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.awaitFulfill(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.lang.Object, boolean, long):java.lang.Object");
        }

        boolean casCleanMe(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, java.util.concurrent.SynchronousQueue.TransferQueue.QNode r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.casCleanMe(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.casCleanMe(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.casCleanMe(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):boolean");
        }

        void clean(java.util.concurrent.SynchronousQueue.TransferQueue.QNode r1, java.util.concurrent.SynchronousQueue.TransferQueue.QNode r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.clean(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.clean(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.clean(java.util.concurrent.SynchronousQueue$TransferQueue$QNode, java.util.concurrent.SynchronousQueue$TransferQueue$QNode):void");
        }

        E transfer(E r1, boolean r2, long r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.SynchronousQueue.TransferQueue.transfer(java.lang.Object, boolean, long):E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.SynchronousQueue.TransferQueue.transfer(java.lang.Object, boolean, long):E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferQueue.transfer(java.lang.Object, boolean, long):E");
        }

        TransferQueue() {
            QNode h = new QNode(null, false);
            this.head = h;
            this.tail = h;
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                Class<?> k = TransferQueue.class;
                headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
                tailOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
                cleanMeOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("cleanMe"));
            } catch (Throwable e) {
                throw new Error(e);
            }
        }
    }

    static final class TransferStack<E> extends Transferer<E> {
        static final int DATA = 1;
        static final int FULFILLING = 2;
        static final int REQUEST = 0;
        private static final Unsafe UNSAFE;
        private static final long headOffset;
        volatile SNode head;

        static final class SNode {
            private static final Unsafe UNSAFE;
            private static final long matchOffset;
            private static final long nextOffset;
            Object item;
            volatile SNode match;
            int mode;
            volatile SNode next;
            volatile Thread waiter;

            SNode(Object item) {
                this.item = item;
            }

            boolean casNext(SNode cmp, SNode val) {
                return cmp == this.next && UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            boolean tryMatch(SNode s) {
                if (this.match == null && UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
                    Thread w = this.waiter;
                    if (w == null) {
                        return true;
                    }
                    this.waiter = null;
                    LockSupport.unpark(w);
                    return true;
                }
                return this.match == s;
            }

            void tryCancel() {
                UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
            }

            boolean isCancelled() {
                return this.match == this;
            }

            static {
                try {
                    UNSAFE = Unsafe.getUnsafe();
                    Class<?> k = SNode.class;
                    matchOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("match"));
                    nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
                } catch (Throwable e) {
                    throw new Error(e);
                }
            }
        }

        TransferStack() {
        }

        static boolean isFulfilling(int m) {
            return (m & FULFILLING) != 0;
        }

        boolean casHead(SNode h, SNode nh) {
            return h == this.head && UNSAFE.compareAndSwapObject(this, headOffset, h, nh);
        }

        static SNode snode(SNode s, Object e, SNode next, int mode) {
            if (s == null) {
                s = new SNode(e);
            }
            s.mode = mode;
            s.next = next;
            return s;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        E transfer(E r10, boolean r11, long r12) {
            /*
            r9 = this;
            r5 = 0;
            r4 = 0;
            if (r10 != 0) goto L_0x0023;
        L_0x0004:
            r3 = 0;
        L_0x0005:
            r0 = r9.head;
            if (r0 == 0) goto L_0x000d;
        L_0x0009:
            r6 = r0.mode;
            if (r6 != r3) goto L_0x004e;
        L_0x000d:
            if (r11 == 0) goto L_0x0025;
        L_0x000f:
            r6 = 0;
            r6 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
            if (r6 > 0) goto L_0x0025;
        L_0x0015:
            if (r0 == 0) goto L_0x0038;
        L_0x0017:
            r6 = r0.isCancelled();
            if (r6 == 0) goto L_0x0038;
        L_0x001d:
            r6 = r0.next;
            r9.casHead(r0, r6);
            goto L_0x0005;
        L_0x0023:
            r3 = 1;
            goto L_0x0005;
        L_0x0025:
            r4 = snode(r4, r10, r0, r3);
            r6 = r9.casHead(r0, r4);
            if (r6 == 0) goto L_0x0005;
        L_0x002f:
            r1 = r9.awaitFulfill(r4, r11, r12);
            if (r1 != r4) goto L_0x0039;
        L_0x0035:
            r9.clean(r4);
        L_0x0038:
            return r5;
        L_0x0039:
            r0 = r9.head;
            if (r0 == 0) goto L_0x0046;
        L_0x003d:
            r5 = r0.next;
            if (r5 != r4) goto L_0x0046;
        L_0x0041:
            r5 = r4.next;
            r9.casHead(r0, r5);
        L_0x0046:
            if (r3 != 0) goto L_0x004b;
        L_0x0048:
            r5 = r1.item;
            goto L_0x0038;
        L_0x004b:
            r5 = r4.item;
            goto L_0x0038;
        L_0x004e:
            r6 = r0.mode;
            r6 = isFulfilling(r6);
            if (r6 != 0) goto L_0x008e;
        L_0x0056:
            r6 = r0.isCancelled();
            if (r6 == 0) goto L_0x0062;
        L_0x005c:
            r6 = r0.next;
            r9.casHead(r0, r6);
            goto L_0x0005;
        L_0x0062:
            r6 = r3 | 2;
            r4 = snode(r4, r10, r0, r6);
            r6 = r9.casHead(r0, r4);
            if (r6 == 0) goto L_0x0005;
        L_0x006e:
            r1 = r4.next;
            if (r1 != 0) goto L_0x0077;
        L_0x0072:
            r9.casHead(r4, r5);
            r4 = 0;
            goto L_0x0005;
        L_0x0077:
            r2 = r1.next;
            r6 = r1.tryMatch(r4);
            if (r6 == 0) goto L_0x008a;
        L_0x007f:
            r9.casHead(r4, r2);
            if (r3 != 0) goto L_0x0087;
        L_0x0084:
            r5 = r1.item;
            goto L_0x0038;
        L_0x0087:
            r5 = r4.item;
            goto L_0x0038;
        L_0x008a:
            r4.casNext(r1, r2);
            goto L_0x006e;
        L_0x008e:
            r1 = r0.next;
            if (r1 != 0) goto L_0x0097;
        L_0x0092:
            r9.casHead(r0, r5);
            goto L_0x0005;
        L_0x0097:
            r2 = r1.next;
            r6 = r1.tryMatch(r0);
            if (r6 == 0) goto L_0x00a4;
        L_0x009f:
            r9.casHead(r0, r2);
            goto L_0x0005;
        L_0x00a4:
            r0.casNext(r1, r2);
            goto L_0x0005;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferStack.transfer(java.lang.Object, boolean, long):E");
        }

        SNode awaitFulfill(SNode s, boolean timed, long nanos) {
            long deadline;
            if (timed) {
                deadline = System.nanoTime() + nanos;
            } else {
                deadline = 0;
            }
            Thread w = Thread.currentThread();
            int spins = shouldSpin(s) ? timed ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins : SynchronousQueue.maxUntimedSpins;
            while (true) {
                if (w.isInterrupted()) {
                    s.tryCancel();
                }
                SNode m = s.match;
                if (m != null) {
                    return m;
                }
                if (timed) {
                    nanos = deadline - System.nanoTime();
                    if (nanos <= 0) {
                        s.tryCancel();
                    }
                }
                if (spins > 0) {
                    spins = shouldSpin(s) ? spins - 1 : SynchronousQueue.maxUntimedSpins;
                } else if (s.waiter == null) {
                    s.waiter = w;
                } else if (!timed) {
                    LockSupport.park(this);
                } else if (nanos > SynchronousQueue.spinForTimeoutThreshold) {
                    LockSupport.parkNanos(this, nanos);
                }
            }
        }

        boolean shouldSpin(SNode s) {
            SNode h = this.head;
            return h == s || h == null || isFulfilling(h.mode);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void clean(java.util.concurrent.SynchronousQueue.TransferStack.SNode r5) {
            /*
            r4 = this;
            r3 = 0;
            r5.item = r3;
            r5.waiter = r3;
            r2 = r5.next;
            if (r2 == 0) goto L_0x0011;
        L_0x0009:
            r3 = r2.isCancelled();
            if (r3 == 0) goto L_0x0011;
        L_0x000f:
            r2 = r2.next;
        L_0x0011:
            r1 = r4.head;
            if (r1 == 0) goto L_0x0023;
        L_0x0015:
            if (r1 == r2) goto L_0x0023;
        L_0x0017:
            r3 = r1.isCancelled();
            if (r3 == 0) goto L_0x0023;
        L_0x001d:
            r3 = r1.next;
            r4.casHead(r1, r3);
            goto L_0x0011;
        L_0x0023:
            if (r1 == 0) goto L_0x0039;
        L_0x0025:
            if (r1 == r2) goto L_0x0039;
        L_0x0027:
            r0 = r1.next;
            if (r0 == 0) goto L_0x0037;
        L_0x002b:
            r3 = r0.isCancelled();
            if (r3 == 0) goto L_0x0037;
        L_0x0031:
            r3 = r0.next;
            r1.casNext(r0, r3);
            goto L_0x0023;
        L_0x0037:
            r1 = r0;
            goto L_0x0023;
        L_0x0039:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.SynchronousQueue.TransferStack.clean(java.util.concurrent.SynchronousQueue$TransferStack$SNode):void");
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                headOffset = UNSAFE.objectFieldOffset(TransferStack.class.getDeclaredField("head"));
            } catch (Throwable e) {
                throw new Error(e);
            }
        }
    }

    static {
        NCPUS = Runtime.getRuntime().availableProcessors();
        maxTimedSpins = NCPUS < 2 ? maxUntimedSpins : 32;
        maxUntimedSpins = maxTimedSpins * 16;
    }

    public SynchronousQueue() {
        this(false);
    }

    public SynchronousQueue(boolean fair) {
        this.transferer = fair ? new TransferQueue() : new TransferStack();
    }

    public void put(E e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        } else if (this.transferer.transfer(e, false, 0) == null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        } else if (this.transferer.transfer(e, true, unit.toNanos(timeout)) != null) {
            return true;
        } else {
            if (!Thread.interrupted()) {
                return false;
            }
            throw new InterruptedException();
        }
    }

    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        } else if (this.transferer.transfer(e, true, 0) != null) {
            return true;
        } else {
            return false;
        }
    }

    public E take() throws InterruptedException {
        E e = this.transferer.transfer(null, false, 0);
        if (e != null) {
            return e;
        }
        Thread.interrupted();
        throw new InterruptedException();
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E e = this.transferer.transfer(null, true, unit.toNanos(timeout));
        if (e != null || !Thread.interrupted()) {
            return e;
        }
        throw new InterruptedException();
    }

    public E poll() {
        return this.transferer.transfer(null, true, 0);
    }

    public boolean isEmpty() {
        return true;
    }

    public int size() {
        return maxUntimedSpins;
    }

    public int remainingCapacity() {
        return maxUntimedSpins;
    }

    public void clear() {
    }

    public boolean contains(Object o) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return c.isEmpty();
    }

    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    public E peek() {
        return null;
    }

    public Iterator<E> iterator() {
        return EmptyIterator.EMPTY_ITERATOR;
    }

    public Object[] toArray() {
        return new Object[maxUntimedSpins];
    }

    public <T> T[] toArray(T[] a) {
        if (a.length > 0) {
            a[maxUntimedSpins] = null;
        }
        return a;
    }

    public int drainTo(Collection<? super E> c) {
        if (c == null) {
            throw new NullPointerException();
        } else if (c == this) {
            throw new IllegalArgumentException();
        } else {
            int n = maxUntimedSpins;
            while (true) {
                E e = poll();
                if (e == null) {
                    return n;
                }
                c.add(e);
                n++;
            }
        }
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        } else if (c == this) {
            throw new IllegalArgumentException();
        } else {
            int n = maxUntimedSpins;
            while (n < maxElements) {
                E e = poll();
                if (e == null) {
                    break;
                }
                c.add(e);
                n++;
            }
            return n;
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        if (this.transferer instanceof TransferQueue) {
            this.qlock = new ReentrantLock(true);
            this.waitingProducers = new FifoWaitQueue();
            this.waitingConsumers = new FifoWaitQueue();
        } else {
            this.qlock = new ReentrantLock();
            this.waitingProducers = new LifoWaitQueue();
            this.waitingConsumers = new LifoWaitQueue();
        }
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if (this.waitingProducers instanceof FifoWaitQueue) {
            this.transferer = new TransferQueue();
        } else {
            this.transferer = new TransferStack();
        }
    }

    static long objectFieldOffset(Unsafe UNSAFE, String field, Class<?> klazz) {
        try {
            return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }
}
