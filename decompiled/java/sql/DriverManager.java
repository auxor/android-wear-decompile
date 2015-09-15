package java.sql;

import dalvik.system.VMStack;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DriverManager {
    private static final SQLPermission logPermission;
    private static int loginTimeout;
    private static final List<Driver> theDrivers;
    private static PrintStream thePrintStream;
    private static PrintWriter thePrintWriter;

    public static java.sql.Connection getConnection(java.lang.String r1, java.lang.String r2, java.lang.String r3) throws java.sql.SQLException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.sql.DriverManager.getConnection(java.lang.String, java.lang.String, java.lang.String):java.sql.Connection
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.sql.DriverManager.getConnection(java.lang.String, java.lang.String, java.lang.String):java.sql.Connection
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.sql.DriverManager.getConnection(java.lang.String, java.lang.String, java.lang.String):java.sql.Connection");
    }

    public static java.util.Enumeration<java.sql.Driver> getDrivers() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.sql.DriverManager.getDrivers():java.util.Enumeration<java.sql.Driver>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.sql.DriverManager.getDrivers():java.util.Enumeration<java.sql.Driver>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.sql.DriverManager.getDrivers():java.util.Enumeration<java.sql.Driver>");
    }

    private static boolean isClassFromClassLoader(java.lang.Object r1, java.lang.ClassLoader r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.sql.DriverManager.isClassFromClassLoader(java.lang.Object, java.lang.ClassLoader):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.sql.DriverManager.isClassFromClassLoader(java.lang.Object, java.lang.ClassLoader):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.sql.DriverManager.isClassFromClassLoader(java.lang.Object, java.lang.ClassLoader):boolean");
    }

    private static void loadInitialDrivers() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.sql.DriverManager.loadInitialDrivers():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.sql.DriverManager.loadInitialDrivers():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.sql.DriverManager.loadInitialDrivers():void");
    }

    public static void println(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.sql.DriverManager.println(java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.sql.DriverManager.println(java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.sql.DriverManager.println(java.lang.String):void");
    }

    static {
        loginTimeout = 0;
        theDrivers = new ArrayList(10);
        logPermission = new SQLPermission("setLog");
        loadInitialDrivers();
    }

    private DriverManager() {
    }

    public static void deregisterDriver(Driver driver) throws SQLException {
        if (driver != null) {
            if (isClassFromClassLoader(driver, VMStack.getCallingClassLoader())) {
                synchronized (theDrivers) {
                    theDrivers.remove((Object) driver);
                }
                return;
            }
            throw new SecurityException("calling class not authorized to deregister JDBC driver");
        }
    }

    public static Connection getConnection(String url) throws SQLException {
        return getConnection(url, new Properties());
    }

    public static Connection getConnection(String url, Properties info) throws SQLException {
        String sqlState = "08001";
        if (url == null) {
            throw new SQLException("The url cannot be null", sqlState);
        }
        synchronized (theDrivers) {
            for (Driver theDriver : theDrivers) {
                Connection theConnection = theDriver.connect(url, info);
                if (theConnection != null) {
                    return theConnection;
                }
            }
            throw new SQLException("No suitable driver", sqlState);
        }
    }

    public static Driver getDriver(String url) throws SQLException {
        ClassLoader callerClassLoader = VMStack.getCallingClassLoader();
        synchronized (theDrivers) {
            for (Driver driver : theDrivers) {
                if (driver.acceptsURL(url) && isClassFromClassLoader(driver, callerClassLoader)) {
                    return driver;
                }
            }
            throw new SQLException("No suitable driver", "08001");
        }
    }

    public static int getLoginTimeout() {
        return loginTimeout;
    }

    @Deprecated
    public static PrintStream getLogStream() {
        return thePrintStream;
    }

    public static PrintWriter getLogWriter() {
        return thePrintWriter;
    }

    public static void registerDriver(Driver driver) throws SQLException {
        if (driver == null) {
            throw new NullPointerException("driver == null");
        }
        synchronized (theDrivers) {
            theDrivers.add(driver);
        }
    }

    public static void setLoginTimeout(int seconds) {
        loginTimeout = seconds;
    }

    @Deprecated
    public static void setLogStream(PrintStream out) {
        thePrintStream = out;
    }

    public static void setLogWriter(PrintWriter out) {
        thePrintWriter = out;
    }
}
