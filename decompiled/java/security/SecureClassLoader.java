package java.security;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class SecureClassLoader extends ClassLoader {
    private HashMap<CodeSource, ProtectionDomain> pds;

    protected SecureClassLoader() {
        this.pds = new HashMap();
    }

    protected SecureClassLoader(ClassLoader parent) {
        super(parent);
        this.pds = new HashMap();
    }

    protected PermissionCollection getPermissions(CodeSource codesource) {
        return new Permissions();
    }

    protected final Class<?> defineClass(String name, byte[] b, int off, int len, CodeSource cs) {
        if (cs == null) {
            return defineClass(name, b, off, len);
        }
        return defineClass(name, b, off, len, getPD(cs));
    }

    protected final Class<?> defineClass(String name, ByteBuffer b, CodeSource cs) {
        byte[] data = b.array();
        if (cs == null) {
            return defineClass(name, data, 0, data.length);
        }
        return defineClass(name, data, 0, data.length, getPD(cs));
    }

    private ProtectionDomain getPD(CodeSource cs) {
        ProtectionDomain protectionDomain = null;
        if (cs != null) {
            synchronized (this.pds) {
                protectionDomain = (ProtectionDomain) this.pds.get(cs);
                if (protectionDomain != null) {
                } else {
                    protectionDomain = new ProtectionDomain(cs, getPermissions(cs), this, null);
                    this.pds.put(cs, protectionDomain);
                }
            }
        }
        return protectionDomain;
    }
}
