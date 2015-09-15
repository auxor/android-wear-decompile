package com.android.okhttp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface HostResolver {
    public static final HostResolver DEFAULT;

    InetAddress[] getAllByName(String str) throws UnknownHostException;

    static {
        DEFAULT = new HostResolver() {
            public InetAddress[] getAllByName(String host) throws UnknownHostException {
                if (host != null) {
                    return InetAddress.getAllByName(host);
                }
                throw new UnknownHostException("host == null");
            }
        };
    }
}
