package java.util.logging;

import dalvik.system.VMStack;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.xml.datatype.DatatypeConstants;
import libcore.util.Objects;

public class Level implements Serializable {
    public static final Level ALL;
    public static final Level CONFIG;
    public static final Level FINE;
    public static final Level FINER;
    public static final Level FINEST;
    public static final Level INFO;
    public static final Level OFF;
    public static final Level SEVERE;
    public static final Level WARNING;
    private static final List<Level> levels;
    private static final long serialVersionUID = -8176160795706313070L;
    private final String name;
    private transient ResourceBundle rb;
    private final String resourceBundleName;
    private final int value;

    static {
        levels = new ArrayList(9);
        OFF = new Level("OFF", Integer.MAX_VALUE);
        SEVERE = new Level("SEVERE", Grego.MILLIS_PER_SECOND);
        WARNING = new Level("WARNING", 900);
        INFO = new Level("INFO", 800);
        CONFIG = new Level("CONFIG", 700);
        FINE = new Level("FINE", HttpURLConnection.HTTP_SERVER_ERROR);
        FINER = new Level("FINER", HttpURLConnection.HTTP_BAD_REQUEST);
        FINEST = new Level("FINEST", HttpURLConnection.HTTP_MULT_CHOICE);
        ALL = new Level("ALL", DatatypeConstants.FIELD_UNDEFINED);
    }

    public static Level parse(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        try {
            int nameAsInt = Integer.parseInt(name);
            boolean isNameAnInt = true;
        } catch (NumberFormatException e) {
            nameAsInt = 0;
            isNameAnInt = false;
        }
        synchronized (levels) {
            for (Level level : levels) {
                if (name.equals(level.getName())) {
                    return level;
                }
            }
            if (isNameAnInt) {
                for (Level level2 : levels) {
                    if (nameAsInt == level2.intValue()) {
                        return level2;
                    }
                }
            }
            if (isNameAnInt) {
                return new Level(name, nameAsInt);
            }
            throw new IllegalArgumentException("Cannot parse name '" + name + "'");
        }
    }

    protected Level(String name, int level) {
        this(name, level, null);
    }

    protected Level(String name, int level, String resourceBundleName) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        this.name = name;
        this.value = level;
        this.resourceBundleName = resourceBundleName;
        if (resourceBundleName != null) {
            try {
                this.rb = ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), VMStack.getCallingClassLoader());
            } catch (MissingResourceException e) {
                this.rb = null;
            }
        }
        synchronized (levels) {
            levels.add(this);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getResourceBundleName() {
        return this.resourceBundleName;
    }

    public final int intValue() {
        return this.value;
    }

    private Object readResolve() {
        synchronized (levels) {
            for (Level level : levels) {
                if (this.value == level.value && this.name.equals(level.name) && Objects.equal(this.resourceBundleName, level.resourceBundleName)) {
                    return level;
                }
            }
            levels.add(this);
            return this;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.resourceBundleName != null) {
            try {
                this.rb = ResourceBundle.getBundle(this.resourceBundleName);
            } catch (MissingResourceException e) {
                this.rb = null;
            }
        }
    }

    public String getLocalizedName() {
        if (this.rb == null) {
            return this.name;
        }
        try {
            return this.rb.getString(this.name);
        } catch (MissingResourceException e) {
            return this.name;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Level)) {
            return false;
        }
        if (((Level) o).intValue() != this.value) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.value;
    }

    public final String toString() {
        return this.name;
    }
}
