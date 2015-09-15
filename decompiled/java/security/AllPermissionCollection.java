package java.security;

import java.util.Enumeration;

final class AllPermissionCollection extends PermissionCollection {
    AllPermissionCollection() {
    }

    public void add(Permission permission) {
    }

    public Enumeration<Permission> elements() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
