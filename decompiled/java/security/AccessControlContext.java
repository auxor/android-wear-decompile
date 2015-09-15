package java.security;

public final class AccessControlContext {
    public AccessControlContext(AccessControlContext acc, DomainCombiner combiner) {
    }

    public AccessControlContext(ProtectionDomain[] context) {
    }

    public void checkPermission(Permission perm) throws AccessControlException {
    }

    public DomainCombiner getDomainCombiner() {
        return null;
    }
}
