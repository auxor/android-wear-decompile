package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DigestInputStream extends FilterInputStream {
    protected MessageDigest digest;
    private boolean isOn;

    public DigestInputStream(InputStream stream, MessageDigest digest) {
        super(stream);
        this.isOn = true;
        this.digest = digest;
    }

    public MessageDigest getMessageDigest() {
        return this.digest;
    }

    public void setMessageDigest(MessageDigest digest) {
        this.digest = digest;
    }

    public int read() throws IOException {
        int byteRead = this.in.read();
        if (this.isOn && byteRead != -1) {
            this.digest.update((byte) byteRead);
        }
        return byteRead;
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int bytesRead = this.in.read(buffer, byteOffset, byteCount);
        if (this.isOn && bytesRead != -1) {
            this.digest.update(buffer, byteOffset, bytesRead);
        }
        return bytesRead;
    }

    public void on(boolean on) {
        this.isOn = on;
    }

    public String toString() {
        return super.toString() + ", " + this.digest.toString() + (this.isOn ? ", is on" : ", is off");
    }
}
