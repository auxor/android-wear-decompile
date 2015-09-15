package java.security;

public class ProtectionDomain {
    public ProtectionDomain(CodeSource cs, PermissionCollection permissions) {
    }

    public ProtectionDomain(CodeSource cs, PermissionCollection permissions, ClassLoader cl, Principal[] principals) {
    }

    public final ClassLoader getClassLoader() {
        return null;
    }

    public final CodeSource getCodeSource() {
        return null;
    }

    public final PermissionCollection getPermissions() {
        return null;
    }

    public final Principal[] getPrincipals() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
