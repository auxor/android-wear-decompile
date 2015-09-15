package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Queue<E>, Cloneable, Serializable {
    private static final long serialVersionUID = 876323262645176354L;
    transient int size;
    transient Link<E> voidLink;

    private static final class Link<ET> {
        ET data;
        Link<ET> next;
        Link<ET> previous;

        Link(ET o, Link<ET> p, Link<ET> n) {
            this.data = o;
            this.previous = p;
            this.next = n;
        }
    }

    private static final class LinkIterator<ET> implements ListIterator<ET> {
        int expectedModCount;
        Link<ET> lastLink;
        Link<ET> link;
        final LinkedList<ET> list;
        int pos;

        LinkIterator(LinkedList<ET> object, int location) {
            this.list = object;
            this.expectedModCount = this.list.modCount;
            if (location < 0 || location > this.list.size) {
                throw new IndexOutOfBoundsException();
            }
            this.link = this.list.voidLink;
            if (location < this.list.size / 2) {
                this.pos = -1;
                while (this.pos + 1 < location) {
                    this.link = this.link.next;
                    this.pos++;
                }
                return;
            }
            this.pos = this.list.size;
            while (this.pos >= location) {
                this.link = this.link.previous;
                this.pos--;
            }
        }

        public void add(ET object) {
            if (this.expectedModCount == this.list.modCount) {
                Link<ET> next = this.link.next;
                Link<ET> newLink = new Link(object, this.link, next);
                this.link.next = newLink;
                next.previous = newLink;
                this.link = newLink;
                this.lastLink = null;
                this.pos++;
                this.expectedModCount++;
                LinkedList linkedList = this.list;
                linkedList.size++;
                linkedList = this.list;
                linkedList.modCount++;
                return;
            }
            throw new ConcurrentModificationException();
        }

        public boolean hasNext() {
            return this.link.next != this.list.voidLink;
        }

        public boolean hasPrevious() {
            return this.link != this.list.voidLink;
        }

        public ET next() {
            if (this.expectedModCount == this.list.modCount) {
                Link<ET> next = this.link.next;
                if (next != this.list.voidLink) {
                    this.link = next;
                    this.lastLink = next;
                    this.pos++;
                    return this.link.data;
                }
                throw new NoSuchElementException();
            }
            throw new ConcurrentModificationException();
        }

        public int nextIndex() {
            return this.pos + 1;
        }

        public ET previous() {
            if (this.expectedModCount != this.list.modCount) {
                throw new ConcurrentModificationException();
            } else if (this.link != this.list.voidLink) {
                this.lastLink = this.link;
                this.link = this.link.previous;
                this.pos--;
                return this.lastLink.data;
            } else {
                throw new NoSuchElementException();
            }
        }

        public int previousIndex() {
            return this.pos;
        }

        public void remove() {
            if (this.expectedModCount != this.list.modCount) {
                throw new ConcurrentModificationException();
            } else if (this.lastLink != null) {
                Link<ET> next = this.lastLink.next;
                Link<ET> previous = this.lastLink.previous;
                next.previous = previous;
                previous.next = next;
                if (this.lastLink == this.link) {
                    this.pos--;
                }
                this.link = previous;
                this.lastLink = null;
                this.expectedModCount++;
                LinkedList linkedList = this.list;
                linkedList.size--;
                linkedList = this.list;
                linkedList.modCount++;
            } else {
                throw new IllegalStateException();
            }
        }

        public void set(ET object) {
            if (this.expectedModCount != this.list.modCount) {
                throw new ConcurrentModificationException();
            } else if (this.lastLink != null) {
                this.lastLink.data = object;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private class ReverseLinkIterator<ET> implements Iterator<ET> {
        private boolean canRemove;
        private int expectedModCount;
        private Link<ET> link;
        private final LinkedList<ET> list;
        final /* synthetic */ LinkedList this$0;

        ReverseLinkIterator(java.util.LinkedList r1, java.util.LinkedList<ET> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.LinkedList.ReverseLinkIterator.<init>(java.util.LinkedList, java.util.LinkedList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.LinkedList.ReverseLinkIterator.<init>(java.util.LinkedList, java.util.LinkedList):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.LinkedList.ReverseLinkIterator.<init>(java.util.LinkedList, java.util.LinkedList):void");
        }

        public boolean hasNext() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.LinkedList.ReverseLinkIterator.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.LinkedList.ReverseLinkIterator.hasNext():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.LinkedList.ReverseLinkIterator.hasNext():boolean");
        }

        public ET next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.LinkedList.ReverseLinkIterator.next():ET
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.LinkedList.ReverseLinkIterator.next():ET
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.LinkedList.ReverseLinkIterator.next():ET");
        }

        public void remove() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.LinkedList.ReverseLinkIterator.remove():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.LinkedList.ReverseLinkIterator.remove():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.LinkedList.ReverseLinkIterator.remove():void");
        }
    }

    public LinkedList() {
        this.size = 0;
        this.voidLink = new Link(null, null, null);
        this.voidLink.previous = this.voidLink;
        this.voidLink.next = this.voidLink;
    }

    public LinkedList(Collection<? extends E> collection) {
        this();
        addAll(collection);
    }

    public void add(int location, E object) {
        if (location < 0 || location > this.size) {
            throw new IndexOutOfBoundsException();
        }
        Link<E> link = this.voidLink;
        int i;
        if (location < this.size / 2) {
            for (i = 0; i <= location; i++) {
                link = link.next;
            }
        } else {
            for (i = this.size; i > location; i--) {
                link = link.previous;
            }
        }
        Link<E> previous = link.previous;
        Link<E> newLink = new Link(object, previous, link);
        previous.next = newLink;
        link.previous = newLink;
        this.size++;
        this.modCount++;
    }

    public boolean add(E object) {
        return addLastImpl(object);
    }

    private boolean addLastImpl(E object) {
        Link<E> oldLast = this.voidLink.previous;
        Link<E> newLink = new Link(object, oldLast, this.voidLink);
        this.voidLink.previous = newLink;
        oldLast.next = newLink;
        this.size++;
        this.modCount++;
        return true;
    }

    public boolean addAll(int location, Collection<? extends E> collection) {
        if (location < 0 || location > this.size) {
            throw new IndexOutOfBoundsException();
        }
        int adding = collection.size();
        if (adding == 0) {
            return false;
        }
        Collection<? extends E> elements;
        if (collection == this) {
            elements = new ArrayList((Collection) collection);
        } else {
            elements = collection;
        }
        Link<E> previous = this.voidLink;
        int i;
        if (location < this.size / 2) {
            for (i = 0; i < location; i++) {
                previous = previous.next;
            }
        } else {
            for (i = this.size; i >= location; i--) {
                previous = previous.previous;
            }
        }
        Link<E> next = previous.next;
        for (E e : elements) {
            Link<E> newLink = new Link(e, previous, null);
            previous.next = newLink;
            previous = newLink;
        }
        previous.next = next;
        next.previous = previous;
        this.size += adding;
        this.modCount++;
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        int adding = collection.size();
        if (adding == 0) {
            return false;
        }
        Collection<? extends E> elements;
        if (collection == this) {
            elements = new ArrayList((Collection) collection);
        } else {
            elements = collection;
        }
        Link<E> previous = this.voidLink.previous;
        for (E e : elements) {
            Link<E> newLink = new Link(e, previous, null);
            previous.next = newLink;
            previous = newLink;
        }
        previous.next = this.voidLink;
        this.voidLink.previous = previous;
        this.size += adding;
        this.modCount++;
        return true;
    }

    public void addFirst(E object) {
        addFirstImpl(object);
    }

    private boolean addFirstImpl(E object) {
        Link<E> oldFirst = this.voidLink.next;
        Link<E> newLink = new Link(object, this.voidLink, oldFirst);
        this.voidLink.next = newLink;
        oldFirst.previous = newLink;
        this.size++;
        this.modCount++;
        return true;
    }

    public void addLast(E object) {
        addLastImpl(object);
    }

    public void clear() {
        if (this.size > 0) {
            this.size = 0;
            this.voidLink.next = this.voidLink;
            this.voidLink.previous = this.voidLink;
            this.modCount++;
        }
    }

    public Object clone() {
        try {
            LinkedList<E> l = (LinkedList) super.clone();
            l.size = 0;
            l.voidLink = new Link(null, null, null);
            l.voidLink.previous = l.voidLink;
            l.voidLink.next = l.voidLink;
            l.addAll(this);
            return l;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public boolean contains(Object object) {
        Link<E> link = this.voidLink.next;
        if (object != null) {
            while (link != this.voidLink) {
                if (object.equals(link.data)) {
                    return true;
                }
                link = link.next;
            }
        } else {
            while (link != this.voidLink) {
                if (link.data == null) {
                    return true;
                }
                link = link.next;
            }
        }
        return false;
    }

    public E get(int location) {
        if (location < 0 || location >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        Link<E> link = this.voidLink;
        int i;
        if (location < this.size / 2) {
            for (i = 0; i <= location; i++) {
                link = link.next;
            }
        } else {
            for (i = this.size; i > location; i--) {
                link = link.previous;
            }
        }
        return link.data;
    }

    public E getFirst() {
        return getFirstImpl();
    }

    private E getFirstImpl() {
        Link<E> first = this.voidLink.next;
        if (first != this.voidLink) {
            return first.data;
        }
        throw new NoSuchElementException();
    }

    public E getLast() {
        Link<E> last = this.voidLink.previous;
        if (last != this.voidLink) {
            return last.data;
        }
        throw new NoSuchElementException();
    }

    public int indexOf(Object object) {
        int pos = 0;
        Link<E> link = this.voidLink.next;
        if (object != null) {
            while (link != this.voidLink) {
                if (object.equals(link.data)) {
                    return pos;
                }
                link = link.next;
                pos++;
            }
        } else {
            while (link != this.voidLink) {
                if (link.data == null) {
                    return pos;
                }
                link = link.next;
                pos++;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object object) {
        int pos = this.size;
        Link<E> link = this.voidLink.previous;
        if (object != null) {
            while (link != this.voidLink) {
                pos--;
                if (object.equals(link.data)) {
                    return pos;
                }
                link = link.previous;
            }
        } else {
            while (link != this.voidLink) {
                pos--;
                if (link.data == null) {
                    return pos;
                }
                link = link.previous;
            }
        }
        return -1;
    }

    public ListIterator<E> listIterator(int location) {
        return new LinkIterator(this, location);
    }

    public E remove(int location) {
        if (location < 0 || location >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        Link<E> link = this.voidLink;
        int i;
        if (location < this.size / 2) {
            for (i = 0; i <= location; i++) {
                link = link.next;
            }
        } else {
            for (i = this.size; i > location; i--) {
                link = link.previous;
            }
        }
        Link<E> previous = link.previous;
        Link<E> next = link.next;
        previous.next = next;
        next.previous = previous;
        this.size--;
        this.modCount++;
        return link.data;
    }

    public boolean remove(Object object) {
        return removeFirstOccurrenceImpl(object);
    }

    public E removeFirst() {
        return removeFirstImpl();
    }

    private E removeFirstImpl() {
        Link<E> first = this.voidLink.next;
        if (first != this.voidLink) {
            Link<E> next = first.next;
            this.voidLink.next = next;
            next.previous = this.voidLink;
            this.size--;
            this.modCount++;
            return first.data;
        }
        throw new NoSuchElementException();
    }

    public E removeLast() {
        return removeLastImpl();
    }

    private E removeLastImpl() {
        Link<E> last = this.voidLink.previous;
        if (last != this.voidLink) {
            Link<E> previous = last.previous;
            this.voidLink.previous = previous;
            previous.next = this.voidLink;
            this.size--;
            this.modCount++;
            return last.data;
        }
        throw new NoSuchElementException();
    }

    public Iterator<E> descendingIterator() {
        return new ReverseLinkIterator(this, this);
    }

    public boolean offerFirst(E e) {
        return addFirstImpl(e);
    }

    public boolean offerLast(E e) {
        return addLastImpl(e);
    }

    public E peekFirst() {
        return peekFirstImpl();
    }

    public E peekLast() {
        Link<E> last = this.voidLink.previous;
        return last == this.voidLink ? null : last.data;
    }

    public E pollFirst() {
        return this.size == 0 ? null : removeFirstImpl();
    }

    public E pollLast() {
        return this.size == 0 ? null : removeLastImpl();
    }

    public E pop() {
        return removeFirstImpl();
    }

    public void push(E e) {
        addFirstImpl(e);
    }

    public boolean removeFirstOccurrence(Object o) {
        return removeFirstOccurrenceImpl(o);
    }

    public boolean removeLastOccurrence(Object o) {
        return removeOneOccurrence(o, new ReverseLinkIterator(this, this));
    }

    private boolean removeFirstOccurrenceImpl(Object o) {
        return removeOneOccurrence(o, new LinkIterator(this, 0));
    }

    private boolean removeOneOccurrence(Object o, Iterator<E> iter) {
        while (iter.hasNext()) {
            E element = iter.next();
            if (o == null) {
                if (element == null) {
                }
            } else if (o.equals(element)) {
            }
            iter.remove();
            return true;
        }
        return false;
    }

    public E set(int location, E object) {
        if (location < 0 || location >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        Link<E> link = this.voidLink;
        int i;
        if (location < this.size / 2) {
            for (i = 0; i <= location; i++) {
                link = link.next;
            }
        } else {
            for (i = this.size; i > location; i--) {
                link = link.previous;
            }
        }
        E result = link.data;
        link.data = object;
        return result;
    }

    public int size() {
        return this.size;
    }

    public boolean offer(E o) {
        return addLastImpl(o);
    }

    public E poll() {
        return this.size == 0 ? null : removeFirst();
    }

    public E remove() {
        return removeFirstImpl();
    }

    public E peek() {
        return peekFirstImpl();
    }

    private E peekFirstImpl() {
        Link<E> first = this.voidLink.next;
        return first == this.voidLink ? null : first.data;
    }

    public E element() {
        return getFirstImpl();
    }

    public Object[] toArray() {
        int index = 0;
        Object[] contents = new Object[this.size];
        Link<E> link = this.voidLink.next;
        while (link != this.voidLink) {
            int index2 = index + 1;
            contents[index] = link.data;
            link = link.next;
            index = index2;
        }
        return contents;
    }

    public <T> T[] toArray(T[] contents) {
        int index = 0;
        if (this.size > contents.length) {
            contents = (Object[]) ((Object[]) Array.newInstance(contents.getClass().getComponentType(), this.size));
        }
        Link<E> link = this.voidLink.next;
        while (link != this.voidLink) {
            int index2 = index + 1;
            contents[index] = link.data;
            link = link.next;
            index = index2;
        }
        if (index < contents.length) {
            contents[index] = null;
        }
        return contents;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.size);
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            stream.writeObject(it.next());
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.size = stream.readInt();
        this.voidLink = new Link(null, null, null);
        Link<E> link = this.voidLink;
        int i = this.size;
        while (true) {
            i--;
            if (i >= 0) {
                Link<E> nextLink = new Link(stream.readObject(), link, null);
                link.next = nextLink;
                link = nextLink;
            } else {
                link.next = this.voidLink;
                this.voidLink.previous = link;
                return;
            }
        }
    }
}
