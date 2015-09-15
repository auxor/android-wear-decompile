package org.apache.harmony.security.fortress;

import java.security.Provider;
import java.security.Provider.Service;
import java.util.List;

public interface SecurityAccess {
    List<String> getAliases(Service service);

    Service getService(Provider provider, String str);

    void renumProviders();
}
