package gov.nist.javax.sip.header;

import javax.sip.header.ProxyAuthorizationHeader;
import org.apache.http.auth.AUTH;

public class ProxyAuthorization extends AuthenticationHeader implements ProxyAuthorizationHeader {
    private static final long serialVersionUID = -6374966905199799098L;

    public ProxyAuthorization() {
        super(AUTH.PROXY_AUTH_RESP);
    }
}
