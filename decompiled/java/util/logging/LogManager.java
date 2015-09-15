package java.util.logging;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;

public class LogManager {
    public static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
    static LogManager manager;
    private static final LoggingPermission perm = null;
    private PropertyChangeSupport listeners;
    private Hashtable<String, Logger> loggers;
    private Properties props;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.logging.LogManager.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.logging.LogManager.<clinit>():void
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
        throw new UnsupportedOperationException("Method not decompiled: java.util.logging.LogManager.<clinit>():void");
    }

    public static LoggingMXBean getLoggingMXBean() {
        throw new UnsupportedOperationException();
    }

    protected LogManager() {
        this.loggers = new Hashtable();
        this.props = new Properties();
        this.listeners = new PropertyChangeSupport(this);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LogManager.this.reset();
            }
        });
    }

    public void checkAccess() {
    }

    public synchronized boolean addLogger(Logger logger) {
        boolean z;
        String name = logger.getName();
        if (this.loggers.get(name) != null) {
            z = false;
        } else {
            addToFamilyTree(logger, name);
            this.loggers.put(name, logger);
            logger.setManager(this);
            z = true;
        }
        return z;
    }

    private void addToFamilyTree(Logger logger, String name) {
        Logger parent = null;
        String parentName = name;
        do {
            int lastSeparator = parentName.lastIndexOf(46);
            if (lastSeparator == -1) {
                break;
            }
            parentName = parentName.substring(0, lastSeparator);
            parent = (Logger) this.loggers.get(parentName);
            if (parent == null) {
                if (getProperty(parentName + ".level") != null) {
                    break;
                }
            } else {
                setParent(logger, parent);
                break;
            }
        } while (getProperty(parentName + ".handlers") == null);
        parent = Logger.getLogger(parentName);
        setParent(logger, parent);
        if (parent == null) {
            parent = (Logger) this.loggers.get(XmlPullParser.NO_NAMESPACE);
            if (parent != null) {
                setParent(logger, parent);
            }
        }
        String nameDot = name + '.';
        for (Object child : this.loggers.values()) {
            Logger oldParent = child.getParent();
            if (parent == oldParent && (name.length() == 0 || child.getName().startsWith(nameDot))) {
                child.setParent(logger);
                if (oldParent != null) {
                    oldParent.children.remove(child);
                }
            }
        }
    }

    public synchronized Logger getLogger(String name) {
        return (Logger) this.loggers.get(name);
    }

    public synchronized Enumeration<String> getLoggerNames() {
        return this.loggers.keys();
    }

    public static LogManager getLogManager() {
        return manager;
    }

    public String getProperty(String name) {
        return this.props.getProperty(name);
    }

    public void readConfiguration() throws IOException {
        String configClassName = System.getProperty("java.util.logging.config.class");
        if (configClassName == null || getInstanceByClass(configClassName) == null) {
            String configFile = System.getProperty("java.util.logging.config.file");
            if (configFile == null) {
                configFile = System.getProperty("java.home") + File.separator + "lib" + File.separator + "logging.properties";
            }
            AutoCloseable input = null;
            try {
                input = new FileInputStream(configFile);
            } catch (IOException exception) {
                input = LogManager.class.getResourceAsStream("logging.properties");
                if (input == null) {
                    throw exception;
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly(input);
            }
            readConfiguration(new BufferedInputStream(input));
            IoUtils.closeQuietly(input);
        }
    }

    static Object getInstanceByClass(String className) {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
        } catch (Exception e) {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
            } catch (Object innerE) {
                System.err.println("Loading class '" + className + "' failed");
                System.err.println(innerE);
                return null;
            }
        }
    }

    private synchronized void readConfigurationImpl(InputStream ins) throws IOException {
        reset();
        this.props.load(ins);
        Logger root = (Logger) this.loggers.get(XmlPullParser.NO_NAMESPACE);
        if (root != null) {
            root.setManager(this);
        }
        String configs = this.props.getProperty("config");
        if (configs != null) {
            StringTokenizer st = new StringTokenizer(configs, " ");
            while (st.hasMoreTokens()) {
                getInstanceByClass(st.nextToken());
            }
        }
        for (Logger logger : this.loggers.values()) {
            String property = this.props.getProperty(logger.getName() + ".level");
            if (property != null) {
                logger.setLevel(Level.parse(property));
            }
        }
        this.listeners.firePropertyChange(null, null, null);
    }

    public void readConfiguration(InputStream ins) throws IOException {
        checkAccess();
        readConfigurationImpl(ins);
    }

    public synchronized void reset() {
        checkAccess();
        this.props = new Properties();
        Enumeration<String> names = getLoggerNames();
        while (names.hasMoreElements()) {
            Logger logger = getLogger((String) names.nextElement());
            if (logger != null) {
                logger.reset();
            }
        }
        Logger root = (Logger) this.loggers.get(XmlPullParser.NO_NAMESPACE);
        if (root != null) {
            root.setLevel(Level.INFO);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }
        checkAccess();
        this.listeners.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        checkAccess();
        this.listeners.removePropertyChangeListener(l);
    }

    synchronized Logger getOrCreate(String name, String resourceBundleName) {
        Logger result;
        result = getLogger(name);
        if (result == null) {
            result = new Logger(name, resourceBundleName);
            addLogger(result);
        }
        return result;
    }

    synchronized void setParent(Logger logger, Logger newParent) {
        logger.parent = newParent;
        if (logger.levelObjVal == null) {
            setLevelRecursively(logger, null);
        }
        newParent.children.add(logger);
        logger.updateDalvikLogHandler();
    }

    synchronized void setLevelRecursively(Logger logger, Level newLevel) {
        int previous = logger.levelIntVal;
        logger.levelObjVal = newLevel;
        if (newLevel == null) {
            logger.levelIntVal = logger.parent != null ? logger.parent.levelIntVal : Level.INFO.intValue();
        } else {
            logger.levelIntVal = newLevel.intValue();
        }
        if (previous != logger.levelIntVal) {
            for (Logger child : logger.children) {
                if (child.levelObjVal == null) {
                    setLevelRecursively(child, null);
                }
            }
        }
    }
}
