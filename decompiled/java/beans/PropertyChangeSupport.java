package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import libcore.util.Objects;

public class PropertyChangeSupport implements Serializable {
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 6401253773779951803L;
    private transient List<PropertyChangeListener> listeners;
    private transient Object sourceBean;

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("source", Object.class), new ObjectStreamField("children", Object.class), new ObjectStreamField("propertyChangeSupportSerializedDataVersion", Integer.TYPE)};
    }

    public PropertyChangeSupport(Object sourceBean) {
        this.listeners = new CopyOnWriteArrayList();
        if (sourceBean == null) {
            throw new NullPointerException("sourceBean == null");
        }
        this.sourceBean = sourceBean;
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        firePropertyChange(new PropertyChangeEvent(this.sourceBean, propertyName, oldValue, newValue));
    }

    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        firePropertyChange(new IndexedPropertyChangeEvent(this.sourceBean, propertyName, oldValue, newValue, index));
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (Object p : this.listeners) {
            if (equals(propertyName, listener, p)) {
                this.listeners.remove(p);
                return;
            }
        }
    }

    private boolean equals(String aName, EventListener a, EventListener b) {
        while (b instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy bProxy = (PropertyChangeListenerProxy) b;
            String bName = bProxy.getPropertyName();
            b = bProxy.getListener();
            if (aName == null) {
                if (!(a instanceof PropertyChangeListenerProxy)) {
                    return false;
                }
                PropertyChangeListenerProxy aProxy = (PropertyChangeListenerProxy) a;
                aName = aProxy.getPropertyName();
                a = aProxy.getListener();
            }
            if (!Objects.equal(aName, bName)) {
                return false;
            }
            aName = null;
        }
        if (aName == null && Objects.equal(a, b)) {
            return true;
        }
        return false;
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener != null && propertyName != null) {
            this.listeners.add(new PropertyChangeListenerProxy(propertyName, listener));
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        List<PropertyChangeListener> result = new ArrayList();
        for (PropertyChangeListener p : this.listeners) {
            if ((p instanceof PropertyChangeListenerProxy) && Objects.equal(propertyName, ((PropertyChangeListenerProxy) p).getPropertyName())) {
                result.add(p);
            }
        }
        return (PropertyChangeListener[]) result.toArray(new PropertyChangeListener[result.size()]);
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
    }

    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    public boolean hasListeners(String propertyName) {
        for (PropertyChangeListener p : this.listeners) {
            if (p instanceof PropertyChangeListenerProxy) {
                if (Objects.equal(propertyName, ((PropertyChangeListenerProxy) p).getPropertyName())) {
                }
            }
            return true;
        }
        return false;
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        for (Object p : this.listeners) {
            if (equals(null, listener, p)) {
                this.listeners.remove(p);
                return;
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return (PropertyChangeListener[]) this.listeners.toArray(new PropertyChangeListener[0]);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Object map = new Hashtable();
        for (PropertyChangeListener p : this.listeners) {
            if ((p instanceof PropertyChangeListenerProxy) && !(p instanceof Serializable)) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) p;
                PropertyChangeListener listener = (PropertyChangeListener) proxy.getListener();
                if (listener instanceof Serializable) {
                    PropertyChangeSupport list = (PropertyChangeSupport) map.get(proxy.getPropertyName());
                    if (list == null) {
                        list = new PropertyChangeSupport(this.sourceBean);
                        map.put(proxy.getPropertyName(), list);
                    }
                    list.listeners.add(listener);
                }
            }
        }
        PutField putFields = out.putFields();
        putFields.put("source", this.sourceBean);
        putFields.put("children", map);
        out.writeFields();
        for (PropertyChangeListener p2 : this.listeners) {
            if (p2 instanceof Serializable) {
                out.writeObject(p2);
            }
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GetField readFields = in.readFields();
        this.sourceBean = readFields.get("source", null);
        this.listeners = new CopyOnWriteArrayList();
        Map<String, PropertyChangeSupport> children = (Map) readFields.get("children", null);
        if (children != null) {
            for (Entry<String, PropertyChangeSupport> entry : children.entrySet()) {
                for (PropertyChangeListener p : ((PropertyChangeSupport) entry.getValue()).listeners) {
                    this.listeners.add(new PropertyChangeListenerProxy((String) entry.getKey(), p));
                }
            }
        }
        while (true) {
            PropertyChangeListener listener = (PropertyChangeListener) in.readObject();
            if (listener != null) {
                this.listeners.add(listener);
            } else {
                return;
            }
        }
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue == null || oldValue == null || !newValue.equals(oldValue)) {
            for (PropertyChangeListener p : this.listeners) {
                PropertyChangeListener p2;
                while (p2 instanceof PropertyChangeListenerProxy) {
                    PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) p2;
                    if (!Objects.equal(proxy.getPropertyName(), propertyName)) {
                        break;
                    }
                    p2 = (PropertyChangeListener) proxy.getListener();
                }
                p2.propertyChange(event);
            }
        }
    }
}
