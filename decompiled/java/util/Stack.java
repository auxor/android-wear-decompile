package java.util;

public class Stack<E> extends Vector<E> {
    private static final long serialVersionUID = 1224463164541339165L;

    public boolean empty() {
        return isEmpty();
    }

    public synchronized E peek() {
        try {
        } catch (IndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
        return this.elementData[this.elementCount - 1];
    }

    public synchronized E pop() {
        E obj;
        if (this.elementCount == 0) {
            throw new EmptyStackException();
        }
        int index = this.elementCount - 1;
        this.elementCount = index;
        obj = this.elementData[index];
        this.elementData[index] = null;
        this.modCount++;
        return obj;
    }

    public E push(E object) {
        addElement(object);
        return object;
    }

    public synchronized int search(Object o) {
        int i;
        Object[] dumpArray = this.elementData;
        int size = this.elementCount;
        int i2;
        if (o != null) {
            for (i2 = size - 1; i2 >= 0; i2--) {
                if (o.equals(dumpArray[i2])) {
                    i = size - i2;
                    break;
                }
            }
            i = -1;
        } else {
            for (i2 = size - 1; i2 >= 0; i2--) {
                if (dumpArray[i2] == null) {
                    i = size - i2;
                    break;
                }
            }
            i = -1;
        }
        return i;
    }
}
