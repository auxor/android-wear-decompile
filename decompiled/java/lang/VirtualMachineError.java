package java.lang;

public abstract class VirtualMachineError extends Error {
    private static final long serialVersionUID = 4161983926571568670L;

    public VirtualMachineError(String detailMessage) {
        super(detailMessage);
    }
}
