package java.security;

import java.io.Serializable;

public abstract class Permission implements Guard, Serializable {
    public abstract String getActions();

    public abstract boolean implies(Permission permission);

    public Permission(String name) {
    }

    public final String getName() {
        return null;
    }

    public void checkGuard(Object obj) throws SecurityException {
    }

    public PermissionCollection newPermissionCollection() {
        return new AllPermissionCollection();
    }
}
